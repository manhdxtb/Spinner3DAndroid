package app.ads;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdValue;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.OnPaidEventListener;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.appopen.AppOpenAd;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

import java.util.Arrays;
import java.util.List;

public class AdmobAds {

    public final static boolean IS_TEST = true;

    // Key Test Admob
    public static String key_admob_banner = "ca-app-pub-3940256099942544/9214589741";
    public static String key_admob_banner_collap = "ca-app-pub-3940256099942544/2014213617";
    public static String key_admob_native = "ca-app-pub-3940256099942544/2247696110";
    public static String key_admob_native_splash = "ca-app-pub-3940256099942544/2247696110";
    public static String key_admob_native_overlay = "ca-app-pub-3940256099942544/2247696110";
    public static String key_admob_openapp = "ca-app-pub-3940256099942544/9257395921";
    public static String key_admob_popup = "ca-app-pub-3940256099942544/1033173712";
    public static String key_admob_video_reward = "ca-app-pub-3940256099942544/5224354917";

    public static String key_admob_popup_resume = "ca-app-pub-3940256099942544/1033173712";
    public static String key_admob_popup_openapp = "ca-app-pub-3940256099942544/1033173712";
    public static String key_admob_native_language = "ca-app-pub-3940256099942544/2247696110";
    public static String key_admob_native_language_2 = "ca-app-pub-3940256099942544/2247696110";
    public static String key_admob_native_preview_small = "ca-app-pub-3940256099942544/2247696110";
    public static String key_admob_native_preview_full = "ca-app-pub-3940256099942544/2247696110";
    /// /////////////////////////////////


    private static final String LOG_TAG_POPUP = "Admob Popup";
    private static final String LOG_TAG_OPENAPP = "Admob OpenApp";

    private static Activity activity_class;
    private static boolean isInitAdmobDone = true, isLoadingOpenAppAds = false, isLoadingResumeAds = false, isLoadingPopupAdmobAds = false;

    private static long timeCacheOpenApp;
    private static AppOpenAd mOpenAppAds;
    public static PopupNetworkAds.OnShowAdCompleteListener onAdOpenApp;

    private static InterstitialAd mInterstitialSplash, mInterstitialResume;
    private static InterstitialAd mInterstitialAd;
    public static PopupNetworkAds.OnShowAdCompleteListener onAdPopupAdmob, onAdPopupResume;

    public static void setTestDevice() {
        List<String> testDeviceIds = Arrays.asList("91073C772DAE246B70737CCD89B18D0B", "E1140908270AB93C60D869BD922038D8", "25B1331CAF16C16379740B30C3B25ED0", "9BCC85DE600BAA005B804C60F5867FBD", "FAE1607EBCAF5A050D6D6CAE8CEF04D7", "8CE7C13C96DAEBB8A5B25D2DEF441894");
        RequestConfiguration configuration =
                new RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build();
        MobileAds.setRequestConfiguration(configuration);


//        MobileAds.openAdInspector(App.self(), new OnAdInspectorClosedListener() {
//            @Override
//            public void onAdInspectorClosed(@Nullable AdInspectorError adInspectorError) {
//            }
//        });
    }

    public static void setupKeyAds() {
        if (IS_TEST) {
            return;
        }

        if (App.self().getPackageName().equals("com.sas.holyquran.tasbih.prayer")) {
            key_admob_banner = "ca-app-pub-6274285024608859/3579502259";
            key_admob_banner_collap = "ca-app-pub-6274285024608859/8234236595";
            key_admob_native = "ca-app-pub-6274285024608859/2457992273";
            key_admob_popup = "ca-app-pub-6274285024608859/1623422750";
            key_admob_video_reward = "";

            key_admob_popup_resume = "ca-app-pub-6274285024608859/9510885169";
            key_admob_popup_openapp = "ca-app-pub-6274285024608859/5105510379";
            key_admob_native_splash = "ca-app-pub-6274285024608859/5763211843";
            key_admob_native_language = "ca-app-pub-6274285024608859/9655129475";
            key_admob_native_language_2 = "ca-app-pub-6274285024608859/5998955496";
            key_admob_native_preview_small = "ca-app-pub-6274285024608859/4450130175";
            key_admob_native_preview_full = "ca-app-pub-6274285024608859/3137048502";
            key_admob_native_overlay = "ca-app-pub-6274285024608859/3792428704";
        }
    }

