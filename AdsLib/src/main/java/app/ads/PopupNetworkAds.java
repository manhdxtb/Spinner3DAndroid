package app.ads;

import android.app.Activity;
import android.content.Context;

import androidx.fragment.app.FragmentActivity;

import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil;

public class PopupNetworkAds {

    public static boolean IS_PRO = true;

    private static final String KEY_TIME_LOAD_OPEN_APP_ADS = "TimeLoadOpenAppAds";
    private static final String KEY_TIME_LOAD_POPUP_ADS = "TimeLoadPopupAds";

    private static String screen_popup = "";

    public static String getScreen_popup() {
        return screen_popup;
    }

    public static void setScreen_popup(String screen_popup) {
        if (screen_popup == null) {
            screen_popup = "";
        }
        PopupNetworkAds.screen_popup = screen_popup;
    }

    public interface OnShowAdCompleteListener {
        void onCloseAdComplete();
    }

    private static long time_start_app_ads = 0;

    public static void setTimeStartAppAds() {
        time_start_app_ads = System.currentTimeMillis();
    }

    public static void notOpenAppAdsNow(Context context) {
        SharedAdsGlobalUtil.setLongValue(context, KEY_TIME_LOAD_OPEN_APP_ADS, System.currentTimeMillis() + 15000);
    }

    public static void notPopupAdsNow(Context context) {
        SharedAdsGlobalUtil.setLongValue(context, KEY_TIME_LOAD_POPUP_ADS, System.currentTimeMillis() + 1000);
    }

    public static void saveTimeOpenAppAds(Context context) {
        SharedAdsGlobalUtil.setLongValue(context, KEY_TIME_LOAD_OPEN_APP_ADS, System.currentTimeMillis());
    }

    public static void saveTimePopupAds(Context context) {
        SharedAdsGlobalUtil.setLongValue(context, KEY_TIME_LOAD_POPUP_ADS, System.currentTimeMillis());
    }

    public static boolean checkConditionOpenAppAds(Context context) {
        if (IS_PRO || !RemoteConfig.remote_ads_openapp_on) {
            return false;
        }

        int offset = RemoteConfig.remote_ads_offset_openapp;
        long timeLoadOpenAds = SharedAdsGlobalUtil.getLongValue(context, KEY_TIME_LOAD_OPEN_APP_ADS);
        long timeNow = System.currentTimeMillis();
        if (timeNow - timeLoadOpenAds > 1000L * offset) {
            return true;
        }
        return false;


//        return false;

    }

    public static boolean checkConditionPopupAds(Context context) {
        if (IS_PRO) {
            return false;
        }

        // Mặc định backTimePreload = 0
        // backTimePreload = 10 là còn 10 giây nữa là đạt điều kiện
        int backTimePreload = 0;
        int offset = RemoteConfig.remote_ads_interval;
        long timeLoadOpenAds = SharedAdsGlobalUtil.getLongValue(context, KEY_TIME_LOAD_POPUP_ADS);
        long timeNow = System.currentTimeMillis();
        if ((timeNow - timeLoadOpenAds) > 1000L * (offset - backTimePreload) &&
                (timeNow - time_start_app_ads) > 1000L * RemoteConfig.remote_time_start_show_popup) {
            return true;
        }
        return false;


//        return false;
    }

    public static boolean checkConditionLoadPopupAds(Context context) {
        if (IS_PRO) {
            return false;
        }

        // Mặc định backTimePreload = 0
        // backTimePreload = 10 là còn 10 giây nữa là đạt điều kiện
        int backTimePreload = 15;
        int offset = RemoteConfig.remote_ads_interval;
        long timeLoadOpenAds = SharedAdsGlobalUtil.getLongValue(context, KEY_TIME_LOAD_POPUP_ADS);
        long timeNow = System.currentTimeMillis();
        if ((timeNow - timeLoadOpenAds) > 1000L * (offset - backTimePreload) &&
                (timeNow - time_start_app_ads) > 1000L * RemoteConfig.remote_time_start_show_popup) {
            return true;
        }
        return false;
    }

