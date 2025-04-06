package tv.utao.x5;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import androidx.multidex.MultiDex;

import com.tencent.smtt.export.external.TbsCoreSettings;
import com.tencent.smtt.sdk.QbSdk;
import com.tencent.smtt.sdk.QbSdk.PreInitCallback;
import com.tencent.smtt.sdk.TbsListener;
import com.tencent.smtt.sdk.WebView;

import java.util.HashMap;
import java.util.UUID;

import tv.utao.x5.service.CrashHandler;
import tv.utao.x5.util.LogUtil;
import tv.utao.x5.util.ValueUtil;

public class MyApplication extends Application {

    private static Context context;
    private static final String TAG = "MyApplication";
   // private String DIR = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
   @Override
   protected void attachBaseContext(Context base) {
       super.attachBaseContext(base);
       MultiDex.install(base);
   }
   public static  String androidId=null;
    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.i(TAG, "onViewInitBegin: ");
        context = getApplicationContext();
        //initX5();会自动初始化
        androidId = Settings.System.getString(getContentResolver(), Settings.System.ANDROID_ID);
        if(null==androidId){
            LogUtil.i(TAG, "androidId: getUUID");
            androidId=getUUID();
        }
        CrashHandler.getInstance().init(this);
        CrashHandler.uploadExceptionToServer(this);
        try {
            System.setProperty("persist.sys.media.use-mediaDrm", "false");
        } catch (Exception e) {
            // 安全处理异常
            LogUtil.e("use-mediaDrm:"+e.getMessage());
        }
        //startX5WebProcessPreinitService();
        //initPieWebView();
    }
    private void  initX5(){
        // 先初始化设置，再初始化环境
        try {
            // 设置参数
            HashMap<String, Object> map = new HashMap<>();
            map.put(TbsCoreSettings.TBS_SETTINGS_USE_SPEEDY_CLASSLOADER, true);
            map.put(TbsCoreSettings.TBS_SETTINGS_USE_DEXLOADER_SERVICE, true);
            QbSdk.initTbsSettings(map);

            // 设置不依赖 WiFi 下载
            QbSdk.setDownloadWithoutWifi(true);

            // 禁止使用系统 WebView
            QbSdk.unForceSysWebView();

            // 初始化 X5 环境
            QbSdk.initX5Environment(getApplicationContext(), new QbSdk.PreInitCallback() {
                @Override
                public void onCoreInitFinished() {
                    Log.d("App", "X5 Core 初始化完成");
                }

                @Override
                public void onViewInitFinished(boolean success) {
                    Log.d("App", "X5 内核加载 " + (success ? "成功" : "失败"));
                    ValueUtil.putString(getApplicationContext(), "x5", success ? "ok" : "0");
                }
            });
        } catch (Exception e) {
            Log.e("App", "X5 初始化失败", e);
        }
    }
    public static Context getAppContext() {
        return context;
    }
    private static final String PROCESS = "tv.utao.x5";
    private void initPieWebView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            String processName = getProcessName(this);
            if (!PROCESS.equals(processName)) {
                WebView.setDataDirectorySuffix(getString(processName, "utao"));
            }
        }
    }
    private void initWebView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            String processName = getProcessName();
            WebView.setDataDirectorySuffix(processName);
        }
    }
    public String getProcessName(Context context) {
        if (context == null) return null;
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo processInfo : manager.getRunningAppProcesses()) {
            if (processInfo.pid == android.os.Process.myPid()) {
                return processInfo.processName;
            }
        }
        return null;
    }

    public String getString(String s, String defValue) {
        return isEmpty(s) ? defValue : s;
    }

    public boolean isEmpty(String s) {
        return s == null || s.trim().length() == 0;
    }

    public static Context getContext() {
        return context;
    }
    public static String getUUID() {
        String serial = null;
        String m_szDevIDShort = "随机两位数" +
                Build.BOARD.length() % 10 + Build.BRAND.length() % 10 +
                Build.CPU_ABI.length() % 10 + Build.DEVICE.length() % 10 +
                Build.DISPLAY.length() % 10 + Build.HOST.length() % 10 +
                Build.ID.length() % 10 + Build.MANUFACTURER.length() % 10 +
                Build.MODEL.length() % 10 + Build.PRODUCT.length() % 10 +
                Build.TAGS.length() % 10 + Build.TYPE.length() % 10 +
                Build.USER.length() % 10; //13 位
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                serial = "默认值";
            } else {
                serial = Build.SERIAL;
            }
            //API>=9 使用serial号
            return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
        } catch (Exception exception) {
            //serial需要一个初始化
            serial = "默认值"; // 随便一个初始化
        }
        //使用硬件信息拼凑出来的15位号码
        return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
    }

    /**
     * 启动X5 独立Web进程的预加载服务。优点：
     * 1、后台启动，用户无感进程切换
     * 2、启动进程服务后，有X5内核时，X5预加载内核
     * 3、Web进程Crash时，不会使得整个应用进程crash掉
     * 4、隔离主进程的内存，降低网页导致的App OOM概率。
     *
     * 缺点：
     * 进程的创建占用手机整体的内存，demo 约为 150 MB
     */
    private boolean startX5WebProcessPreinitService() {
        String currentProcessName = QbSdk.getCurrentProcessName(this);
        // 设置多进程数据目录隔离，不设置的话系统内核多个进程使用WebView会crash，X5下可能ANR
        WebView.setDataDirectorySuffix(QbSdk.getCurrentProcessName(this));
        LogUtil.i(TAG, currentProcessName);
        if (currentProcessName.equals(this.getPackageName())) {
            this.startService(new Intent(this, X5ProcessInitService.class));
            return true;
        }
        return false;
    }

    private static void resetSdk() {
        // 在调用TBS初始化、创建WebView之前进行如下配置
        HashMap<String, Object> map = new HashMap<>();
        map.put(TbsCoreSettings.TBS_SETTINGS_USE_SPEEDY_CLASSLOADER, true);
        map.put(TbsCoreSettings.TBS_SETTINGS_USE_DEXLOADER_SERVICE, true);
        QbSdk.initTbsSettings(map);
        QbSdk.setDownloadWithoutWifi(true);
//        QbSdk.disableAutoCreateX5Webview();
        //强制使用系统内核
        //QbSdk.forceSysWebView();

    }

    private void downSDk(){
        /* [new] 独立Web进程演示 */
        if (!startX5WebProcessPreinitService()) {
            return;
        }
        //TbsDownloader.startDownload(this);
        LogUtil.i(TAG, "onViewInitBegin: ");
        /* 设置允许移动网络下进行内核下载。默认不下载，会导致部分一直用移动网络的用户无法使用x5内核 */
        // resetSdk();
        QbSdk.setDownloadWithoutWifi(true);

        //QbSdk.setCoreMinVersion(QbSdk.CORE_VER_ENABLE_202112);
        /* SDK内核初始化周期回调，包括 下载、安装、加载 */

        QbSdk.setTbsListener(new TbsListener() {

            /**
             * @param stateCode 用户可处理错误码请参考{@link com.tencent.smtt.sdk.TbsCommonCode}
             */
            @Override
            public void onDownloadFinish(int stateCode) {
                LogUtil.i(TAG, "onDownloadFinished: " + stateCode);
            }

            /**
             * @param stateCode 用户可处理错误码请参考{@link com.tencent.smtt.sdk.TbsCommonCode}
             */
            @Override
            public void onInstallFinish(int stateCode) {
                LogUtil.i(TAG, "onInstallFinished: " + stateCode);
            }

            /**
             * 首次安装应用，会触发内核下载，此时会有内核下载的进度回调。
             * @param progress 0 - 100
             */
            @Override
            public void onDownloadProgress(int progress) {
                LogUtil.i(TAG, "Core Downloading: " + progress);
            }
        });

        /* 此过程包括X5内核的下载、预初始化，接入方不需要接管处理x5的初始化流程，希望无感接入 */
        QbSdk.initX5Environment(this, new PreInitCallback() {
            @Override
            public void onCoreInitFinished() {
                // 内核初始化完成，可能为系统内核，也可能为系统内核

            }

            /**
             * 预初始化结束
             * 由于X5内核体积较大，需要依赖wifi网络下发，所以当内核不存在的时候，默认会回调false，此时将会使用系统内核代替
             * 内核下发请求发起有24小时间隔，卸载重装、调整系统时间24小时后都可重置
             * 调试阶段建议通过 WebView 访问 debugtbs.qq.com -> 安装线上内核 解决
             * @param isX5 是否使用X5内核
             */
            @Override
            public void onViewInitFinished(boolean isX5) {
                LogUtil.i(TAG, "onViewInitFinished: " + isX5);
                // hint: you can use QbSdk.getX5CoreLoadHelp(context) anytime to get help.
            }
        });
    }

}