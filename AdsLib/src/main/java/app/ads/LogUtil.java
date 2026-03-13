package app.ads;

public class LogUtil {

    private final static boolean isLogcat = true;
    private final static String TAG = "XXX";

    public static void d(String msg) {
        if (!isLogcat)
            return;
        android.util.Log.d(TAG, msg);
    }

    public static void e(String header, Exception e) {
        if (!isLogcat)
            return;
        e(header + ": " + e.getMessage());
    }

    public static void e(String header, Throwable throwable) {
        if (!isLogcat)
            return;
        e(header + ": " + throwable.getMessage());
    }

    public static void e(String msg) {
        if (!isLogcat)
            return;
        android.util.Log.e(TAG, msg);
    }

    public static void i(String msg) {
        if (!isLogcat)
            return;
        android.util.Log.i(TAG, msg);
    }

    public static void v(String msg) {
        if (!isLogcat)
            return;
        android.util.Log.v(TAG, msg);
    }

    public static void w(String msg) {
        if (!isLogcat)
            return;
        android.util.Log.w(TAG, msg);
    }

    public static void d(Object object) {
        if (!isLogcat)
            return;
        android.util.Log.i(TAG, object.toString());
    }
}
