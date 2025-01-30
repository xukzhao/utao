package tv.utao.x5.impl;

import android.util.Log;
import android.view.View;

import com.tencent.smtt.export.external.interfaces.IX5WebChromeClient;
import com.tencent.smtt.export.external.interfaces.JsPromptResult;
import com.tencent.smtt.export.external.interfaces.JsResult;
import com.tencent.smtt.export.external.interfaces.PermissionRequest;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebView;

import java.util.Arrays;

public class WebChromeClientImpl extends WebChromeClient {
    private static String TAG="WebChromeClient";
    /**
     * 具体接口使用细节请参考文档：
     * https://x5.tencent.com/docs/webview.html
     * 或 Android WebKit 官方：
     * https://developer.android.com/reference/android/webkit/WebChromeClient
     */

    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        Log.i(TAG, "onProgressChanged, newProgress:" + newProgress + ", view:" + view);
    }

    @Override
    public boolean onJsAlert(WebView webView, String url, String message, JsResult result) {
        Log.i(TAG,"onJsAlert "+url);
/*                new AlertDialog.Builder(context).setTitle("JS弹窗Override")
                        .setMessage(message)
                        .setPositiveButton("OK", (dialogInterface, i) -> result.confirm())
                        .setCancelable(false)
                        .show();*/
        return true;
    }

    @Override
    public boolean onJsConfirm(WebView webView, String url, String message, JsResult result) {
        Log.i(TAG,"onJsConfirm "+url);
               /* new AlertDialog.Builder(context).setTitle("JS弹窗Override")
                        .setMessage(message)
                        .setPositiveButton("OK", (dialogInterface, i) -> result.confirm())
                        .setNegativeButton("Cancel", (dialogInterface, i) -> result.cancel())
                        .setCancelable(false)
                        .show();*/
        return true;
    }

    @Override
    public boolean onJsBeforeUnload(WebView webView, String url, String message, JsResult result) {
        Log.i(TAG,"onJsBeforeUnload "+url);
            /*    new AlertDialog.Builder(context).setTitle("页面即将跳转")
                        .setMessage(message)
                        .setPositiveButton("OK", (dialogInterface, i) -> result.confirm())
                        .setNegativeButton("Cancel", (dialogInterface, i) -> result.cancel())
                        .setCancelable(false)
                        .show();*/
        return true;
    }

    @Override
    public boolean onJsPrompt(WebView webView, String url, String message, String defaultValue, JsPromptResult result) {
        Log.i(TAG,"onJsPrompt");
                /*final EditText input = new EditText(context);
                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);*/
               /* new AlertDialog.Builder(context).setTitle("JS弹窗Override")
                        .setMessage(message)
                        .setView(input)
                        .setPositiveButton("OK", (dialogInterface, i) -> result.confirm(input.getText().toString()))
                        .setCancelable(false)
                        .show();*/
        return true;
    }

    @Override
    public void onPermissionRequest(PermissionRequest request) {
        Log.i(TAG,"onPermissionRequest "+request.getOrigin());
            Log.i(TAG,request.getOrigin()+" "+ Arrays.toString(request.getResources()));
            request.deny();
            //request.grant(request.getResources());
    }
    @Override
    public void onShowCustomView(View view, IX5WebChromeClient.CustomViewCallback callback) {
        Log.i(TAG,"onShowCustomView ");
    }
}
