package tv.utao.x5;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;

import com.tencent.smtt.export.external.extension.interfaces.IX5WebSettingsExtension;
import com.tencent.smtt.export.external.interfaces.IX5WebChromeClient;
import com.tencent.smtt.export.external.interfaces.PermissionRequest;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;

import java.util.Arrays;

import tv.utao.x5.databinding.ActivityDouyinBinding;
import tv.utao.x5.impl.WebViewClientImpl;
import tv.utao.x5.impl.X5WebChromeClientExtension;
import tv.utao.x5.util.LogUtil;
import tv.utao.x5.utils.ToastUtils;

public class DouyinActivity extends Activity {
    protected String TAG = "DouyinActivity";
    private Context thisContext;
    protected WebView lWebView;
    protected ActivityDouyinBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//隐藏标题栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        // 强制横屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_douyin);
        bind();
        thisContext=this;
        //更新数据
        initWebView();
        lWebView.requestFocus();
        binding.webviewWrapper.requestFocus();
        //数据库获取最新数据
        lWebView.loadUrl("https://www.douyin.com/?recommend=1");
        ToastUtils.show(this,"已支持遥控器上下可快速切台",Toast.LENGTH_SHORT);
        // 或者如果使用旧的 ActionBar
        if (getActionBar() != null) {
            getActionBar().hide();
        }
    }
    private void bind(){
        binding = DataBindingUtil.setContentView(this, R.layout.activity_douyin);
        //binding.setMenuTitleHandler(new BaseWebViewActivity.MenuTitleHandler());
        ViewGroup container = binding.webviewWrapper;
        lWebView = new com.tencent.smtt.sdk.WebView(this);
        container.addView(lWebView, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        // lWebView=binding.webView;
        //focusChange();
    }
    protected void initWebView() {
        WebSettings webSetting = lWebView.getSettings();
        webSetting.setJavaScriptEnabled(true);
        webSetting.setAllowFileAccess(true);
        webSetting.setDatabaseEnabled(true);
        webSetting.setDomStorageEnabled(true);
        //webSetting.setNeedInitialFocus(false);
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
            LogUtil.i(TAG,"isX5 webSettingsExtension");
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
        lWebView.setWebViewClient(new WebViewClientImpl(getBaseContext(),lWebView,2));
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
        //lWebView.addJavascriptInterface(new LiveActivity.JsInterface(),"_api");
    }
    private void initWebChromeClient() {
        lWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onShowCustomView(View view, IX5WebChromeClient.CustomViewCallback callback) {
                LogUtil.i("WebChromeClient","onShowCustomView");
                binding.fullscreen.addView(view);
                binding.fullscreen.setVisibility(View.VISIBLE);
            }
            @Override
            public void onPermissionRequest(PermissionRequest request) {
                LogUtil.i("WebChromeClient","onPermissionRequest "+request.getOrigin());
                LogUtil.i("WebChromeClient",request.getOrigin()+" "+ Arrays.toString(request.getResources()));
                request.deny();
            }
            @Override
            public void onHideCustomView() {
                LogUtil.i("WebChromeClient","onHideCustomView");
                binding.fullscreen.removeAllViews();
                binding.fullscreen.setVisibility(View.GONE);
            }
        });
        lWebView.setWebChromeClientExtension(new X5WebChromeClientExtension());
    }
}