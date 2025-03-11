package tv.utao.x5;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.google.gson.reflect.TypeToken;
import com.tencent.smtt.export.external.extension.interfaces.IX5WebSettingsExtension;
import com.tencent.smtt.export.external.interfaces.IX5WebChromeClient;
import com.tencent.smtt.export.external.interfaces.PermissionRequest;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import tv.utao.x5.call.StringCallback;
import tv.utao.x5.dao.HistoryDaoX;
import tv.utao.x5.databinding.ActivityLiveBinding;
import tv.utao.x5.domain.live.Vod;
import tv.utao.x5.impl.WebViewClientImpl;
import tv.utao.x5.impl.X5WebChromeClientExtension;
import tv.utao.x5.service.UpdateService;
import tv.utao.x5.util.FileUtil;
import tv.utao.x5.util.HttpUtil;
import tv.utao.x5.util.JsonUtil;
import tv.utao.x5.util.Util;

public class LiveActivity extends Activity {
    protected String TAG = "LiveActivity";
    protected WebView lWebView;
    protected ActivityLiveBinding binding;
    private  Context thisContext;
    private static Vod currentLive=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//隐藏标题栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        // 强制横屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_live);
        bind();
        UpdateService.baseFolder= this.getFilesDir().getPath();
        UpdateService.initTvData();
        thisContext=this;
        if(null==currentLive){
            currentLive = HistoryDaoX.currentChannel(this);
                    //UpdateService.getByKey("0_0");
        }
        //更新数据
        initWebView();
        //数据库获取最新数据
        //String liveUrl= "https://tv.cctv.com/live/cctv13/";
        lWebView.loadUrl(currentLive.getUrl());
        showToastOrg("已支持遥控器上下左右可快速切台",this);
    }
    private long lastTime = 0;

    // 在 Handler 对象中处理消息
   private Handler  handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    String messageContent = (String) msg.obj;
                    // 处理接收到的消息（例如，显示 Toast）
                    if(currentLive.getKey().equals(messageContent)){
                        lWebView.loadUrl(currentLive.getUrl());
                        //记录到db
                    }
                    break;
                case 2:
                    binding.liveName.setText("");
                    break;
            }
        }
    };
    /*
    说明：我们使用 obtainMessage 方法创建消息，并将消息的内容（字符串）作为第二个参数。
    */
    private boolean goNext(String nextType){
        if(null==currentLive){
            currentLive = UpdateService.getByKey("0_0");
        }
        String key= UpdateService.liveNext(currentLive.getTagIndex(),currentLive.getDetailIndex(),nextType);
        currentLive = UpdateService.getByKey(key);
        if(null!=currentLive){
            //延迟1s
            showToast(currentLive.getName(),this);
            String liveKey=currentLive.getKey();
            handler.sendMessageDelayed (handler.obtainMessage(1, liveKey),1000);

        }
        return true;
    }
    protected   void showToast(String text, Context context){
        binding.liveName.setText(text);
        //showToastOrg(text,context);
    }
    private static Toast toast;
    protected  void showToastOrg(String text, Context context){
        if(toast==null){
            toast = Toast.makeText(context, text,Toast.LENGTH_SHORT);
        }else {
            toast.setText(text);//如果不为空，则直接改变当前toast的文本
        }
        toast.show();
    }

    public boolean dispatchTouchEvent(MotionEvent event) {
        if(!isMenuShow&&event.getAction() == KeyEvent.ACTION_DOWN){
            ctrl("menu");
            return true;
        }
        return super.dispatchTouchEvent(event);
    }
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_UP) {
            return super.dispatchKeyEvent(event);
        }
        int keyCode = event.getKeyCode();
        Log.i("keyDown keyCode ", keyCode+" event" + event);
        if(isMenuShow){
            if(keyCode==KeyEvent.KEYCODE_DPAD_CENTER||keyCode==KeyEvent.KEYCODE_ENTER){
                return ctrl("ok");
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
            if(keyCode==KeyEvent.KEYCODE_MENU||keyCode==KeyEvent.KEYCODE_TAB||keyCode==KeyEvent.KEYCODE_BACK){
                return ctrl("menu");
            }
            if(keyCode==KeyEvent.KEYCODE_DPAD_UP){
                return ctrl("up");
            }
            return super.dispatchKeyEvent(event);
        }
        if(keyCode==KeyEvent.KEYCODE_DPAD_CENTER||keyCode==KeyEvent.KEYCODE_ENTER||keyCode==KeyEvent.KEYCODE_MENU||keyCode==KeyEvent.KEYCODE_TAB){
            return ctrl("menu");
            //showMenu();
           // return true;
        }
        if(keyCode==KeyEvent.KEYCODE_DPAD_RIGHT){
            return goNext("right");
        }
        if(keyCode==KeyEvent.KEYCODE_DPAD_LEFT){
            return goNext("left");
        }
        if(keyCode==KeyEvent.KEYCODE_DPAD_DOWN){
            return goNext("down");
        }
        if(keyCode==KeyEvent.KEYCODE_DPAD_UP){
            return goNext("up");
        }
        if(keyCode==KeyEvent.KEYCODE_BACK){
            toHome();
            return true;
        }
        return super.dispatchKeyEvent(event);
                //ctrl("menu");
    }
    private void toHome(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
    private boolean ctrl(String code){
        String  js= "_menuCtrl."+code+"()";
        Log.i(TAG,js);
        lWebView.evaluateJavascript(js,null);
        return true;
    }
    private void bind(){
        binding = DataBindingUtil.setContentView(this, R.layout.activity_live);
        //binding.setMenuTitleHandler(new BaseWebViewActivity.MenuTitleHandler());
        lWebView=binding.webView;
        //focusChange();
    }
    protected void initWebView() {
        WebSettings webSetting = lWebView.getSettings();
        webSetting.setJavaScriptEnabled(true);
        webSetting.setAllowFileAccess(true);
        webSetting.setDatabaseEnabled(true);
        webSetting.setDomStorageEnabled(true);
        webSetting.setNeedInitialFocus(false);
        // 禁用缩放
        webSetting.setSupportZoom(false);
        webSetting.setBuiltInZoomControls(false);
        webSetting.setDisplayZoomControls(false);
        //自适应屏幕
        webSetting.setUseWideViewPort(true);
        //webSetting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webSetting.setLoadWithOverviewMode(true);
        webSetting.setMixedContentMode(WebSettings.LOAD_NORMAL);
        //app cache
        //webSetting.setAppCacheEnabled(true);
        //自动播放
        webSetting.setMediaPlaybackRequiresUserGesture(false);
        String userAgent=webSetting.getUserAgentString();
        //"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Safari/537.36"
        webSetting.setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.0.0 Safari/537.36");
        //webSetting.setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Safari/537.36");
        //normal?
        webSetting.setCacheMode(WebSettings.LOAD_DEFAULT);
        webSetting.setJavaScriptCanOpenWindowsAutomatically(false);
        webSetting.setGeolocationEnabled(false);
        //无图
        webSetting.setBlockNetworkImage(true);
        // 在WebView的初始化代码中启用缓存
        IX5WebSettingsExtension webSettingsExtension=  lWebView.getSettingsExtension();
        if(null!=webSettingsExtension){
            Log.i(TAG,"isX5 webSettingsExtension");
            //webSettingsExtension.setDayOrNight(false);
            //webSettingsExtension.setFitScreen(true);//会乱适配 // webSettingsExtension.setSmartFullScreenEnabled(true);
            webSettingsExtension.setAcceptCookie(true);
            webSettingsExtension.setWebViewInBackground(true);
            webSettingsExtension.setForcePinchScaleEnabled(false);//缩放
            //webSettingsExtension.setUseQProxy(true);
            // webSettingsExtension.setHttpDnsDomains(Arrays.asList("dns.alidns.com"));
            //无图
             webSettingsExtension.setPicModel(IX5WebSettingsExtension.PicModel_NoPic);
        }
        lWebView.setWebViewClient(new WebViewClientImpl(getBaseContext(),lWebView,1));
        initWebChromeClient();
        //禁止上下左右滚动(不显示滚动条)
        lWebView.setScrollContainer(false);
        lWebView.setVerticalScrollBarEnabled(false);
        lWebView.setHorizontalScrollBarEnabled(false);

        //远程调试
        WebView.setWebContentsDebuggingEnabled(true);
        // mWebView.setFocusable(false);
        //mWebView.setFocusableInTouchMode(false);
        //硬件加速 android 4.X 有问题
        //mWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        //mWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        lWebView.addJavascriptInterface(new JsInterface(),"_api");
    }
  /*  @Override
    public void onPause() {
        super.onPause();  // Always call the superclass method first
        toHome();
    }*/
    @Override
    public void onDestroy() {
        if(lWebView!=null){
            Log.i(TAG,"onDestroy");
            lWebView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            lWebView.clearHistory();
            lWebView.destroy();
        }
        super.onDestroy();
    }

    private void initWebChromeClient() {
        lWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                isMenuShow=false;
                String url= view.getUrl();
                try {
                    url=  URLDecoder.decode(url, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                }
                Log.i(TAG,"onProgressChangedX"+url);
                Vod vod = UpdateService.getByUrl(url);
                currentLive=vod;
                binding.liveName.setText(currentLive.getName()+" "+newProgress+"%");
                if(newProgress==100){
                    HistoryDaoX.updateChannel(thisContext,url);
                    handler.sendMessageDelayed (handler.obtainMessage(2, "noText"),1000);
                }
                Log.i("WebChromeClient", "onProgressChanged, newProgress:" + newProgress + ", view:" + view);
            }
            @Override
            public void onShowCustomView(View view, IX5WebChromeClient.CustomViewCallback callback) {
                Log.i("WebChromeClient","onShowCustomView");
                //binding.fullscreen.addView(view);
                //binding.fullscreen.setVisibility(View.VISIBLE);
            }
            @Override
            public void onPermissionRequest(PermissionRequest request) {
                Log.i("WebChromeClient","onPermissionRequest "+request.getOrigin());
                Log.i("WebChromeClient",request.getOrigin()+" "+ Arrays.toString(request.getResources()));
                request.deny();
            }
            @Override
            public void onHideCustomView() {
                Log.i("WebChromeClient","onHideCustomView");
                //binding.fullscreen.removeAllViews();
                //binding.fullscreen.setVisibility(View.GONE);
            }
        });
        lWebView.setWebChromeClientExtension(new X5WebChromeClientExtension());
    }

    private static boolean isMenuShow=false;
    public class JsInterface{

        // Android 调用 Js 方法1 中的返回值
        @JavascriptInterface
        public void toast(String message){
            Log.i(TAG,"message "+message);
            Toast.makeText(MyApplication.getContext(),message, Toast.LENGTH_SHORT)
                    .show();
        }
        @JavascriptInterface
        public void message(String service,String data){
            Log.i(TAG,"service "+service+" data "+data);
            if("history.save".equals(service)){
                //final AppDatabase db = AppDatabase.getInstance(this);
                HistoryDaoX.save(thisContext, data, new StringCallback() {
                    @Override
                    public void data(String data) {
                        runOnUiThread(()->{
                            lWebView.loadUrl(data);
                        });
                    }
                });
                return;
            }
            if("history.update".equals(service)){
                HistoryDaoX.update(thisContext,data);
                return;
            }
            if("menuShow".equals(service)){
                //Util.evalOnUi(lWebView,data);
                if(data.equals("1")){
                    isMenuShow =true;
                }else{
                    isMenuShow =false;
                }
                return;
            }
            if("js".equals(service)){
                Util.evalOnUi(lWebView,data);
                return;
            }
            if("key".equals(service)){
                keyCodeAllByCode(data);
                return;
            }
            if("keyNum".equals(service)){
                keyEventAll(Integer.parseInt(data));
                return;
            }
        }
        @JavascriptInterface
        public String postJson(String url,String header, String requestBody){

            Map<String, String> headerMap= JsonUtil.fromJson(header,
                    new TypeToken<Map<String, String>>() {}.getType());
            if(!url.startsWith("http")){
                return FileUtil.readExt("tv-web/"+url);
            }
            Log.i(TAG,headerMap.toString()+"url "+url+" "+requestBody);
            return HttpUtil.postJson(url,headerMap,requestBody);
        }
        @JavascriptInterface
        public String getJson(String url,String header){
            Map<String, String> headerMap= JsonUtil.fromJson(header,
                    new TypeToken<Map<String, String>>() {}.getType());
            if(!url.startsWith("http")){
                return FileUtil.readExt("tv-web/"+url);
            }
            Log.i(TAG,headerMap.toString()+"url "+url);
            return HttpUtil.getJson(url,headerMap);
        }
        @JavascriptInterface
        public String getHtml(String url,String header){
            Map<String, String> headerMap= JsonUtil.fromJson(header,
                    new TypeToken<Map<String, String>>() {}.getType());
            Log.i(TAG,headerMap.toString()+" getHtml "+url);
            return HttpUtil.getJson(url,headerMap);
        }

    }


    //key event
    private static Instrumentation inst = new Instrumentation();
    private static Map<String,Integer> keyCodeMap=new HashMap<>();
    static {
        keyCodeMap.put("SPACE",62);
        keyCodeMap.put("F",34);
    }
    protected void keyCodeAllByCode(String keyCode){
        Integer keyCodeNum=  keyCodeMap.get(keyCode);
        if(null==keyCodeNum){return;}
        Log.i("onKeyEvent", "keyCodeStr "+keyCode);
        keyEventAll(keyCodeNum);
    }
    protected void keyEventAll(final int keyCode){
        new Thread() {
            public void run() {
                try {
                    Log.i("onKeyEvent", "onKeyEvent"+keyCode);
                    inst.sendKeySync(new KeyEvent(KeyEvent.ACTION_DOWN, keyCode));
                    inst.sendKeySync(new KeyEvent(KeyEvent.ACTION_UP, keyCode));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

}