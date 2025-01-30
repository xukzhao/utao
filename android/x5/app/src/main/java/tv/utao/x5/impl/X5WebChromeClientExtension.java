package tv.utao.x5.impl;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.tencent.smtt.export.external.extension.interfaces.IX5WebChromeClientExtension;
import com.tencent.smtt.export.external.extension.interfaces.IX5WebViewExtension;
import com.tencent.smtt.export.external.interfaces.IX5WebViewBase;
import com.tencent.smtt.export.external.interfaces.JsResult;
import com.tencent.smtt.export.external.interfaces.MediaAccessPermissionsCallback;

import java.util.HashMap;

public class X5WebChromeClientExtension implements IX5WebChromeClientExtension {

    private static String TAG="X5WebChromeClientExtension";
    @Override
    public Object getX5WebChromeClientInstance() {
        Log.i(TAG,"getX5WebChromeClientInstance");
        return null;
    }

    @Override
    public View getVideoLoadingProgressView() {
        Log.i(TAG,"getVideoLoadingProgressView");
        return null;
    }

    @Override
    public void onAllMetaDataFinished(IX5WebViewExtension ix5WebViewExtension, HashMap<String, String> hashMap) {
        Log.i(TAG,"onAllMetaDataFinished");
    }

    @Override
    public void onBackforwardFinished(int i) {
        Log.i(TAG,"onBackforwardFinished"+i);
    }

    @Override
    public void onHitTestResultForPluginFinished(IX5WebViewExtension ix5WebViewExtension, IX5WebViewBase.HitTestResult hitTestResult, Bundle bundle) {
        Log.i(TAG,"onHitTestResultForPluginFinished");
    }

    @Override
    public void onHitTestResultFinished(IX5WebViewExtension ix5WebViewExtension, IX5WebViewBase.HitTestResult hitTestResult) {
        Log.i(TAG,"onHitTestResultFinished");
    }

    @Override
    public void onPromptScaleSaved(IX5WebViewExtension ix5WebViewExtension) {
        Log.i(TAG,"onPromptScaleSaved");
    }

    @Override
    public void onPromptNotScalable(IX5WebViewExtension ix5WebViewExtension) {
        Log.i(TAG,"onPromptNotScalable");
    }

    @Override
    public boolean onAddFavorite(IX5WebViewExtension ix5WebViewExtension, String s, String s1, JsResult jsResult) {
        Log.i(TAG,"onAddFavorite");
        return false;
    }

    @Override
    public void onPrepareX5ReadPageDataFinished(IX5WebViewExtension ix5WebViewExtension, HashMap<String, String> hashMap) {
        Log.i(TAG,"onPrepareX5ReadPageDataFinished");
    }

    @Override
    public boolean onSavePassword(String s, String s1, String s2, boolean b, Message message) {
        Log.i(TAG,"onSavePassword");
        return false;
    }

    @Override
    public boolean onSavePassword(android.webkit.ValueCallback<String> valueCallback, String s, String s1, String s2, String s3, String s4, boolean b) {
        Log.i(TAG,"onSavePasswordX");
        return false;
    }

    @Override
    public void onX5ReadModeAvailableChecked(HashMap<String, String> hashMap) {
        Log.i(TAG,"onX5ReadModeAvailableChecked");
    }

    @Override
    public void addFlashView(View view, ViewGroup.LayoutParams layoutParams) {
        Log.i(TAG,"addFlashView");
    }

    @Override
    public void h5videoRequestFullScreen(String s) {
        Log.i(TAG,"h5videoRequestFullScreen");
    }

    @Override
    public void h5videoExitFullScreen(String s) {
        Log.i(TAG,"h5videoExitFullScreen");
    }

    @Override
    public void requestFullScreenFlash() {
        Log.i(TAG,"requestFullScreenFlash");
    }

    @Override
    public void exitFullScreenFlash() {
        Log.i(TAG,"exitFullScreenFlash");
    }

    @Override
    public void jsRequestFullScreen() {
        Log.i(TAG,"jsRequestFullScreen");
    }

    @Override
    public void jsExitFullScreen() {
        Log.i(TAG,"jsExitFullScreen");
    }

    @Override
    public void acquireWakeLock() {
        Log.i(TAG,"acquireWakeLock");
    }

    @Override
    public void releaseWakeLock() {
        Log.i(TAG,"releaseWakeLock");
    }

    @Override
    public Context getApplicationContex() {
        Log.i(TAG,"getApplicationContex");
        return null;
    }

    @Override
    public boolean onPageNotResponding(Runnable runnable) {
        Log.i(TAG,"onPageNotResponding");
        return false;
    }

    @Override
    public Object onMiscCallBack(String s, Bundle bundle) {
        Log.i(TAG,"onMiscCallBack");
        return null;
    }

    @Override
    public void openFileChooser(android.webkit.ValueCallback<Uri[]> valueCallback, String s, String s1) {
        Log.i(TAG,"openFileChooser");

    }

    @Override
    public void onPrintPage() {
        Log.i(TAG,"onPrintPage");
    }

    @Override
    public void onColorModeChanged(long l) {
        Log.i(TAG,"onColorModeChanged");
    }

    @Override
    public boolean onPermissionRequest(String s, long l, MediaAccessPermissionsCallback mediaAccessPermissionsCallback) {
        Log.i(TAG,"onPermissionRequest "+s+" "+l);
        //mediaAccessPermissionsCallback.invoke(s,MediaAccessPermissionsCallback.,);
        return true;
    }
}