    public static void initOpenAppAds(Activity activity) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setupOpenAppAds(activity);
            }
        });
    }

    private static void setupOpenAppAds(Context context) {
        if (RemoteConfig.remote_ads_openapp_on) {
            AdmobAds.setupOpenAppAds(context);
        }

        if (time_start_app_ads == 0) {
            setTimeStartAppAds();
        }
    }

    public static void setupPopupAds(Context context) {
        AdmobAds.setupPopupAdmobAds(context);

        if (time_start_app_ads == 0) {
            setTimeStartAppAds();
        }
    }

    public static boolean canShowPopupAdsNow(Context context) {
        boolean check = checkConditionPopupAds(context);
        if (check) {
            if (AdmobAds.isReadyPopup()) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private static long timeClickShowPopupAds = 0;

    private static void showNativeAdOverlay(FragmentActivity activity) {
        if (NativeAdmobOverlayAds.isAdLoaded()) {
            ShowNativeAdsOverlay.show(activity);
        }
    }

    public static void showPopupAds(FragmentActivity activity, String screen_name, OnShowAdCompleteListener listener) {
        long timeNow = System.currentTimeMillis();
        if (timeNow - timeClickShowPopupAds < 1000) {
            return;
        }
        timeClickShowPopupAds = timeNow;

        if (listener == null) {
            listener = new OnShowAdCompleteListener() {
                @Override
                public void onCloseAdComplete() {
                }
            };
        }

        try {
            BannerAds.hidenBannerAds(activity.findViewById(android.R.id.content));
        } catch (Exception e) {
        }

        setScreen_popup(screen_name);
        boolean check = checkConditionPopupAds(activity) && App.self().isQuyenShowPopupAds;
        if (check) {
            App.self().logEventAppsflyer("af_inters_ad_eligible");
            UIUtil.hideKeyboard(activity);
            if (AdmobAds.isReadyPopup()) {
                showNativeAdOverlay(activity);

                AdmobAds.showPopupAdmobAds(activity, listener);
            } else {
                listener.onCloseAdComplete();
            }
        } else {
            listener.onCloseAdComplete();
        }
    }

    public static void showPopupAdsViewPager(FragmentActivity activity, String screen_name, OnShowAdCompleteListener listener) {
        long timeNow = System.currentTimeMillis();
        if (timeNow - timeClickShowPopupAds < 1000) {
            return;
        }
        timeClickShowPopupAds = timeNow;

        if (listener == null) {
            listener = new OnShowAdCompleteListener() {
                @Override
                public void onCloseAdComplete() {
                }
            };
        }
        if (!RemoteConfig.remote_ads_popup_switch_home_category) {
            listener.onCloseAdComplete();
            return;
        }

        setScreen_popup(screen_name);
        boolean check = checkConditionPopupAds(activity) && App.self().isQuyenShowPopupAds;
        if (check) {
            App.self().logEventAppsflyer("af_inters_ad_eligible");
            UIUtil.hideKeyboard(activity);
            if (AdmobAds.isReadyPopup()) {
                try {
                    BannerAds.hidenBannerAds(activity.findViewById(android.R.id.content));
                } catch (Exception e) {
                }
                showNativeAdOverlay(activity);

                AdmobAds.showPopupAdmobAds(activity, listener);
            } else {
                listener.onCloseAdComplete();
            }
        } else {
            listener.onCloseAdComplete();
        }
    }

    public static void showPopupAdsNowKoDieuKien(FragmentActivity activity, OnShowAdCompleteListener listener) {
        long timeNow = System.currentTimeMillis();
        if (timeNow - timeClickShowPopupAds < 1000) {
            return;
        }
        timeClickShowPopupAds = timeNow;

        try {
            BannerAds.hidenBannerAds(activity.findViewById(android.R.id.content));
        } catch (Exception e) {
        }

        App.self().logEventAppsflyer("af_inters_ad_eligible");
        UIUtil.hideKeyboard(activity);
        if (AdmobAds.isReadyPopup()) {
            AdmobAds.showPopupAdmobAds(activity, listener);
        } else {
            listener.onCloseAdComplete();
        }
    }

    public static boolean isReadyPopup() {
        if (AdmobAds.isReadyPopup()) {
            return true;
        }
        return false;
    }

    public static void showOpenAppAds(FragmentActivity activity, OnShowAdCompleteListener listener) {
        boolean check = checkConditionOpenAppAds(activity) && App.self().isQuyenShowPopupAds;
        if (check) {
            App.self().logEventAppsflyer("af_AOA_displayed");
            if (AdmobAds.isReadyOpenApp()) {
                AdmobAds.showOpenAppAds(activity, listener);
            } else {
                listener.onCloseAdComplete();
            }
        } else {
            listener.onCloseAdComplete();
        }
    }

}
