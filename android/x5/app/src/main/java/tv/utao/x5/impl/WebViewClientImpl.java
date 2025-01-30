package tv.utao.x5.impl;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.tencent.smtt.export.external.interfaces.WebResourceRequest;
import com.tencent.smtt.export.external.interfaces.WebResourceResponse;
import com.tencent.smtt.sdk.ValueCallback;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import java.io.ByteArrayInputStream;
import java.nio.charset.Charset;

import tv.utao.x5.util.ConstantMy;
import tv.utao.x5.util.FileUtil;
import tv.utao.x5.util.Util;

public class WebViewClientImpl extends WebViewClient {
    private  static  String TAG="WebViewClient";
    private Context context;
    private WebView mWebView;

    private static  String lastUrl=null;
    private static  String rootUrl=null;
    private static  String currentUrl=null;
    public WebViewClientImpl(Context context,WebView mWebView){
        this.context=context;
        this.mWebView=mWebView;
    }
    @Override
    public boolean onRenderProcessGone(WebView view, RenderProcessGoneDetail detail) {
        Log.i(TAG,"onRenderProcessGone");
        view.clearCache(false);
        view.clearHistory();
        return true;
    }

    @Override
    public void onReceivedSslError(com.tencent.smtt.sdk.WebView webView,
                                   com.tencent.smtt.export.external.interfaces.SslErrorHandler handler,
                                   com.tencent.smtt.export.external.interfaces.SslError error) {
        Log.i(TAG,"onReceivedSslError");
        handler.proceed(); // 忽略 SSL 错误
    }
    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {

        Log.i(TAG, "onPageStarted , url:" + url);
        currentUrl=url;
        String baseFolder = "tv-web/";
        if(url.contains("tv-web")){
            if(url.endsWith("index.html")){
                rootUrl=url;
            }
            lastUrl=url;
        }
        String fileContent = FileUtil.readExt(baseFolder +"js/begin.js");
        Log.i(TAG,"begin:: "+fileContent);
        //begin.js cctv测试直接全屏
       // String fileContent= FileUtil.readAssert(context,"web/js/begin.js");
        view.evaluateJavascript(fileContent, new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String s) {
                Log.i(TAG, "onReceiveValue:"+s);
            }
        });
    }


    private String getFileContent(String url){
        String baseFolder = "tv-web/";
        if(url.contains(baseFolder)){
            return null;
        }
        String fileContent = FileUtil.readExt(baseFolder +"js/end.js");
        return fileContent +FileUtil.readExt(  baseFolder + "js/load_detail.js");
    }
    @Override
    public void onPageFinished(WebView view, String url) {
        Log.i(TAG, "onPageFinished, view:" + view + ", url:" + url);
        if (mWebView.getProgress() == 100) {
            Log.i(TAG, "onPageFinished XX, url:" + url);
            String fileContent =getFileContent(url);
            if(null==fileContent){
                return;
            }
            Log.i(TAG, "fileContent end:");
            view.evaluateJavascript(fileContent, new ValueCallback<String>() {
                @Override
                public void onReceiveValue(String s) {
                    Log.i(TAG, "onReceiveValue:" + s);
                }
            });
        }
    }

    @Override
    public void onReceivedError(WebView webView, int errorCode, String description, String failingUrl) {
        Log.e(TAG, "onReceivedError: " + errorCode
                + ", description: " + description
                + ", url: " + failingUrl);
    }

    @Override
    public WebResourceResponse shouldInterceptRequest(WebView webView, WebResourceRequest webResourceRequest) {
        //无图 Dec-Fetch-Dest
        String accept=  webResourceRequest.getRequestHeaders().get("Accept");
        String url=webResourceRequest.getUrl().toString();
        //Log.i(TAG,"shouldInterceptRequest "+url);
        //Log.i(TAG,"XXXXXX getMethod "+webResourceRequest.getMethod());
        //Log.i(TAG,"XXXXXX heades "+webResourceRequest.getRequestHeaders());
        /*if(notLoadUrl(url)){
            return new WebResourceResponse(null,
                    null, null);
      *//*      return new WebResourceResponse("text/plain",
                    "null", new ByteArrayInputStream("utao".getBytes()));*//*
        }*/
        if(null!=accept&&accept.startsWith("image/")&&!imageLoad(url)){
            return new WebResourceResponse(null,
                    null, null);
        }
        int index= url.indexOf("tv-web");
        if(index<0){
            if(webResourceRequest.getMethod().equals("GET")&&url.startsWith("https://mesh.if.iqiyi")){
                if(url.startsWith("https://mesh.if.iqiyi.com/tvg/v2/lw/base_info")){
                    Util.evalOnUi(webView,Util.sessionStorageWithTime("iqiyiXj",url));
                }
            }
            return super.shouldInterceptRequest(webView, webResourceRequest);
        }
        if(url.endsWith("tvImg=1")){
            if(index>0) {
                String fileName = url.substring(index,url.indexOf("?"));
                Log.i(TAG, "fileName image " + fileName);
                return new WebResourceResponse("image/jpeg",
                        ConstantMy.UTF8, FileUtil.readExtIn(fileName));
            }
        }
        int indexWen=url.indexOf("?");
        if(indexWen>0){
            url=url.substring(0,indexWen);
        }
        if(url.endsWith("js")){
            if(index>0) {
                String fileName = url.substring(index);
                Log.i(TAG, "fileName js " + fileName);
                return new WebResourceResponse("text/javascript",
                        ConstantMy.UTF8, FileUtil.readExtIn( fileName));
            }
        }
        if(url.endsWith("css")){
            if(index>0) {
                String fileName = url.substring(index);
                Log.i(TAG, "fileName css " + fileName);
                return new WebResourceResponse("text/css",
                        ConstantMy.UTF8, FileUtil.readExtIn(fileName));
            }
        }
        if(url.endsWith(".html")){
            if(index>0){
                String fileName=url.substring(index);
                Log.i(TAG,"fileName html "+fileName);
                String html = FileUtil.readExt(fileName);
                html= html.replace("base.js","basex.js");
                return new WebResourceResponse("text/html",
                        ConstantMy.UTF8, new ByteArrayInputStream(html.getBytes(Charset.defaultCharset())));
            }
        }
        if(url.endsWith(".woff2")){
            if(index>0){
                String fileName = url.substring(index);
                Log.i(TAG, "fileName woff2 " + fileName);
                return new WebResourceResponse("font/woff2",
                        ConstantMy.UTF8, FileUtil.readExtIn(fileName));
            }
        }
        return super.shouldInterceptRequest(webView, webResourceRequest);

    }


   private  boolean notLoadUrl(String url){

       if(url.contains("https://cmts.iqiyi.com/bulle")){
           return  true;
       }
      if(url.contains("https://puui.qpic.cn/iwan_cloud")){
          return  true;
      }
       if(url.contains("https://msg.qy.net/")){
           return  true;
       }
      return  false;
   }
    private boolean  imageLoad(String url){
        if(url.contains("tvImg")){
            return true;
        }
        if(url.contains("cctvpic.com")){
            return true;
        }
        if(url.contains("default")){
            return true;
        }
        if(url.contains("open.weixin.qq.com/connect/qrcode")){
            String code=Util.loginQr(url,"微信");
            Log.i(TAG, "imageLoad: "+code);
            Util.evalOnUi(mWebView, code);
            return true;
        }
        if(url.contains("ssl.ptlogin2.qq.com/ptqrshow")){
            String code=Util.loginQr(url,"手机端qq");
            Log.i(TAG, "imageLoad: "+code);
            Util.evalOnUi(mWebView, code);
            return true;
        }
        if(url.startsWith("https://img.alicdn.com/imgextra/")&&url.endsWith("xcode.png")){
            String code=Util.loginQr(url,"youkuQr");
                    //Util.sessionStorageWithTime("youkuQr",url);
            Log.i(TAG, "imageLoad: "+code);
            Util.evalOnUi(mWebView, code);
            return true;
        }
        return false;
    }

    public static String  backUrl(){
        if(null==currentUrl){
            return null;
        }
        if(currentUrl.contains("tv-web")){
            if(currentUrl.endsWith("index.html")){
                return null;
            }
            return rootUrl;
        }
        return lastUrl;
    }

}
