package tv.utao.x5;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tv.utao.x5.call.StringCallback;
import tv.utao.x5.dao.HistoryDaoX;
import tv.utao.x5.databinding.ActivityLiveBinding;
import tv.utao.x5.domain.live.Live;
import tv.utao.x5.domain.live.Vod;
import tv.utao.x5.impl.WebViewClientImpl;
import tv.utao.x5.impl.X5WebChromeClientExtension;
import tv.utao.x5.service.UpdateService;
import tv.utao.x5.util.FileUtil;
import tv.utao.x5.util.HttpUtil;
import tv.utao.x5.util.JsonUtil;
import tv.utao.x5.util.LogUtil;
import tv.utao.x5.util.Util;
import tv.utao.x5.utils.ToastUtils;

public class LiveActivity extends Activity {
    protected String TAG = "LiveActivity";
    protected WebView lWebView;
    protected ActivityLiveBinding binding;
    private Context thisContext;
    private static Vod currentLive = null;
    private List<Live> provinces = new ArrayList<>();
    private int currentProvinceIndex = 0;

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
        if(null==currentLive){
            ToastUtils.show(this,"获取数据错误 请重启",Toast.LENGTH_SHORT);
            finish();
            return;
        }
        initData();
        //更新数据
        initWebView();
        lWebView.requestFocus();
        binding.webviewWrapper.requestFocus();
        //数据库获取最新数据
        //String liveUrl= "https://tv.cctv.com/live/cctv13/";
        lWebView.loadUrl(currentLive.getUrl());
        ToastUtils.show(this,"已支持遥控器上下左右可快速切台",Toast.LENGTH_SHORT);

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
                        if (lWebView != null) {
                            lWebView.loadUrl(currentLive.getUrl());
                        }
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


