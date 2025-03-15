package tv.utao.x5;

import android.content.res.Configuration;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.Toast;

import tv.utao.x5.impl.WebViewClientImpl;

public class MainActivity extends BaseWebViewActivity {
    private long mClickBackTime = 0;
    public boolean dispatchTouchEvent(MotionEvent event) {
        if(event.getAction() == KeyEvent.ACTION_DOWN){
            float x= event.getX();
            float y= event.getY();
            //Log.i("dispatchTouchEvent", "x" + x+"y "+y);
            if(x<100f&&y<100f) {
                ctrl("menu");
            }
        }
        return super.dispatchTouchEvent(event);
    }

    private boolean ctrl(String code){
        String  js= "_menuCtrl."+code+"()";
        Log.i(TAG,js);
        mWebView.evaluateJavascript(js,null);
        return true;
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        Log.i(TAG,"onConfigurationChanged...."+newConfig.orientation);
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
        Log.i("keyDown keyCode ", keyCode+" event" + event);
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
        String url = WebViewClientImpl.backUrl();
        Log.i("keyBack","keyBack "+url);
        //NextPlusNavigationDelegate.backUrl();
        if(null==url){
            long currentTime = System.currentTimeMillis();
            if (currentTime - mClickBackTime < 3000) {
                //killAppProcess();
                 finish();
                //super.onBackPressed();
                //System.exit(0);
            } else {
                Toast.makeText(this, "再按一次返回键退出", Toast.LENGTH_SHORT).show();
                mClickBackTime = currentTime;
            }
        }else{
            mWebView.loadUrl(url);
        }
        //detail-> home-> index
        return true;
    }

    @Override
    public void onDestroy() {
        if(mWebView!=null){
            Log.i(TAG,"onDestroy");
            mWebView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            mWebView.destroy();
        }
        super.onDestroy();
    }



}
