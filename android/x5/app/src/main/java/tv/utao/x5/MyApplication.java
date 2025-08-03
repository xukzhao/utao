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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import tv.utao.x5.service.CrashHandler;
import tv.utao.x5.util.LogUtil;
import tv.utao.x5.util.ValueUtil;

public class MyApplication extends Application implements InvocationHandler {

    private static Context context;
    private String realPackageName;
    private String targetPackageName="android";
    private Object originalPM;
    private static final String TAG = "MyApplication";
    private final List<String> prefixes = Arrays.asList("com", "org", "net", "io", "app");
   // private String DIR = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
   @Override
   protected void attachBaseContext(Context base) {
       realPackageName = base.getPackageName();
       hookPackageManager(base);
       super.attachBaseContext(base);
       MultiDex.install(base);
   }
   public static  String androidId=null;

   @Override
   public String  getPackageName(){
       for (StackTraceElement trace : Thread.currentThread().getStackTrace()) {
           if ("org.chromium.base.BuildInfo" .equalsIgnoreCase( trace.getClassName())) {
               String m = trace.getMethodName();
               // 判断当前调用是否来自 Chromium（BuildInfo 类的方法）
               // 在部分旧版是 getAll 或 getPackageName 在较新版本是 <init>
               if (m.equalsIgnoreCase("getAll") || m.equalsIgnoreCase("getPackageName") || m.equalsIgnoreCase("<init>")) {
                   return targetPackageName;
               }
              // return "";// 返回空字符串移除包名
           }
       }
       return super.getPackageName();// 其他场景返回真实包名
   }
    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.i(TAG, "onViewInitBegin: ");
        targetPackageName=generateRandomPackageName();
        allErrorCatch();
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
    private String randomStr(int length){
        StringBuilder result = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            // 生成97-122之间的随机整数，对应ASCII码中的a-z
            int randomInt = random.nextInt(26) + 97;
            result.append((char) randomInt);
        }
        return result.toString();
    }
    private String generateRandomPackageName() {
        // 生成随机包名（格式：com.xxx.yyy.zzz）
        String prefix = prefixes.get(new Random().nextInt(prefixes.size()));
        String segment =randomStr(4);
        String suffix = randomStr(3);
        return prefix + "." + segment + "." + suffix;
    }
    private void hookPackageManager(Context context) {
        try {
            // 获取ActivityThread实例
            Class<?> activityThreadClass = Class.forName("android.app.ActivityThread");
            Method currentActivityThreadMethod = activityThreadClass.getMethod("currentActivityThread");
            Object activityThread = currentActivityThreadMethod.invoke(null);

            // 获取原始IPackageManager
            java.lang.reflect.Field sPackageManagerField = activityThreadClass.getDeclaredField("sPackageManager");
            sPackageManagerField.setAccessible(true);
            originalPM = sPackageManagerField.get(activityThread);

            // 创建代理并替换
            Object proxy = createProxy();
            sPackageManagerField.set(activityThread, proxy);

            // 替换Context中的mPM
            java.lang.reflect.Field mPMField = context.getPackageManager().getClass().getDeclaredField("mPM");
            mPMField.setAccessible(true);
            mPMField.set(context.getPackageManager(), proxy);

            Log.d("PackageHook", "Hook成功: 拦截" + targetPackageName + "请求");
        } catch (Exception e) {
            Log.e("PackageHook", "Hook失败: " + e.getMessage());
            e.printStackTrace();
            targetPackageName="android";
        }
    }
    private Object createProxy() throws ClassNotFoundException {
        return Proxy.newProxyInstance(
                Class.forName("android.content.pm.IPackageManager").getClassLoader(),
                new Class<?>[]{Class.forName("android.content.pm.IPackageManager")},
                this);
    }
    private void allErrorCatch(){
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable throwable) {
                // 检查是否为 SurfaceTexture 相关异常
                if (throwable != null && throwable.getMessage() != null &&
                        (throwable instanceof NullPointerException) &&
                        (throwable.getStackTrace() != null && throwable.getStackTrace().length > 0 &&
                                containsSurfaceTextureInStackTrace(throwable.getStackTrace()))) {

                    LogUtil.e("Application", "捕获到 SurfaceTexture 相关异常: " + throwable.getMessage());

                    // 记录异常但不终止应用
                    return;
                }

                // 其他异常，使用默认处理器
                Thread.UncaughtExceptionHandler defaultHandler = Thread.getDefaultUncaughtExceptionHandler();
                if (defaultHandler != null) {
                    defaultHandler.uncaughtException(thread, throwable);
                }
            }

            // 检查堆栈跟踪是否包含 SurfaceTexture 相关内容
            private boolean containsSurfaceTextureInStackTrace(StackTraceElement[] stackTrace) {
                for (StackTraceElement element : stackTrace) {
                    if (element.getClassName().contains("SurfaceTexture") ||
                            element.getMethodName().contains("SurfaceTexture")) {
                        return true;
                    }
                }
                return false;
            }
        });
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

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 检查是否有参数，并且第一个参数是 targetPackageName
        if (args != null && args.length > 0 && targetPackageName.equals(args[0])) {
            // 修改 args 的第一个参数为 realPackageName（自身包名）
            Object[] modifiedArgs = args.clone();
            modifiedArgs[0] = realPackageName;
            // 仍然调用原始方法 originalPM，并返回结果
            return method.invoke(originalPM, modifiedArgs);
        }
        // 否则正常调用原始方法
        return method.invoke(originalPM, args);
    }
}