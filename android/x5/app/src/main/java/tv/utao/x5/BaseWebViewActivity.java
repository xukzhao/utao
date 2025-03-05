package tv.utao.x5;

import static tv.utao.x5.util.PermissionUtil.REQUEST_EXTERNAL_STORAGE;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.reflect.TypeToken;
import com.tencent.smtt.export.external.extension.interfaces.IX5WebSettingsExtension;
import com.tencent.smtt.export.external.interfaces.IX5WebChromeClient;
import com.tencent.smtt.export.external.interfaces.PermissionRequest;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tv.utao.x5.api.ConfigApi;
import tv.utao.x5.call.DownloadCallback;
import tv.utao.x5.call.StringCallback;
import tv.utao.x5.dao.HistoryDaoX;
import tv.utao.x5.databinding.ActivityMainBinding;
import tv.utao.x5.databinding.ItemHzBinding;
import tv.utao.x5.databinding.ItemJdBinding;
import tv.utao.x5.databinding.ItemRateBinding;
import tv.utao.x5.databinding.ItemXjBinding;
import tv.utao.x5.domain.ApkInfo;
import tv.utao.x5.domain.ConfigDTO;
import tv.utao.x5.domain.DetailMenu;
import tv.utao.x5.domain.HzItem;
import tv.utao.x5.domain.JdItem;
import tv.utao.x5.domain.RateItem;
import tv.utao.x5.domain.SysInfo;
import tv.utao.x5.domain.XjItem;
import tv.utao.x5.impl.BaseBindingAdapter;
import tv.utao.x5.impl.BaseViewHolder;
import tv.utao.x5.impl.IBaseBindingPresenter;
import tv.utao.x5.impl.WebViewClientImpl;
import tv.utao.x5.impl.X5WebChromeClientExtension;
import tv.utao.x5.service.UpdateService;
import tv.utao.x5.util.AppVersionUtils;
import tv.utao.x5.util.DataCleanManager;
import tv.utao.x5.util.FileUtil;
import tv.utao.x5.util.HttpUtil;
import tv.utao.x5.util.JsonUtil;
import tv.utao.x5.util.Util;
import tv.utao.x5.util.ValueUtil;
import tv.utao.x5.util.WebService;


/**
 * Demo 基础 WebViewActivity，所有WebView能力Demo继承该 Activity 开发
 */
public class BaseWebViewActivity extends Activity {
    protected String TAG = "BaseWebViewActivity";

    public static com.tencent.smtt.sdk.WebView mWebView;



    private static final String mHomeUrl =
            "https://tv.utao.tv/tv-web/index.html";
            //"https://www.iqiyi.com/v_mscze4lfao.html";
            //"https://tv.utao.tv/tv-web/index.html";
            //"file:///android_asset/homePage.html";

    protected  ActivityMainBinding binding;

    private Context thisContext;
    private boolean x5Ok(){
        return "ok".equals(ValueUtil.getString(this,"x5","0"));
    }
    private void toStart(){
        Intent intent = new Intent(this, StartActivity.class);
        startActivity(intent);
        finish();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//隐藏标题栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        bind();
        thisContext=this;
        UpdateService.updateRes(thisContext);
        initWebView();
        //file:///android_asset/tv-web/index.html http://www.utao.tv/tv-web/index.html
        mWebView.loadUrl(mHomeUrl);
        ConfigApi.syncIsX5Ok(this);
    }


    private void bind(){
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.setMenuTitleHandler(new MenuTitleHandler());
        mWebView=binding.webView;
        focusChange();
    }

    /**
     * 自定义初始化WebView设置，此处为默认 BaseWebViewActivity 初始化
     * 可通过继承该 Activity Override 该方法做自己的实现
     */
    protected void initWebView() {
        WebSettings webSetting = mWebView.getSettings();
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
        // 在WebView的初始化代码中启用缓存
        IX5WebSettingsExtension webSettingsExtension=  mWebView.getSettingsExtension();
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
           // webSettingsExtension.setPicModel(IX5WebSettingsExtension.PicModel_NoPic);
        }
        initWebViewClient();
        initWebChromeClient();
        //禁止上下左右滚动(不显示滚动条)
        mWebView.setScrollContainer(false);
        mWebView.setVerticalScrollBarEnabled(false);
        mWebView.setHorizontalScrollBarEnabled(false);

