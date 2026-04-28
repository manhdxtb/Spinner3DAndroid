package app.ads;

import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.google.android.ads.nativetemplates.R;
import com.google.android.ads.nativetemplates.TemplateView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import org.json.JSONObject;


public class SplashAction {

    public static FragmentActivity activity;
    private static SplashNextActivity splashNextActivity;
    private Handler handler;
    private TemplateView adsNativeSplash;

    public SplashAction(FragmentActivity activity, SplashNextActivity splashNextActivity) {
        SplashAction.activity = activity;
        SplashAction.splashNextActivity = splashNextActivity;
        handler = new Handler();
        adsNativeSplash = activity.findViewById(R.id.app_nativeads);

        NativeAdmobAds.destroyNative();
        AdmobAds.setupKeyAds();
        AdmobAds.initAdmob(activity);
        startLoadDataServer();
    }

    private void startLoadDataServer() {
        updateRemoteConfig();
        loadMessGDPR();
    }

    private void loadMessGDPR() {
        if (PopupNetworkAds.IS_PRO) {
            startThreadShowAdsOpenApp(10);
            return;
        }

        long timeNow = System.currentTimeMillis();
        long timeLastCheck = SharedAdsGlobalUtil.getLongValue(activity, "TIME_CHECK_MESSGDPR");

        // Check GDPR định kỳ (Ví dụ: 999000000L ~ 11 ngày)
        if (timeNow - timeLastCheck > 999000000L) {
            MessGDPR.getInstance().onCreate(activity, new MessGDPR.MessGDPRListener() {
                @Override
                public void onDone(boolean canRequestAds) {
                    SharedAdsGlobalUtil.setLongValue(activity, "TIME_CHECK_MESSGDPR", System.currentTimeMillis());
                    // Dù canRequestAds là true hay false, ta vẫn init Ads
                    // (SDK AdMob sẽ tự handle việc có load ads thật hay không dựa trên consent)
                    startInitAds(100);
                }

                @Override
                public void onError() {
                    // Fail-safe: Nếu lỗi thì cứ cho vào app và init ads bình thường
                    Log.e("GDPR", "UMP Error, proceeding to init ads");
                    startInitAds(100);
                }
            });
        } else {
            startInitAds(100);
        }
    }