    /// //////////////////////////////////////////////////////////////////////////


    public static boolean isInitAdmobDone() {
        return true;
    }

    public static boolean isReadyOpenApp() {
        if (mOpenAppAds != null || mInterstitialSplash != null) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isReadyPopup() {
        if (mInterstitialAd != null) {
            return true;
        } else {
            return false;
        }
    }

    public static void clearOpenAppAds() {
        mOpenAppAds = null;
        mInterstitialSplash = null;
        onAdOpenApp = null;
        isLoadingOpenAppAds = false;
        timeCacheOpenApp = 0;
    }

    public static void clearPopupAdmobAds() {
        mInterstitialAd = null;
        onAdPopupAdmob = null;
        isLoadingPopupAdmobAds = false;
    }

    public static void clearPopupResumeAds() {
        mInterstitialResume = null;
        onAdPopupResume = null;
        isLoadingResumeAds = false;
    }

    public static void initAdmob(Context context) {
        try {
            MobileAds.initialize(context, new OnInitializationCompleteListener() {
                @Override
                public void onInitializationComplete(InitializationStatus initializationStatus) {
                    isInitAdmobDone = true;
                    setTestDevice();
                }
            });

            setTestDevice();
        } catch (Exception e) {
            PopupNetworkAds.IS_PRO = true;
        }
    }

    // Uu tien truyen Activity this vao
    public static void setupOpenAppAds(Context context) {
        if (Math.abs(System.currentTimeMillis() - timeCacheOpenApp) > 3500000) {
            clearOpenAppAds();
        }

        boolean conditionOpenAppAds = PopupNetworkAds.checkConditionOpenAppAds(context);
        if (isInitAdmobDone && !isLoadingOpenAppAds && mOpenAppAds == null && mInterstitialSplash == null && conditionOpenAppAds) {
            loadOpenAppAds(context);
        }
    }

    private static void loadOpenAppAds(Context context) {
        isLoadingOpenAppAds = true;
        setTestDevice();

        loadPopupSplashAds(context, key_admob_popup_openapp);
    }

    public static void showOpenAppAds(FragmentActivity activity, PopupNetworkAds.OnShowAdCompleteListener listener) {
        AdmobAds.activity_class = activity;
        onAdOpenApp = listener;
        if (onAdOpenApp != null && (mOpenAppAds != null || mInterstitialSplash != null)) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (mOpenAppAds != null) {
                            mOpenAppAds.show(activity);
                        } else if (mInterstitialSplash != null) {
                            mInterstitialSplash.show(activity);
                        } else {
                            try {
                                if (onAdOpenApp != null) {
                                    onAdOpenApp.onCloseAdComplete();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (Exception e) {
                        try {
                            if (onAdOpenApp != null) {
                                onAdOpenApp.onCloseAdComplete();
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            });
        }
    }

    // Uu tien truyen Activity this vao
    public static void setupPopupAdmobAds(Context context) {
        boolean conditionPopupAds = PopupNetworkAds.checkConditionLoadPopupAds(context);
        if (isInitAdmobDone && !isLoadingPopupAdmobAds && mInterstitialAd == null && conditionPopupAds) {
            loadPopupAdmobAds(context);
        }
    }

    private static void loadPopupAdmobAds(Context context) {
        isLoadingPopupAdmobAds = true;
        setTestDevice();
        AdRequest request = new AdRequest.Builder().build();
        InterstitialAd.load(
                context, key_admob_popup, request,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(InterstitialAd interstitialAd) {
                        // Called when an app open ad has loaded.
                        App.self().logEventAppsflyer("af_inters_api_called");

                        Log.d(LOG_TAG_POPUP, "Ad Popup was loaded.");
                        isLoadingPopupAdmobAds = false;
                        mInterstitialAd = interstitialAd;
                        mInterstitialAd.setOnPaidEventListener(new OnPaidEventListener() {
                            @Override
                            public void onPaidEvent(@NonNull AdValue adValue) {
                                App.self().logAd_Impression_Admob_AppsFlyer(adValue, mInterstitialAd.getResponseInfo(), "Inter Ads");
                            }
                        });
                        mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                            @Override
                            public void onAdDismissedFullScreenContent() {
                                Log.d(LOG_TAG_POPUP, "Ad Popup dismissed fullscreen content.");
                                notShowPopupResumeAdsNow();

                                SharedAdsGlobalUtil.setValue(App.self(), "TIME_HOME_ACTION", "" + System.currentTimeMillis());

                                new Thread() {
                                    @Override
                                    public void run() {
                                        try {
                                            Thread.sleep(100);
                                            AdmobAds.activity_class.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    try {
                                                        if (onAdPopupAdmob != null) {
                                                            onAdPopupAdmob.onCloseAdComplete();
                                                        }
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                    clearPopupAdmobAds();

                                                    try {
                                                        PopupNetworkAds.setupPopupAds(App.self());
                                                    } catch (Exception e) {
                                                    }
                                                }
                                            });
                                        } catch (Exception e) {
                                            try {
                                                if (onAdPopupAdmob != null) {
                                                    onAdPopupAdmob.onCloseAdComplete();
                                                }
                                            } catch (Exception ex) {
                                                ex.printStackTrace();
                                            }
                                            clearPopupAdmobAds();
                                        }
                                    }
                                }.start();

                                PopupNetworkAds.saveTimePopupAds(context);

                                PopupNetworkAds.setScreen_popup(null);
                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(AdError adError) {
                                Log.e(LOG_TAG_POPUP, "Ad Popup failed to show fullscreen content  " + adError.getMessage());

                                SharedAdsGlobalUtil.setValue(App.self(), "TIME_HOME_ACTION", "" + System.currentTimeMillis());

                                new Thread() {
                                    @Override
                                    public void run() {
                                        try {
                                            Thread.sleep(100);
                                            AdmobAds.activity_class.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    try {
                                                        if (onAdPopupAdmob != null) {
                                                            onAdPopupAdmob.onCloseAdComplete();
                                                        }
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                    clearPopupAdmobAds();
                                                }
                                            });
                                        } catch (Exception e) {
                                            try {
                                                if (onAdPopupAdmob != null) {
                                                    onAdPopupAdmob.onCloseAdComplete();
                                                }
                                            } catch (Exception ex) {
                                                ex.printStackTrace();
                                            }
                                            clearPopupAdmobAds();
                                        }
                                    }
                                }.start();

                                PopupNetworkAds.saveTimePopupAds(context);

                                PopupNetworkAds.setScreen_popup(null);
                            }

                            @Override
                            public void onAdShowedFullScreenContent() {
                                new Thread() {
                                    @Override
                                    public void run() {
                                        try {
                                            Thread.sleep(1000);
                                            SharedAdsGlobalUtil.setValue(App.self(), "TIME_HOME_ACTION", "" + System.currentTimeMillis() + 5000);
                                        } catch (Exception e) {
                                        }
                                    }
                                }.start();
                                // Called when fullscreen content is shown.
                                Log.d(LOG_TAG_POPUP, "Ad Popup showed fullscreen content.");
                                notShowPopupResumeAdsNow(30000);

                                try {
                                    App.self().logEventFirebaseSAS("ad_inter_status", "ad_status", "success_" + PopupNetworkAds.getScreen_popup());
                                } catch (Exception e) {
                                }
                            }

                        });
                    }

                    @Override
                    public void onAdFailedToLoad(LoadAdError loadAdError) {
                        // Called when an app open ad has failed to load.
                        Log.d(LOG_TAG_POPUP, "" + loadAdError);
                        isLoadingPopupAdmobAds = false;
                        clearPopupAdmobAds();

                        PopupNetworkAds.saveTimePopupAds(context);
                    }
                });
    }

    public static void showPopupAdmobAds(Activity activity, PopupNetworkAds.OnShowAdCompleteListener listener) {
        AdmobAds.activity_class = activity;
        if (!ShowNativeAdsOverlay.isShow()) {
            onAdPopupAdmob = listener;
        } else {
            ShowNativeAdsOverlay.setListener(listener);
            onAdPopupAdmob = new PopupNetworkAds.OnShowAdCompleteListener() {
                @Override
                public void onCloseAdComplete() {
                    try {
                        ShowNativeAdsOverlay.self().startCountDownTimer();
                    } catch (Exception e) {
                    }
                }
            };
        }
        if (onAdPopupAdmob != null) {
            if (isReadyPopup()) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (mInterstitialAd != null) {
                                mInterstitialAd.show(activity);

                                App.self().logEventAppsflyer("af_inters_displayed");

                                notShowPopupResumeAdsNow();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            } else {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            onAdPopupAdmob.onCloseAdComplete();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }
    }

    private static void loadPopupSplashAds(Context context, String key_ads) {
        // Cache cho open app
        if (TextUtils.isEmpty(key_ads)) {
            return;
        }
        isLoadingOpenAppAds = true;
        AdRequest request = new AdRequest.Builder().build();
        InterstitialAd.load(
                context, key_ads, request,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(InterstitialAd interstitialAd) {
                        // Called when an app open ad has loaded.
                        App.self().logEventAppsflyer("af_inters_api_called");

                        Log.d(LOG_TAG_POPUP, "Splash Popup was loaded.");
                        isLoadingOpenAppAds = false;
                        timeCacheOpenApp = System.currentTimeMillis();
                        mInterstitialSplash = interstitialAd;
                        mInterstitialSplash.setOnPaidEventListener(new OnPaidEventListener() {
                            @Override
                            public void onPaidEvent(@NonNull AdValue adValue) {
                                App.self().logAd_Impression_Admob_AppsFlyer(adValue, mInterstitialSplash.getResponseInfo(), "Inter Ads");
                            }
                        });
                        mInterstitialSplash.setFullScreenContentCallback(new FullScreenContentCallback() {
                            @Override
                            public void onAdDismissedFullScreenContent() {
                                SharedAdsGlobalUtil.setValue(App.self(), "TIME_HOME_ACTION", "" + System.currentTimeMillis());
                                // Called when ad is dismissed.
                                // Set the ad reference to null so you don't show the ad a second time.
                                Log.d(LOG_TAG_POPUP, "Splash Popup dismissed fullscreen content.");

                                PopupNetworkAds.saveTimeOpenAppAds(App.self());
                                new Thread() {
                                    @Override
                                    public void run() {
                                        try {
                                            Thread.sleep(100);
                                            try {
                                                if (onAdOpenApp != null) {
                                                    onAdOpenApp.onCloseAdComplete();
                                                } else {
                                                    Log.e("XXX Admob", "NULL   Splash   onAdOpenApp   NULL   ERROR");
                                                }
                                            } catch (Exception e) {
                                                try {
                                                    activity_class.runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            try {
                                                                if (onAdOpenApp != null) {
                                                                    onAdOpenApp.onCloseAdComplete();
                                                                }
                                                            } catch (Exception ex) {
                                                            }
                                                        }
                                                    });
                                                } catch (Exception ex) {
                                                }
                                            }

                                            clearOpenAppAds();
                                        } catch (Exception e) {
                                        }
                                    }
                                }.start();

                                PopupNetworkAds.setScreen_popup(null);
                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(AdError adError) {
                                // Called when ad fails to show.
                                Log.e(LOG_TAG_POPUP, "Splash Popup failed to show fullscreen content.  " + adError.getMessage());
                                try {
                                    if (onAdOpenApp != null) {
                                        onAdOpenApp.onCloseAdComplete();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                clearOpenAppAds();

                                try {
                                    App.self().logEventFirebaseSAS("ad_inter_status", "ad_status", "fail_Splash_Screen",
                                            "fail_reason", adError.getMessage());
                                } catch (Exception e) {
                                }
                            }

                            @Override
                            public void onAdShowedFullScreenContent() {
                                new Thread() {
                                    @Override
                                    public void run() {
                                        try {
                                            Thread.sleep(1000);
                                            SharedAdsGlobalUtil.setValue(App.self(), "TIME_HOME_ACTION", "" + System.currentTimeMillis() + 5000);
                                        } catch (Exception e) {
                                        }
                                    }
                                }.start();
                                // Called when fullscreen content is shown.
                                Log.d(LOG_TAG_POPUP, "Splash Popup showed fullscreen content.");

                                try {
                                    App.self().logEventFirebaseSAS("ad_inter_status", "ad_status", "success_Splash_Screen");
                                } catch (Exception e) {
                                }
                            }

                        });
                    }

                    @Override
                    public void onAdFailedToLoad(LoadAdError loadAdError) {
                        Log.d(LOG_TAG_POPUP, loadAdError.getMessage());
                        isLoadingOpenAppAds = false;
                    }
                });
    }

    public static boolean isReadyResumeAds() {
        if (mInterstitialResume != null) {
            return true;
        } else {
            return false;
        }
    }

    private static long timeShowPopupResumeAds;

    public static void notShowPopupResumeAdsNow() {
        timeShowPopupResumeAds = System.currentTimeMillis();
    }

    public static void notShowPopupResumeAdsNow(int addTimeDelayMs) {
        timeShowPopupResumeAds = System.currentTimeMillis() + addTimeDelayMs;
    }

    public static void showPopupResumeAds(FragmentActivity activity, PopupNetworkAds.OnShowAdCompleteListener listener) {
        AdmobAds.activity_class = activity;
        onAdPopupResume = listener;
        if (mInterstitialResume != null && RemoteConfig.remote_ads_resume_app && App.self().isQuyenShowPopupAds
                && (System.currentTimeMillis() - timeShowPopupResumeAds) > 3000) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (mInterstitialResume != null) {
                            mInterstitialResume.show(activity);
                        } else {
                            if (onAdPopupResume != null) {
                                onAdPopupResume.onCloseAdComplete();
                            }
                        }
                    } catch (Exception e) {
                        mInterstitialResume = null;
                    }
                }
            });
        }
    }

    public static void setupPopupResumeAds(Context context) {
        boolean conditionResumeAds = true;
        if (isInitAdmobDone && !isLoadingResumeAds && mInterstitialResume == null && conditionResumeAds && RemoteConfig.remote_ads_resume_app) {
            loadPopupResumeAds(context, key_admob_popup_resume);
        }
    }

    private static void loadPopupResumeAds(Context context, String key_ads) {
        if (TextUtils.isEmpty(key_ads) || isLoadingResumeAds) {
            return;
        }
        isLoadingResumeAds = true;
        AdRequest request = new AdRequest.Builder().build();
        InterstitialAd.load(
                context, key_ads, request,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(InterstitialAd interstitialAd) {
                        Log.d(LOG_TAG_POPUP, "Popup Resume was loaded.");
                        isLoadingResumeAds = false;
                        mInterstitialResume = interstitialAd;
                        mInterstitialResume.setFullScreenContentCallback(new FullScreenContentCallback() {
                            @Override
                            public void onAdDismissedFullScreenContent() {
                                SharedAdsGlobalUtil.setValue(App.self(), "TIME_HOME_ACTION", "" + System.currentTimeMillis());
                                // Called when ad is dismissed.
                                // Set the ad reference to null so you don't show the ad a second time.
                                Log.d(LOG_TAG_POPUP, "Popup Resume dismissed fullscreen content.");

                                new Thread() {
                                    @Override
                                    public void run() {
                                        try {
                                            Thread.sleep(100);
                                            if (onAdPopupResume != null) {
                                                onAdPopupResume.onCloseAdComplete();
                                            }
                                            clearPopupResumeAds();
                                        } catch (Exception e) {
                                            clearPopupResumeAds();
                                            e.printStackTrace();
                                        }
                                    }
                                }.start();
                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(AdError adError) {
                                // Called when ad fails to show.
                                Log.e(LOG_TAG_POPUP, "Popup Resume failed to show fullscreen content.");
                                try {
                                    if (onAdPopupResume != null) {
                                        onAdPopupResume.onCloseAdComplete();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                clearPopupResumeAds();
                            }

                            @Override
                            public void onAdShowedFullScreenContent() {
                                SharedAdsGlobalUtil.setValue(App.self(), "TIME_HOME_ACTION", "" + System.currentTimeMillis() + 5000);
                                // Called when fullscreen content is shown.
                                Log.d(LOG_TAG_POPUP, "Popup Resume showed fullscreen content.");
                            }

                        });
                    }

                    @Override
                    public void onAdFailedToLoad(LoadAdError loadAdError) {
                        Log.d(LOG_TAG_POPUP, loadAdError.getMessage());
                        isLoadingResumeAds = false;
                        mInterstitialResume = null;
                    }
                });
    }
}