        //远程调试
        WebView.setWebContentsDebuggingEnabled(true);
       // mWebView.setFocusable(false);
        //mWebView.setFocusableInTouchMode(false);
        //硬件加速 android 4.X 有问题
        //mWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        //mWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        mWebView.addJavascriptInterface(new JsInterface(),"_api");

    }


    private void initWebViewClient() {
        mWebView.setWebViewClient(new WebViewClientImpl(getBaseContext(),mWebView,0));
    }

    private void initWebChromeClient() {
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                Log.i("WebChromeClient", "onProgressChanged, newProgress:" + newProgress + ", view:" + view);
            }
            @Override
            public void onShowCustomView(View view, IX5WebChromeClient.CustomViewCallback callback) {
                Log.i("WebChromeClient","onShowCustomView");
                binding.fullscreen.addView(view);
                binding.fullscreen.setVisibility(View.VISIBLE);
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
                binding.fullscreen.removeAllViews();
                binding.fullscreen.setVisibility(View.GONE);
            }
        });
        mWebView.setWebChromeClientExtension(new X5WebChromeClientExtension());
    }



    /* Don't care about the Base UI Logic below ^_^ */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_EXTERNAL_STORAGE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 权限被授予，可以进行文件操作
            } else {
                // 权限被拒绝，需要进行一些UX处理
            }
            Log.i(TAG, "onRequestPermissionsResult: initWebView");
            //initX5();
            initWebView();
        }
    }

    @Override
    protected void onDestroy() {
        if (mWebView != null) {
            mWebView.destroy();
        }
        super.onDestroy();
    }


    //菜单
    private Button defaultFocusBtn(View oldFocus, View newFocus){
        if(!(newFocus instanceof Button)){
            return null;
        }
        if(null!=oldFocus){  oldFocus.setScaleX(1.0f); oldFocus.setScaleY(1.0f);}
        newFocus.setScaleX(1.1f);
        newFocus.setScaleY(1.1f);
        return (Button) newFocus;

    }
    private String  oldBtnTag(View oldFocus){
        if(!(oldFocus instanceof Button)){
            return null;
        }
        Object tagObj=oldFocus.getTag();
        if(null==tagObj){return null;}
        return  tagObj.toString();
    }

    private void focusChange() {
        View view = binding.tvMenu;
        view.getViewTreeObserver().addOnGlobalFocusChangeListener(new ViewTreeObserver.OnGlobalFocusChangeListener() {
            @Override
            public void onGlobalFocusChanged(View oldFocus, View newFocus) {
                Log.d(TAG, "onGlobalFocusChanged: oldFocus=" + oldFocus);
                Log.d(TAG, "onGlobalFocusChanged: newFocus=" + newFocus);
                //lastFocus=oldFocus;nowFocus=newFocus;
                Button focusBtn=defaultFocusBtn(oldFocus,newFocus);
                if(null==focusBtn){return;}
                Object tagObj=focusBtn.getTag();
                if(null==tagObj){return;}
                String tag= tagObj.toString();
                Log.d(TAG, "onGlobalFocusChanged: newFocus=" + tag);
                if(tag.startsWith("menu_")){
                    tag=tag.substring(5);
                    binding.getMenu().setTab(tag);
                }
                String oldTag=null;
                switch (tag){
                    case "hzItem":
                        focusBtn.setNextFocusUpId(R.id.hzBtn);
                        oldTag=oldBtnTag(oldFocus);
                        if(null!=oldTag){
                            if(oldTag.equals("menu_hz")){
                                RecyclerView.ViewHolder viewHolder = binding.hzsView.findViewHolderForLayoutPosition(0);
                                if(null!=viewHolder){
                                    viewHolder.itemView.requestFocus();
                                }
                            }
                        }
                        break;
                    case "rateItem":
                        focusBtn.setNextFocusUpId(R.id.rateBtn);
                        oldTag=oldBtnTag(oldFocus);
                        if(null!=oldTag){
                            if(oldTag.equals("menu_rate")){
                                RecyclerView.ViewHolder viewHolder = binding.ratesView.findViewHolderForLayoutPosition(0);
                                if(null!=viewHolder){
                                    viewHolder.itemView.requestFocus();
                                }
                            }
                        }
                        break;
                    case "jdItem":
                        LinearLayout layoutJd = (LinearLayout) focusBtn.getParent();
                        RecyclerView.LayoutParams paramsJd = (RecyclerView.LayoutParams) layoutJd.getLayoutParams();
                        int itemPositionJd = paramsJd.getViewLayoutPosition();
                        if(itemPositionJd<6){
                            focusBtn.setNextFocusUpId(R.id.jdBtn);
                        }
                        oldTag=oldBtnTag(oldFocus);
                        if(null!=oldTag){
                            if(oldTag.equals("menu_jd")){
                                RecyclerView.ViewHolder viewHolder = binding.jdsView.findViewHolderForLayoutPosition(0);
                                if(null!=viewHolder){
                                    viewHolder.itemView.requestFocus();
                                }
                            }
                        }
                        break;
                    case "xjItem":
                        LinearLayout layout = (LinearLayout) focusBtn.getParent();
                        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) layout.getLayoutParams();
                        int itemPosition = params.getViewLayoutPosition();
                        if(itemPosition<6){
                            focusBtn.setNextFocusUpId(R.id.xjBtn);
                        }
                        Log.d(TAG, "xjItem: index=" + itemPosition);
                        oldTag=oldBtnTag(oldFocus);
                        if(null!=oldTag){
                            //old上一个是选集btn 下一个是item 自动选择
                            if(oldTag.equals("menu_xj")){
                                int id =  binding.xjsView.getLayoutManager().getItemCount();
                                Log.i(TAG,"count "+ id+" "+ binding.xjsView.getChildCount()+" "+binding.xjsView.getAdapter().getItemCount());
                                int viewCount= binding.xjsView.getChildCount();
                                int num=binding.getMenu().getNow().getXj().getIndex();
                                if(num>viewCount){
                                    num=viewCount-1;
                                }
                                RecyclerView.ViewHolder viewHolder = binding.xjsView.findViewHolderForLayoutPosition(num);
                                if(null!=viewHolder){
                                    viewHolder.itemView.requestFocus();
                                }
                            }
                        }
                        break;
                    default:
                        Log.i(TAG,"setTab"+tag);
                        break;
                }
            }
        });
    }

    private void xjBlind(List<XjItem> xjItems){
        BaseBindingAdapter xjAdapter = new BaseBindingAdapter<XjItem, ItemXjBinding>(xjItems,R.layout.item_xj) {
            @Override
            public void doBindViewHolder(BaseViewHolder<ItemXjBinding> holder, XjItem item) {
                holder.getBinding().setVariable(BR.item, item);
                holder.getBinding().setVariable(BR.itemPresenter, ItemPresenter);
            }
        };
        xjAdapter.setItemPresenter(new XjBindPresenter());
        binding.xjsView
                .setLayoutManager(new GridLayoutManager(this, 6));
        binding.xjsView
                .setAdapter(xjAdapter);
    }
    private void jdBlind(List<JdItem> jdItems){
        BaseBindingAdapter jdAdapter = new BaseBindingAdapter<JdItem, ItemJdBinding>(jdItems,R.layout.item_jd) {
            @Override
            public void doBindViewHolder(BaseViewHolder<ItemJdBinding> holder, JdItem item) {
                holder.getBinding().setVariable(BR.item, item);
                holder.getBinding().setVariable(BR.itemPresenter, ItemPresenter);
            }
        };
        jdAdapter.setItemPresenter(new JdBindPresenter());
        binding.jdsView
                .setLayoutManager(new GridLayoutManager(this, 6));
        binding.jdsView
                .setAdapter(jdAdapter);
    }
    private void hzBind(List<HzItem> hzItems){
        BaseBindingAdapter hzAdapter = new BaseBindingAdapter<HzItem, ItemHzBinding>(hzItems,R.layout.item_hz) {
            @Override
            public void doBindViewHolder(BaseViewHolder<ItemHzBinding> holder, HzItem item) {
                holder.getBinding().setVariable(BR.item, item);
                holder.getBinding().setVariable(BR.itemPresenter, ItemPresenter);
            }
        };
        hzAdapter.setItemPresenter(new HzBindPresenter());
        binding.hzsView
                .setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        binding.hzsView
                .setAdapter(hzAdapter);
    }

    private void rateBind(List<RateItem> rateItems){
        BaseBindingAdapter rateAdapter = new BaseBindingAdapter<RateItem, ItemRateBinding>(rateItems,R.layout.item_rate) {
            @Override
            public void doBindViewHolder(BaseViewHolder<ItemRateBinding> holder, RateItem item) {
                holder.getBinding().setVariable(BR.item, item);
                holder.getBinding().setVariable(BR.itemPresenter, ItemPresenter);
            }
        };
        rateAdapter.setItemPresenter(new RateBindPresenter());
        binding.ratesView
                .setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        binding.ratesView
                .setAdapter(rateAdapter);

    }

    public  class XjBindPresenter implements IBaseBindingPresenter {

        public void onClick(XjItem item) {
            Log.i(TAG,item.getTitle());
            //TestActivity.binding.getMenu().getNow().setXj(item);
            hideMenu();
            postMessage("click","xj-"+item.getId());

        }
    }
    public  class JdBindPresenter implements IBaseBindingPresenter {

        public void onClick(JdItem item) {
            Log.i(TAG,item.getName());
            hideMenu();
            postMessage("click","jd-"+item.getId());

        }
    }

    public    class HzBindPresenter implements IBaseBindingPresenter {

        public void onClick(HzItem item) {
            Log.i(TAG,item.getName());
            hideMenu();
            postMessage("click","hz-"+item.getId());

        }
    }
    public  class RateBindPresenter implements IBaseBindingPresenter {

        public void onClick(RateItem item) {
            Log.i(TAG,item.getName());
            hideMenu();
            postMessage("click","rate-"+item.getId());

        }
    }

    public  class MenuTitleHandler {

        public void nextBtn() {
            hideMenu();
            postMessage("click","tv-next");
        }
        public void reloadBtn() {
            hideMenu();
            mWebView.reload();
        }
        public void btnClick(View view){
            Log.i(TAG,"btnClick "+view);
            //binding.tvMenu.setFocusable(true);
             view.requestFocus();
        }
    }

    //menu mange
    protected     boolean isMenuShow(){
        int visible=  binding.tvMenu.getVisibility();
        if(visible== View.VISIBLE){
            return true;
        }
        return false;
    }
    protected void showMenu(String data){
        //binding.webView.setFocusable(false);
        //binding.tvMenu.setFocusable(true);
        Log.i(TAG,"data:: "+data);
        if(null==data||!data.startsWith("{")){
            return;
        }
        DetailMenu detailMenu = JsonUtil.fromJson(data, DetailMenu.class);
        binding.setMenu(detailMenu);
        xjBlind(detailMenu.getXjs());
        hzBind(detailMenu.getHzs());
        jdBlind(detailMenu.getJds());
        rateBind(detailMenu.getRates());
        //binding.nextBtn.setBackgroundResource(R.drawable.btnsel);
        binding.tvMenu.setVisibility(View.VISIBLE);
        //binding.tvMenu.requestFocus();
        binding.xjBtn.requestFocus();
        //binding.tvMenu.setFocusable(false);
    }
    protected void hideMenu(){
        binding.tvMenu.setVisibility(View.GONE);
       // binding.webView.setFocusable(true);
        //mWebView.requestFocus();
       // binding.tvMenu.clearFocus();
        //binding.webView.setFocusable(false);
        //binding.fullscreen.setFocusable(true);
      //  binding.fullscreen.requestFocus();
    }
    public    void postMessage(String service, String data) {
        if(service.equals("click")){
            String click=Util.click(data);
            Log.i(TAG,"clickCode: "+click);
            mWebView.evaluateJavascript(click,null);
        }
    }

    private void toLive(){
        Intent intent = new Intent(this, LiveActivity.class);
        startActivity(intent);
        finish();
    }
    protected void killAppProcess()
    {
        //注意：不能先杀掉主进程，否则逻辑代码无法继续执行，需先杀掉相关进程最后杀掉主进程
        ActivityManager mActivityManager = (ActivityManager)this.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> mList = mActivityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : mList)
        {
            if (runningAppProcessInfo.pid != android.os.Process.myPid())
            {
                android.os.Process.killProcess(runningAppProcessInfo.pid);
            }
        }
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }
    private  static  WebService webService=null;
    private void newWebService(){
        if(null==webService){
            webService=new WebService(10240);
        }
    }
    //js
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
            if("activity".equals(service)){
                toLive();
                return;
            }
            if("history.save".equals(service)){
                //final AppDatabase db = AppDatabase.getInstance(this);
                HistoryDaoX.save(thisContext, data, new StringCallback() {
                    @Override
                    public void data(String data) {
                        runOnUiThread(()->{
                            mWebView.loadUrl(data);
                        });
                    }
                });
                return;
            }
            if("history.update".equals(service)){
                HistoryDaoX.update(thisContext,data);
                return;
            }
            if("menu".equals(service)){
                runOnUiThread(()->{
                    boolean isMenuShow=isMenuShow();
                    if(isMenuShow){
                        hideMenu();
                        return;
                    }
                    showMenu(data);
                });
                return;
            }
            if("openX5".equals(service)){
                ValueUtil.putString(getApplicationContext(),"openX5","1");
                Toast.makeText(thisContext, "开启内核成功 重启应用后生效",Toast.LENGTH_SHORT).show();
                killAppProcess();
                //showToastOrg("开启内核成功 重启应用后生效",thisContext);
                //toStart();
                return;
            }
            if("closeApp".equals(service)){
                killAppProcess();
                return;
            }
            if("js".equals(service)){
                Util.evalOnUi(mWebView,data);
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
            if("clearCache".equals(service)){
                DataCleanManager.cleanInternalCache(thisContext);
                DataCleanManager.cleanExternalCache(thisContext);
                Toast.makeText(thisContext, "清理缓存成功 网站可能会要求重新扫码登录",Toast.LENGTH_SHORT).show();
                return;
            }
            if("updateApk".equals(service)){
                int versionCode=  AppVersionUtils.getVersionCode();
                ConfigDTO configDTO =  ConfigApi.getConfig();
                ApkInfo apkInfo = configDTO.getApk();
                if(apkInfo.getVersion()<=versionCode){
                    return;
                }
                File targetFile = new File(thisContext.getFilesDir().getPath(),"x5.apk");
                HttpUtil.download(apkInfo.getUrl(),
                        thisContext.getFilesDir().getPath(), "x5.apk", new DownloadCallback() {
                            @Override
                            public void downloaded() {
                                Util.installApk(thisContext,targetFile);
                            }
                });
            }
            //Toast.makeText(MyApplication.getContext(),data, Toast.LENGTH_SHORT)
            // .show();
        }
        @JavascriptInterface
        public String queryByService(String service,String extPraram){
            Log.i(TAG,"queryByService "+service+" extPraram "+extPraram);
            if("queryHistory".equals(service)){
                return JsonUtil.toJson(HistoryDaoX.queryHistory(thisContext));
            }
            if("queryIp".equals(service)){
                //开启服务 有且只有一次
                newWebService();
                return Util.getLocalIPAddress(thisContext);
            }
            if("querySysInfo".equals(service)){
               ConfigDTO configDTO= ConfigApi.getConfig();
                int versionCode=  AppVersionUtils.getVersionCode();
                ApkInfo apkInfo = configDTO.getApk();
                int updateCode= apkInfo.getVersion();
                SysInfo sysInfo = new SysInfo();
                if(updateCode>versionCode){
                    sysInfo.setHaveNew(true);
                }
                boolean is64= Util.is64();
                sysInfo.setIs64(is64);
                sysInfo.setVersionCode(Build.VERSION.SDK_INT);
                sysInfo.setX5Ok(x5Ok());
                sysInfo.setCacheSize(DataCleanManager.getCacheSize(thisContext));
                //Build.VERSION.SDK_INT
                sysInfo.setVersionName(AppVersionUtils.getVersionName());
                return JsonUtil.toJson(sysInfo);
            }
            return null;
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
