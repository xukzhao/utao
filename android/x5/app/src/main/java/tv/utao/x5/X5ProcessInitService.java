package tv.utao.x5;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.tencent.smtt.sdk.QbSdk;
import com.tencent.smtt.sdk.QbSdk.PreInitCallback;

import tv.utao.x5.util.LogUtil;

public class X5ProcessInitService extends Service {

    private static final String TAG = "X5ProcessInitService";

    @Override
    public void onCreate() {
        /* 只进行本地内核的预加载、不做版本检测及内核下载 */
        QbSdk.preInit(this.getApplicationContext(), new PreInitCallback() {
            @Override
            public void onCoreInitFinished() {

            }

            @Override
            public void onViewInitFinished(boolean b) {
                LogUtil.i(TAG, "init web process x5: " + b);
            }
        });
    }

    @Override
    public IBinder onBind(Intent intent) {
        LogUtil.i(TAG, "Service OnBind");
        return null;
    }
}