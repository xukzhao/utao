package tv.utao.x5.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

/**
 * 安全的 Toast 工具类，避免 "View has already been added to the window manager" 错误
 */
public class ToastUtils {
    private static final Map<Integer, Toast> toastMap = new HashMap<>();
    private static final Handler mainHandler = new Handler(Looper.getMainLooper());

    /**
     * 显示短时间 Toast
     * @param context 上下文
     * @param message 消息内容
     */
    public static void showShort(Context context, String message) {
        show(context, message, Toast.LENGTH_SHORT);
    }

    /**
     * 显示长时间 Toast
     * @param context 上下文
     * @param message 消息内容
     */
    public static void showLong(Context context, String message) {
        show(context, message, Toast.LENGTH_LONG);
    }

    /**
     * 显示 Toast
     * @param context 上下文
     * @param message 消息内容
     * @param duration 持续时间
     */
    public static void show(final Context context, final String message, final int duration) {
        if (context == null) return;
        
        // 确保在主线程中执行
        if (Looper.myLooper() != Looper.getMainLooper()) {
            mainHandler.post(() -> show(context, message, duration));
            return;
        }

        // 生成唯一键，用于标识不同消息的 Toast
        final int key = (message + duration).hashCode();
        
        // 取消之前的 Toast
        if (toastMap.containsKey(key)) {
            toastMap.get(key).cancel();
            toastMap.remove(key);
        }
        
        // 创建新的 Toast
        Toast toast = Toast.makeText(context.getApplicationContext(), message, duration);
        toastMap.put(key, toast);
        
        // 显示 Toast
        toast.show();
        
        // 设置定时器，在 Toast 消失后从 Map 中移除
        mainHandler.postDelayed(() -> {
            if (toastMap.containsKey(key)) {
                toastMap.remove(key);
            }
        }, duration == Toast.LENGTH_SHORT ? 2000 : 3500);
    }

    /**
     * 取消所有 Toast
     */
    public static void cancelAll() {
        for (Toast toast : toastMap.values()) {
            toast.cancel();
        }
        toastMap.clear();
    }
} 