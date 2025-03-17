package tv.utao.x5.impl;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;

import com.tencent.smtt.export.external.extension.interfaces.IX5WebChromeClientExtension;
import com.tencent.smtt.export.external.extension.interfaces.IX5WebViewExtension;
import com.tencent.smtt.export.external.interfaces.IX5WebViewBase;
import com.tencent.smtt.export.external.interfaces.JsResult;
import com.tencent.smtt.export.external.interfaces.MediaAccessPermissionsCallback;

import java.util.HashMap;

import tv.utao.x5.util.LogUtil;

public class X5WebChromeClientExtension implements IX5WebChromeClientExtension {

    private static String TAG="X5WebChromeClientExtension";
    @Override
    public Object getX5WebChromeClientInstance() {
        LogUtil.i(TAG,"getX5WebChromeClientInstance");
        return null;
    }

    @Override
    public View getVideoLoadingProgressView() {
        LogUtil.i(TAG,"getVideoLoadingProgressView");
        return null;
    }

    @Override
    public void onAllMetaDataFinished(IX5WebViewExtension ix5WebViewExtension, HashMap<String, String> hashMap) {
        LogUtil.i(TAG,"onAllMetaDataFinished");
    }

    @Override
    public void onBackforwardFinished(int i) {
        LogUtil.i(TAG,"onBackforwardFinished"+i);
    }

    @Override
    public void onHitTestResultForPluginFinished(IX5WebViewExtension ix5WebViewExtension, IX5WebViewBase.HitTestResult hitTestResult, Bundle bundle) {
        LogUtil.i(TAG,"onHitTestResultForPluginFinished");
    }

    @Override
    public void onHitTestResultFinished(IX5WebViewExtension ix5WebViewExtension, IX5WebViewBase.HitTestResult hitTestResult) {
        LogUtil.i(TAG,"onHitTestResultFinished");
    }

    @Override
    public void onPromptScaleSaved(IX5WebViewExtension ix5WebViewExtension) {
        LogUtil.i(TAG,"onPromptScaleSaved");
    }

    @Override
    public void onPromptNotScalable(IX5WebViewExtension ix5WebViewExtension) {
        LogUtil.i(TAG,"onPromptNotScalable");
    }

    @Override
    public boolean onAddFavorite(IX5WebViewExtension ix5WebViewExtension, String s, String s1, JsResult jsResult) {
        LogUtil.i(TAG,"onAddFavorite");
        return false;
    }

    @Override
    public void onPrepareX5ReadPageDataFinished(IX5WebViewExtension ix5WebViewExtension, HashMap<String, String> hashMap) {
        LogUtil.i(TAG,"onPrepareX5ReadPageDataFinished");
    }

    @Override
    public boolean onSavePassword(String s, String s1, String s2, boolean b, Message message) {
        LogUtil.i(TAG,"onSavePassword");
        return false;
    }

    @Override
    public boolean onSavePassword(android.webkit.ValueCallback<String> valueCallback, String s, String s1, String s2, String s3, String s4, boolean b) {
        LogUtil.i(TAG,"onSavePasswordX");
        return false;
    }

    @Override
    public void onX5ReadModeAvailableChecked(HashMap<String, String> hashMap) {
        LogUtil.i(TAG,"onX5ReadModeAvailableChecked");
    }

    @Override
    public void addFlashView(View view, ViewGroup.LayoutParams layoutParams) {
        LogUtil.i(TAG,"addFlashView");
    }

    @Override
    public void h5videoRequestFullScreen(String s) {
        LogUtil.i(TAG,"h5videoRequestFullScreen");
    }

    @Override
    public void h5videoExitFullScreen(String s) {
        LogUtil.i(TAG,"h5videoExitFullScreen");
    }

    @Override
    public void requestFullScreenFlash() {
        LogUtil.i(TAG,"requestFullScreenFlash");
    }

    @Override
    public void exitFullScreenFlash() {
        LogUtil.i(TAG,"exitFullScreenFlash");
    }

    @Override
    public void jsRequestFullScreen() {
        LogUtil.i(TAG,"jsRequestFullScreen");
    }

    @Override
    public void jsExitFullScreen() {
        LogUtil.i(TAG,"jsExitFullScreen");
    }

    @Override
    public void acquireWakeLock() {
        LogUtil.i(TAG,"acquireWakeLock");
    }

    @Override
    public void releaseWakeLock() {
        LogUtil.i(TAG,"releaseWakeLock");
    }

    @Override
    public Context getApplicationContex() {
        LogUtil.i(TAG,"getApplicationContex");
        return null;
    }

    @Override
    public boolean onPageNotResponding(Runnable runnable) {
        LogUtil.i(TAG,"onPageNotResponding");
        return false;
    }

    @Override
    public Object onMiscCallBack(String s, Bundle bundle) {
        LogUtil.i(TAG,"onMiscCallBack");
        return null;
    }

    @Override
    public void openFileChooser(android.webkit.ValueCallback<Uri[]> valueCallback, String s, String s1) {
        LogUtil.i(TAG,"openFileChooser");

    }

    @Override
    public void onPrintPage() {
        LogUtil.i(TAG,"onPrintPage");
    }

    @Override
    public void onColorModeChanged(long l) {
        LogUtil.i(TAG,"onColorModeChanged");
    }

    @Override
    public boolean onPermissionRequest(String s, long l, MediaAccessPermissionsCallback mediaAccessPermissionsCallback) {
        LogUtil.i(TAG,"onPermissionRequest "+s+" "+l);
        //mediaAccessPermissionsCallback.invoke(s,MediaAccessPermissionsCallback.,);
        return true;
    }
}