    public boolean dispatchTouchEvent(MotionEvent event) {
        if(!isMenuShow()&&event.getAction() == KeyEvent.ACTION_DOWN){
            showMenu();
            return true;
        }
        return super.dispatchTouchEvent(event);
    }
    protected     boolean isMenuShow(){
        int visible=  binding.menuContainer.getVisibility();
        if(visible== View.VISIBLE){
            return true;
        }
        return false;
    }
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_UP) {
            return super.dispatchKeyEvent(event);
        }
        int keyCode = event.getKeyCode();
        LogUtil.i("keyDown keyCode ", keyCode+" event" + event);
        boolean isMenuShow=isMenuShow();
        if(isMenuShow){
            if(keyCode==KeyEvent.KEYCODE_BACK||keyCode==KeyEvent.KEYCODE_MENU||keyCode==KeyEvent.KEYCODE_TAB){
                hideMenu();
                return true;
            }
            if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                    currentProvinceIndex--;
                    if (currentProvinceIndex < 0) {
                        currentProvinceIndex = provinces.size() - 1;
                    }
                } else {
                    currentProvinceIndex++;
                    if (currentProvinceIndex >= provinces.size()) {
                        currentProvinceIndex = 0;
                    }
                }
                showCurrentProvince();
                return true;
            }
            return super.dispatchKeyEvent(event);
        }
        if(keyCode==KeyEvent.KEYCODE_MENU|| keyCode == KeyEvent.KEYCODE_TAB||keyCode==KeyEvent.KEYCODE_DPAD_CENTER||keyCode==KeyEvent.KEYCODE_ENTER){
            showMenu();
            return true;
        }

        if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
            return goNext("right");
        }
        if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
            return goNext("left");
        }
        if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
            return goNext("down");
        }
        if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
            return goNext("up");
        }
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            toHome();
            return true;
        }
        return super.dispatchKeyEvent(event);
    }
    private void toHome(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
    private boolean ctrl(String code){
        String  js= "_menuCtrl."+code+"()";
        LogUtil.i(TAG,js);
        lWebView.evaluateJavascript(js,null);
        return true;
    }
    private void bind(){
        binding = DataBindingUtil.setContentView(this, R.layout.activity_live);
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
                LogUtil.i(TAG,"onProgressChangedX"+url);
                Vod vod = UpdateService.getByUrl(url);
                if(null!=vod){
                    currentLive=vod;
                    binding.liveName.setText(currentLive.getName()+" "+newProgress+"%");
                }
                if(newProgress==100){
                    HistoryDaoX.updateChannel(thisContext,url);
                    handler.sendMessageDelayed (handler.obtainMessage(2, "noText"),1000);
                }
                LogUtil.i("WebChromeClient", "onProgressChanged, newProgress:" + newProgress + ", view:" + view);
            }
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

    private static boolean isMenuShow=false;
    public class JsInterface{

        // Android 调用 Js 方法1 中的返回值
        @JavascriptInterface
        public void toast(String message){
            LogUtil.i(TAG,"message "+message);
            ToastUtils.show(MyApplication.getContext(),message, Toast.LENGTH_SHORT);
        }
        @JavascriptInterface
        public void message(String service,String data){
            LogUtil.i(TAG,"service "+service+" data "+data);
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
                return FileUtil.readExt(MyApplication.getAppContext(),"tv-web/"+url);
            }
            LogUtil.i(TAG,headerMap.toString()+"url "+url+" "+requestBody);
            return HttpUtil.postJson(url,headerMap,requestBody);
        }
        @JavascriptInterface
        public String getJson(String url,String header){
            Map<String, String> headerMap= JsonUtil.fromJson(header,
                    new TypeToken<Map<String, String>>() {}.getType());
            if(!url.startsWith("http")){
                return FileUtil.readExt(MyApplication.getAppContext(),"tv-web/"+url);
            }
            LogUtil.i(TAG,headerMap.toString()+"url "+url);
            return HttpUtil.getJson(url,headerMap);
        }
        @JavascriptInterface
        public String getHtml(String url,String header){
            Map<String, String> headerMap= JsonUtil.fromJson(header,
                    new TypeToken<Map<String, String>>() {}.getType());
            LogUtil.i(TAG,headerMap.toString()+" getHtml "+url);
            return HttpUtil.getJson(url,headerMap);
        }

    }


    //key event
    private static Instrumentation inst = new Instrumentation();
    private static Map<String,Integer> keyCodeMap=new HashMap<>();
    static {
        keyCodeMap.put("SPACE",62);
        keyCodeMap.put("F",KeyEvent.KEYCODE_F);  // 使用Android标准键码
    }
    protected void keyCodeAllByCode(String keyCode){
        Integer keyCodeNum=  keyCodeMap.get(keyCode);
        if(null==keyCodeNum){return;}
        LogUtil.i("onKeyEvent", "keyCodeStr "+keyCode);
        keyEventAll(keyCodeNum);
    }
    protected void keyEventAll(final int keyCode){
        new Thread() {
            public void run() {
                try {
                    LogUtil.i("onKeyEvent", "onKeyEvent"+keyCode);
                    inst.sendKeySync(new KeyEvent(KeyEvent.ACTION_DOWN, keyCode));
                    inst.sendKeySync(new KeyEvent(KeyEvent.ACTION_UP, keyCode));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private void initData() {
        // 使用异步任务加载数据
        new Thread(() -> {
            // 在后台线程执行耗时操作
            List<Live> result = UpdateService.getByLives();
            
            // 在UI线程更新界面
            runOnUiThread(() -> {
                provinces = result;
                currentProvinceIndex = currentLive.getTagIndex();
                showCurrentProvince();
            });
        }).start();
    }

    private Vod createVod(String name, String key, String url) {
        Vod vod = new Vod();
        vod.setName(name);
        vod.setKey(key);
        vod.setUrl(url);
        return vod;
    }

    private void showCurrentProvince() {
        if (provinces == null || provinces.isEmpty()) {
            // 处理空数据情况
            binding.provinceName.setText("无数据");
            setupChannelList(new ArrayList<>());
            return;
        }
        
        // 确保索引在有效范围内
        if (currentProvinceIndex < 0) {
            currentProvinceIndex = 0;
        } else if (currentProvinceIndex >= provinces.size()) {
            currentProvinceIndex = provinces.size() - 1;
        }
        
        Live currentProvince = provinces.get(currentProvinceIndex);
        binding.provinceName.setText(currentProvince.getName() + "(" + currentProvince.getVods().size() + ")");
        setupChannelList(currentProvince.getVods());
    }

    private void setupChannelList(List<Vod> channels) {
        ArrayAdapter<Vod> adapter = new ArrayAdapter<Vod>(this, android.R.layout.simple_list_item_1, channels) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                Button btn;
                if (convertView == null) {
                    btn = new Button(getContext());
                    btn.setLayoutParams(new AbsListView.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT));
                    btn.setTextColor(Color.WHITE);
                    btn.setTextSize(16);
                    btn.setPadding(24, 16, 24, 16);
                    btn.setBackgroundResource(R.drawable.menu_button_background);
                    btn.setClickable(false);
                    btn.setFocusable(false);
                } else {
                    btn = (Button) convertView;
                    
                    if (!(btn.getLayoutParams() instanceof AbsListView.LayoutParams)) {
                        btn.setLayoutParams(new AbsListView.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT));
                    }
                }
                
                Vod channel = getItem(position);
                if (channel != null) {
                    btn.setText(channel.getName());
                }
                
                return btn;
            }
        };
        binding.channelList.setAdapter(adapter);
        binding.channelList.setOnItemClickListener((parent, view, position, id) -> {
            try {
                Vod channel = channels.get(position);
                if (channel.getUrl() != null) {
                    currentLive = channel;
                    // 在主线程中执行WebView操作
                    runOnUiThread(() -> {
                        try {
                            LogUtil.i(TAG, "Loading URL in WebView: " + channel.getUrl());
                            lWebView.loadUrl(channel.getUrl());
                            LogUtil.i(TAG, "URL loaded successfully");
                        } catch (Exception e) {
                            LogUtil.e(TAG, "Error loading URL in WebView: " + e.getMessage());
                            e.printStackTrace();
                        }
                    });
                    
                    // 更新历史记录
                    HistoryDaoX.updateChannel(thisContext, channel.getUrl());
                    
                    // 显示提示
                    showToast(channel.getName(), this);
                    
                    // 隐藏菜单
                    hideMenu();
                } else {
                    LogUtil.e(TAG, "Channel or URL is null");
                }
            } catch (Exception e) {
                LogUtil.e(TAG, "Error handling channel click: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    private void showMenu() {
        binding.menuContainer.setVisibility(View.VISIBLE);
        isMenuShow = true;
        showCurrentProvince();
        setupProvinceButtons();
        // 默认选中第一个频道
        if (binding.channelList.getAdapter() != null && binding.channelList.getCount() > 0) {
            binding.channelList.setSelection(0);
            binding.channelList.requestFocus();
        }
        
        // 点击空白处关闭菜单
        binding.menuContainer.setOnClickListener(v -> hideMenu());
    }

    private void setupProvinceButtons() {
        binding.prevProvince.setOnClickListener(v -> {
            currentProvinceIndex--;
            if (currentProvinceIndex < 0) {
                currentProvinceIndex = provinces.size() - 1;
            }
            showCurrentProvince();
        });

        binding.nextProvince.setOnClickListener(v -> {
            currentProvinceIndex++;
            if (currentProvinceIndex >= provinces.size()) {
                currentProvinceIndex = 0;
            }
            showCurrentProvince();
        });
    }

    private void hideMenu() {
        binding.menuContainer.setVisibility(View.GONE);
        isMenuShow = false;
        binding.menuContainer.setOnClickListener(null);
    }


    private boolean isWebViewDestroyed = false;

    @Override
    protected void onPause() {
        if (lWebView != null) {
            try {
                // 暂停 WebView 以减少资源使用
                lWebView.onPause();
                // 暂停 JS 执行
                lWebView.getSettings().setJavaScriptEnabled(false);
            } catch (Exception e) {
                LogUtil.e(TAG, "Error pausing WebView", e);
            }
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (lWebView != null && !isWebViewDestroyed) {
            try {
                // 恢复 WebView
                lWebView.onResume();
                // 恢复 JS 执行
                lWebView.getSettings().setJavaScriptEnabled(true);
            } catch (Exception e) {
                LogUtil.e(TAG, "Error resuming WebView", e);
            }
        }
    }
    /**
     * 安全地销毁 WebView
     */
    private void destroyWebView() {
        if (lWebView != null && !isWebViewDestroyed) {
            // 标记 WebView 已销毁，防止重复操作
            isWebViewDestroyed = true;

            try {
                // 移除所有 JS 接口
                lWebView.removeJavascriptInterface("android");
                // 其他可能的 JS 接口...

                // 加载空白页面
                lWebView.loadUrl("about:blank");

                // 清除历史
                lWebView.clearHistory();

                // 从父视图中移除
                ViewParent parent = lWebView.getParent();
                if (parent instanceof ViewGroup) {
                    ((ViewGroup) parent).removeView(lWebView);
                }

                // 停止加载
                lWebView.stopLoading();

                // 清除缓存
                lWebView.clearCache(true);
                lWebView.clearFormData();
                lWebView.clearSslPreferences();

                // 销毁 WebView
                lWebView.destroy();
                // 设置为 null
                lWebView = null;
            } catch (Exception e) {
                LogUtil.e(TAG, "Error destroying WebView", e);
            }
        }
    }
    @Override
    protected void onDestroy() {
        destroyWebView();
        super.onDestroy();
    }
 /*   @Override
    public void onDestroy() {
        if(lWebView!=null){
            LogUtil.i(TAG,"onDestroy");
            //lWebView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            lWebView.clearHistory();
            lWebView.destroy();
        }
        super.onDestroy();
    }*/
    /**
     * 防止内存泄漏的额外措施
     */
    @Override
    protected void onStop() {
        super.onStop();
        if (lWebView != null) {
            try {
                // 停止所有可能的后台处理
                lWebView.stopLoading();
            } catch (Exception e) {
                LogUtil.e(TAG, "Error stopping WebView", e);
            }
        }
    }


}