    private void startInitAds(int timeDelay) {
        PopupNetworkAds.setTimeStartAppAds();

        AdmobAds.initAdmob(activity);

        if (RemoteConfig.remote_ads_openapp_on) {
            startThreadShowAdsOpenApp(timeDelay);

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        PopupNetworkAds.initOpenAppAds(activity);
                        NativeAdmobAds.loadNativeSplash();
                    } catch (Exception e) {
                    }
                }
            }, 500);

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        loadNativeAdsLanguage();
                    } catch (Exception e) {
                    }
                }
            }, 1500);
        } else {
            startThreadShowAdsOpenApp(10);
        }
    }

    private void startThreadShowAdsOpenApp(int maxCount) {
        new Thread() {
            @Override
            public void run() {
                int i, max_i = maxCount;
                for (i = 0; i < max_i; i++) {
                    try {
                        Thread.sleep(99);
                    } catch (Exception e) {
                    }

                    try {
                        if (RemoteConfig.remote_native_splash && NativeAdmobAds.getTotalNativeAds() > 0 && adsNativeSplash.getVisibility() != View.VISIBLE) {
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        NativeAdmobAds.showNativeAd(adsNativeSplash);
                                    } catch (Exception e) {
                                    }
                                }
                            });
                            try {
                                Thread.sleep(1000);
                            } catch (Exception e) {
                            }
                        }
                    } catch (Exception e) {
                    }

                    if (i == 15) {
                        loadNativeAdsLanguage();
                    }

                    if (AdmobAds.isInitAdmobDone()) {
                        if (PopupNetworkAds.checkConditionOpenAppAds(activity)) {
                            if (AdmobAds.isReadyOpenApp()) {
                                loadNativeAdsLanguage();
                                PopupNetworkAds.showOpenAppAds(activity, new PopupNetworkAds.OnShowAdCompleteListener() {
                                    @Override
                                    public void onCloseAdComplete() {
                                        splashNextActivity.gotoNextActivity();
                                    }
                                });
                                break;
                            }
                        } else {
                            Log.e("Admob Applovin Ads", "Admob nextMainActivity    NOT  ConditionOpenAppAds");
                            try {
                                splashNextActivity.gotoNextActivity();
                                break;
                            } catch (Exception e) {
                            }
                        }
                    }
                }
                if (i >= max_i) {
                    Log.e("Admob Applovin Ads", "nextMainActivity    i >= 100");
                    try {
                        splashNextActivity.gotoNextActivity();
                    } catch (Exception e) {
                    }
                }
            }
        }.start();
    }

    private boolean isLoadNativeAdsLanguage;

    private void loadNativeAdsLanguage() {
        if (!isLoadNativeAdsLanguage) {
            isLoadNativeAdsLanguage = true;
            try {
                int skip_intro = (int) SharedAdsGlobalUtil.getLongValue(App.self(), "SKIP_INTRO");
                if (skip_intro == 0) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                NativeAdmobAdsLanguage.loadNativeAd();
                            } catch (Exception e) {
                            }
                        }
                    });
                }
            } catch (Exception e) {
            }
        }
    }

    private void updateRemoteConfig() {
        String jsonDefault = "{\n" +
                "    \"ads_interval\": \"10\",\n" +
                "    \"time_start_show_popup\": \"0\",\n" +
                "    \"ads_offset_openapp\": \"30\",\n" +
                "    \"max_native_ads\": \"8\",\n" +
                "    \"ads_native_on\": \"true\",\n" +
                "    \"buy_vip_active\": \"true\",\n" +
                "    \"native_splash\": \"true\",\n" +
                "    \"ads_openapp_on\": \"true\",\n" +
                "    \"show_language_ads\": \"true\",\n" +
                "    \"show_onboarding_preview\": \"true\",\n" +
                "    \"time_reload_collap_ad\": \"30\",\n" +
                "    \"show_collap_ad\": \"true\",\n" +
                "    \"show_banner_ads_home_top\": \"true\",\n" +
                "    \"show_native_som_on_list\": \"true\",\n" +
                "    \"rating_popup\": \"true\",\n" +
                "    \"update_fast_weather\": \"false\",\n" +
                "    \"show_language_screen\": \"true\",\n" +
                "    \"time_restart_home_screen\": \"180\",\n" +
                "    \"show_banner_ads_home\": \"2\",\n" +
                "    \"ads_popup_switch_home_category\": \"true\"\n" +
                "}";
        FirebaseRemoteConfig mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(3600)
                .build();
        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings);
        mFirebaseRemoteConfig.fetchAndActivate()
                .addOnCompleteListener(activity, new OnCompleteListener<Boolean>() {
                    @Override
                    public void onComplete(@NonNull Task<Boolean> task) {
                        String jsonConfig = null;
                        try {
                            jsonConfig = mFirebaseRemoteConfig.getString("config_ads_lib");
                        } catch (Exception e) {
                        }
                        if (TextUtils.isEmpty(jsonConfig) || !task.isSuccessful()) {
                            jsonConfig = jsonDefault;
                        }
                        try {
                            JSONObject jsonObject = new JSONObject(jsonConfig);

                            RemoteConfig.remote_ads_interval = (int) jsonObject.getLong("ads_interval");
                            RemoteConfig.remote_ads_offset_openapp = (int) jsonObject.getLong("ads_offset_openapp");
                            RemoteConfig.remote_rating_popup = jsonObject.getBoolean("rating_popup");
                            RemoteConfig.remote_show_collap_ad = jsonObject.getBoolean("show_collap_ad");
                            RemoteConfig.remote_ads_resume_app = jsonObject.getBoolean("ads_resume_app");
                            RemoteConfig.remote_time_reload_collap_ad = (int) jsonObject.getLong("time_reload_collap_ad");
                            RemoteConfig.remote_show_banner_ads_home = (int) jsonObject.getLong("show_banner_ads_home");
                            RemoteConfig.remote_time_start_show_popup = (int) jsonObject.getLong("time_start_show_popup");
                            RemoteConfig.remote_show_banner_ads_home_top = jsonObject.getBoolean("show_banner_ads_home_top");
                            RemoteConfig.remote_show_language_ads = jsonObject.getBoolean("show_language_ads");
                            RemoteConfig.remote_ads_native_on = jsonObject.getBoolean("ads_native_on");
                            RemoteConfig.remote_ads_native_overlay = jsonObject.getBoolean("ads_native_overlay");
                            RemoteConfig.remote_ads_openapp_on = jsonObject.getBoolean("ads_openapp_on");
                            RemoteConfig.remote_native_splash = jsonObject.getBoolean("native_splash");
                            RemoteConfig.remote_show_native_som_on_list = jsonObject.getBoolean("show_native_som_on_list");
                            RemoteConfig.remote_show_language_screen = jsonObject.getBoolean("show_language_screen");
                            RemoteConfig.remote_show_onboarding_preview = jsonObject.getBoolean("show_onboarding_preview");
                            RemoteConfig.remote_ads_popup_switch_home_category = jsonObject.getBoolean("ads_popup_switch_home_category");
                            try {
                                RemoteConfig.remote_max_native_ads = (int) jsonObject.getLong("max_native_ads");
                            } catch (Exception e) {
                            }
                            try {
                                RemoteConfig.remote_buy_vip_active = jsonObject.getBoolean("buy_vip_active");
                            } catch (Exception e) {
                            }

                            RemoteConfig.saveToPreferences();
                        } catch (Exception e) {
                        }
                    }
                });

    }

    public interface SplashNextActivity {
        public void gotoNextActivity();
    }
}
