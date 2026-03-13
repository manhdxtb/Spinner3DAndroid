package app.ads;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;

public class SharedAdsGlobalUtil {
    public final static String LANGUAGE = "LANGUAGE";
    public static final String SHARED_PREFERENCES_GLOBAL = "Shared_Preferences";

    public static String getValue(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                SHARED_PREFERENCES_GLOBAL,
                Context.MODE_PRIVATE);
        String value = sharedPreferences.getString(key, null);
        return value;
    }

    public static void setValue(Context context, String key, String value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                SHARED_PREFERENCES_GLOBAL,
                Context.MODE_PRIVATE);
        Editor editor = sharedPreferences.edit();
        if (value != null) {
            editor.putString(key, value);
        } else {
            editor.remove(key);
        }
        editor.commit();
    }

    public static long getLongValue(Context context, String key) {
        String valueStr = getValue(context, key);
        if (TextUtils.isEmpty(valueStr)) {
            valueStr = "0";
        }
        try {
            long value = Long.parseLong(valueStr);
            return value;
        } catch (Exception e) {
            return 0;
        }
    }

    public static void setLongValue(Context context, String key, long value) {
        setValue(context, key, "" + value);
    }

    public final static String getLanguage(Context context) {
        SharedPreferences sh = context.getSharedPreferences(
                SHARED_PREFERENCES_GLOBAL,
                Context.MODE_PRIVATE);
        return sh.getString(LANGUAGE, "en").toLowerCase();
    }

    public final static void setLanguage(Context context, String name) {
        SharedPreferences sh = context.getSharedPreferences(
                SHARED_PREFERENCES_GLOBAL,
                Context.MODE_PRIVATE);
        sh.edit().putString(LANGUAGE, name.toLowerCase()).apply();
    }

    public static boolean isVipSub() {
        int vip = (int) getLongValue(App.self(), "IS_VIP_SUB");
        if (vip == 0) {
            return false;
        } else {
            return true;
        }
    }

    public static void setVipSub(boolean isVip) {
        if (isVip) {
            PopupNetworkAds.IS_PRO = true;
            setLongValue(App.self(), "IS_VIP_SUB", 1);
        } else {
            setLongValue(App.self(), "IS_VIP_SUB", 0);
        }
    }

    public static boolean isUnlockTheme(Context context, String codeTheme, long price) {
        if (PopupNetworkAds.IS_PRO) {
            return true;
        }

        if (price == 0) {
            return true;
        } else {
            String valueStr = getValue(context, "Unlock_" + codeTheme);
            if (TextUtils.isEmpty(valueStr)) {
                valueStr = "false";
            }
            try {
                boolean value = Boolean.parseBoolean(valueStr);
                return value;
            } catch (Exception e) {
                return false;
            }
        }
    }

    public static void setUnlockTheme(Context context, String codeTheme) {
        setValue(context, "Unlock_" + codeTheme, "" + true);
    }

    public static boolean isUnlockTheme_FunctionCC(Context context, String codeTheme, long price) {
        if (PopupNetworkAds.IS_PRO) {
            return true;
        }

        if (price == 0) {
            return true;
        } else {
            String valueStr = getValue(context, "Unlock_FunctionCC_" + codeTheme);
            if (TextUtils.isEmpty(valueStr)) {
                valueStr = "false";
            }
            try {
                boolean value = Boolean.parseBoolean(valueStr);
                return value;
            } catch (Exception e) {
                return false;
            }
        }
    }

    public static void setUnlockTheme_FunctionCC(Context context, String codeTheme) {
        setValue(context, "Unlock_FunctionCC_" + codeTheme, "" + true);
    }

    public static long getTimeAutoRate(Context context) {
        return getLongValue(context, "TimeAutoRate");
    }

    public static void setTimeAutoRate(Context context) {
        setLongValue(context, "TimeAutoRate", System.currentTimeMillis());
    }

    public static long getCoin(Context context) {
        return getLongValue(context, "COIN_TOTAL");
    }

    public static boolean isUnlock_Widget(Context context, String codeTheme, String typeWidget, long price) {
        if (PopupNetworkAds.IS_PRO) {
            return true;
        }

        if (price == 0) {
            return true;
        } else {
            String valueStr = getValue(context, "Unlock_Widget_" + codeTheme + "_" + typeWidget);
            if (TextUtils.isEmpty(valueStr)) {
                valueStr = "false";
            }
            try {
                boolean value = Boolean.parseBoolean(valueStr);
                return value;
            } catch (Exception e) {
                return false;
            }
        }
    }

    public static void setUnlock_Widget(Context context, String codeTheme, String typeWidget) {
        setValue(context, "Unlock_Widget_" + codeTheme + "_" + typeWidget, "" + true);
    }

}
