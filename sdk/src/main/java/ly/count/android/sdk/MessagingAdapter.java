package ly.count.android.sdk;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import java.lang.reflect.Method;

public class MessagingAdapter {
    private static final String TAG = "MessagingAdapter";
    private final static String MESSAGING_CLASS_NAME = "ly.count.android.sdk.messaging.CountlyMessaging";

    public static boolean isMessagingAvailable() {
        Log.d(TAG, "isMessageingAvailable 1");
        boolean messagingAvailable = false;
        try {
            Log.d(TAG, "isMessageingAvailable 2");
            Class.forName(MESSAGING_CLASS_NAME);
            messagingAvailable = true;
        } catch (ClassNotFoundException ignored) {
            Log.d(TAG, "isMessageingAvailable 3");
        }
        Log.d(TAG, "isMessageingAvailable 4 messagingAvailable = " + messagingAvailable);
        return messagingAvailable;
    }

    public static boolean init(Activity activity, Class<? extends Activity> activityClass, String sender, String[] buttonNames, Boolean disableUI, Integer customIconResId) {
        Log.d(TAG, "init");
        try {
            final Class<?> cls = Class.forName(MESSAGING_CLASS_NAME);
            final Method method = cls.getMethod("init", Activity.class, Class.class, String.class, String[].class, Boolean.class, Integer.class);
            method.invoke(null, activity, activityClass, sender, buttonNames, disableUI, customIconResId);
            return true;
        } catch (Throwable logged) {
            Log.e(TAG, "Couldn't init Countly Messaging", logged);
            return false;
        }
    }

    public static boolean storeConfiguration(Context context, String serverURL, String appKey, String deviceID, DeviceId.Type idMode) {
        Log.d(TAG, "storeConfiguration");
        try {
            final Class<?> cls = Class.forName(MESSAGING_CLASS_NAME);
            final Method method = cls.getMethod("storeConfiguration", Context.class, String.class, String.class, String.class, DeviceId.Type.class);
            method.invoke(null, context, serverURL, appKey, deviceID, idMode);
            return true;
        } catch (Throwable logged) {
            Log.e(TAG, "Couldn't store configuration in Countly Messaging", logged);
            return false;
        }
    }
}
