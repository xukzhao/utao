package tv.utao.x5;

import android.content.Intent;
import android.content.res.Configuration;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;

import tv.utao.x5.databinding.DialogExitBinding;
import tv.utao.x5.impl.WebViewClientImpl;
import tv.utao.x5.util.LogUtil;
import tv.utao.x5.util.ValueUtil;
import tv.utao.x5.utils.ToastUtils;

public class MainActivity extends BaseWebViewActivity {
    private long mClickBackTime = 0;
    private DialogExitBinding exitDialogBinding;
    private boolean isExitDialogShowing = false;
    public boolean dispatchTouchEvent(MotionEvent event) {
        if(event.getAction() == KeyEvent.ACTION_DOWN){
            float x= event.getX();
            float y= event.getY();
            //LogUtil.i("dispatchTouchEvent", "x" + x+"y "+y);
            if(x<100f&&y<100f) {
                ctrl("menu");
            }
        }
        return super.dispatchTouchEvent(event);
    }

    private boolean ctrl(String code){
        if (mWebView != null) {
            String  js= "_menuCtrl."+code+"()";
            LogUtil.i(TAG,js);
            mWebView.evaluateJavascript(js,null);
        }
        return true;
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        LogUtil.i(TAG,"onConfigurationChanged...."+newConfig.orientation);
        super.onConfigurationChanged(newConfig);
        // 检查屏幕方向是否改变
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // 在这里处理横屏模式下的布局调整
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            // 在这里处理竖屏模式下的布局调整
        }
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_UP) {
            return super.dispatchKeyEvent(event);
        }
        int keyCode = event.getKeyCode();
        LogUtil.i("keyDown keyCode ", keyCode+" event" + event);
        
        // 优先处理退出对话框
        if(isExitDialogShowing){
            if(keyCode==KeyEvent.KEYCODE_BACK){
                finish();
                return true;
            }
            // 退出对话框显示时，让系统处理上下键焦点切换和确认键
            if(keyCode==KeyEvent.KEYCODE_DPAD_UP || keyCode==KeyEvent.KEYCODE_DPAD_DOWN || 
               keyCode==KeyEvent.KEYCODE_DPAD_CENTER || keyCode==KeyEvent.KEYCODE_ENTER){
                return super.dispatchKeyEvent(event);
            }
            // 其他按键不处理
            return true;
        }
        
        boolean isMenuShow=isMenuShow();
        if(isMenuShow){
            if(keyCode==KeyEvent.KEYCODE_BACK||keyCode==KeyEvent.KEYCODE_MENU||keyCode==KeyEvent.KEYCODE_TAB){
                hideMenu();
                return true;
            }
            return super.dispatchKeyEvent(event);
        }
        if(keyCode==KeyEvent.KEYCODE_BACK){
            return keyBack();
        }
        if(keyCode==KeyEvent.KEYCODE_DPAD_CENTER||keyCode==KeyEvent.KEYCODE_ENTER){
            if(openOkMenu()&&!WebViewClientImpl.currentUrlIsHome()){
                return ctrl("menu");
            }
            return ctrl("ok");
           // return super.dispatchKeyEvent(event);
        }
        if(keyCode==KeyEvent.KEYCODE_MENU||keyCode==KeyEvent.KEYCODE_TAB){
            return ctrl("menu");
        }
        if(keyCode==KeyEvent.KEYCODE_DPAD_RIGHT){
            return ctrl("right");
        }
        if(keyCode==KeyEvent.KEYCODE_DPAD_LEFT){
            return ctrl("left");
        }
        if(keyCode==KeyEvent.KEYCODE_DPAD_DOWN){
            return ctrl("down");
        }
        if(keyCode==KeyEvent.KEYCODE_DPAD_UP){
            return ctrl("up");
        }
        if(keyCode==KeyEvent.KEYCODE_VOLUME_UP||keyCode==KeyEvent.KEYCODE_VOLUME_DOWN
                ||keyCode==KeyEvent.KEYCODE_VOLUME_MUTE){
            return super.dispatchKeyEvent(event);
        }
       // return ctrl("menu");
        return super.dispatchKeyEvent(event);
    }
    private boolean keyBack(){
        // 如果退出对话框已显示，再次按返回键则退出
        if (isExitDialogShowing) {
            finish();
            return true;
        }
        
        String url = WebViewClientImpl.backUrl();
        LogUtil.i("keyBack","keyBack "+url);
        //NextPlusNavigationDelegate.backUrl();
        if(null!=url&&null!=mWebView){
            mWebView.loadUrl(url);
            return true;
        }
        
        // 显示退出对话框
        showExitDialog();
        return true;
    }
    
    private void showExitDialog() {
        if (exitDialogBinding == null) {
            initExitDialog();
        }
        isExitDialogShowing = true;
        exitDialogBinding.exitDialogContainer.setVisibility(View.VISIBLE);
        
        // 设置对话框中按钮的焦点
        exitDialogBinding.btnExit.setFocusable(true);
        exitDialogBinding.btnCancel.setFocusable(true);
        exitDialogBinding.btnStartToggle.setFocusable(true);
        
        // 设置切换按钮的文本
        String currentStartPage = ValueUtil.getString(this, "startPage", "main");
        exitDialogBinding.btnStartToggle.setText("切换到" + ("main".equals(currentStartPage) ? "电视直播" : "视频点播"));
        
        // 默认焦点在退出按钮上
        exitDialogBinding.btnExit.requestFocus();
        
        updateStartPageHint();
    }
    
    private void hideExitDialog() {
        if (exitDialogBinding != null) {
            isExitDialogShowing = false;
            exitDialogBinding.exitDialogContainer.setVisibility(View.GONE);
        }
    }
    
    private void initExitDialog() {
        View dialogView = findViewById(R.id.exitDialog);
        exitDialogBinding = DataBindingUtil.bind(dialogView);
        
        if (exitDialogBinding == null) {
            return;
        }
        
        // 退出按钮
        exitDialogBinding.btnExit.setOnClickListener(v -> {
            finish();
        });
        
        // 取消按钮
        exitDialogBinding.btnCancel.setOnClickListener(v -> {
            hideExitDialog();
        });
        
        // 点击背景关闭对话框
        exitDialogBinding.dialogBackdrop.setOnClickListener(v -> {
            hideExitDialog();
        });
        
        // 启动首页切换按钮
        exitDialogBinding.btnStartToggle.setOnClickListener(v -> {
            String currentStartPage = ValueUtil.getString(this, "startPage", "main");
            if ("main".equals(currentStartPage)) {
                // 当前是视频点播，切换到电视直播
                ValueUtil.putString(this, "startPage", "live");
                ToastUtils.show(this, "已设置启动首页为：电视直播", Toast.LENGTH_SHORT);
                exitDialogBinding.btnStartToggle.setText("切换到视频点播");
            } else {
                // 当前是电视直播，切换到视频点播
                ValueUtil.putString(this, "startPage", "main");
                ToastUtils.show(this, "已设置启动首页为：视频点播", Toast.LENGTH_SHORT);
                exitDialogBinding.btnStartToggle.setText("切换到电视直播");
            }
            updateStartPageHint();
        });
    }
    
    private void updateStartPageHint() {
        if (exitDialogBinding == null) {
            return;
        }
        String startPage = ValueUtil.getString(this, "startPage", "main");
        if ("live".equals(startPage)) {
            exitDialogBinding.tvStartPageHint.setText("当前启动首页：电视直播");
        } else {
            exitDialogBinding.tvStartPageHint.setText("当前启动首页：视频点播");
        }
    }




}
