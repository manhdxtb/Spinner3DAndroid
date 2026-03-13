package app.ads;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import com.google.android.ads.nativetemplates.NativeTemplateStyle;
import com.google.android.ads.nativetemplates.TemplateView;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdValue;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MediaAspectRatio;
import com.google.android.gms.ads.OnPaidEventListener;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAd.OnNativeAdLoadedListener;
import com.google.android.gms.ads.nativead.NativeAdOptions;

import java.util.ArrayList;

public class NativeAdmobAds {

    private static int MAX_NATIVE_ADS = 8;
    private static String LOG_TAG = "Admob Ads";

    private static boolean adLoaded = false;
    private static Context context;
    private static ArrayList<NativeAd> nativeAds;
    private static long timeAddAds;
    private static int indexShowNative = 0;
    private static TemplateView template, template_temp;

    public static void loadNativeAd(Activity activity, int numLoad) {
        if (RemoteConfig.remote_max_native_ads > 0) {
            MAX_NATIVE_ADS = RemoteConfig.remote_max_native_ads;
        }
        new Thread() {
            @Override
            public void run() {
                try {
                    for (int i = 0; i < numLoad; i++) {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                loadNativeAdOneTime();
                            }
                        });

                        Thread.sleep(4000);
                    }
                } catch (Exception e) {
                }
            }
        }.start();
    }

    public static void loadNativeSplash() {
        context = App.self();

        if (PopupNetworkAds.IS_PRO || !RemoteConfig.remote_ads_native_on || !RemoteConfig.remote_native_splash || TextUtils.isEmpty(AdmobAds.key_admob_native_splash)) {
            return;
        }

        if (nativeAds == null || nativeAds.size() < MAX_NATIVE_ADS) {
            timeAddAds = System.currentTimeMillis();
            AdLoader adLoader = new AdLoader.Builder(context, AdmobAds.key_admob_native_splash)
                    .forNativeAd(new OnNativeAdLoadedListener() {

                        @Override
                        public void onNativeAdLoaded(@NonNull NativeAd ad) {
                            if (nativeAds == null) {
                                nativeAds = new ArrayList<>();
                            }
                            ad.setOnPaidEventListener(new OnPaidEventListener() {
                                @Override
                                public void onPaidEvent(@NonNull AdValue adValue) {
                                    App.self().logAd_Impression_Admob_AppsFlyer(adValue, ad.getResponseInfo(), "Native Ads");
                                }
                            });
                            nativeAds.add(ad);
                            timeAddAds = System.currentTimeMillis();
                            adLoaded = true;
                            Log.d(LOG_TAG, "Splash NativeAd was loaded  " + nativeAds.size());
                        }
                    })
                    .withAdListener(new AdListener() {
                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError error) {
                            LogUtil.e("Splash NativeAd Admob error  " + error.getMessage());
                        }

                        @Override
                        public void onAdLoaded() {
                        }
                    })
                    .withNativeAdOptions(new NativeAdOptions.Builder().setMediaAspectRatio(MediaAspectRatio.LANDSCAPE).build())
                    .build();

            AdRequest adRequest = new AdRequest.Builder().build();
            adLoader.loadAd(adRequest);
        }
    }

    private static void loadNativeAdOneTime() {
        context = App.self();

        if (PopupNetworkAds.IS_PRO || !RemoteConfig.remote_ads_native_on) {
            return;
        }

        if (nativeAds == null || nativeAds.size() < MAX_NATIVE_ADS) {
            timeAddAds = System.currentTimeMillis();
            AdLoader adLoader = new AdLoader.Builder(context, AdmobAds.key_admob_native)
                    .forNativeAd(new OnNativeAdLoadedListener() {

                        @Override
                        public void onNativeAdLoaded(@NonNull NativeAd ad) {
                            if (nativeAds == null) {
                                nativeAds = new ArrayList<>();
                            }
                            ad.setOnPaidEventListener(new OnPaidEventListener() {
                                @Override
                                public void onPaidEvent(@NonNull AdValue adValue) {
                                    App.self().logAd_Impression_Admob_AppsFlyer(adValue, ad.getResponseInfo(), "Native Ads");
                                }
                            });
                            nativeAds.add(ad);
                            timeAddAds = System.currentTimeMillis();
                            adLoaded = true;
                            Log.d(LOG_TAG, "Ad NativeAd was loaded  " + nativeAds.size());
                        }
                    })
                    .withAdListener(new AdListener() {
                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError error) {
                            LogUtil.e("Ad NativeAd Admob error  " + error.getMessage());
                        }

                        @Override
                        public void onAdLoaded() {
                        }
                    })
                    .withNativeAdOptions(new NativeAdOptions.Builder().setMediaAspectRatio(MediaAspectRatio.LANDSCAPE).build())
                    .build();

            AdRequest adRequest = new AdRequest.Builder().build();
            adLoader.loadAd(adRequest);
        }
    }

    public static void showNativeAd(TemplateView naviteView) {
        if (PopupNetworkAds.IS_PRO || !RemoteConfig.remote_ads_native_on) {
            naviteView.setVisibility(View.GONE);
            return;
        }

        if (adLoaded && getTotalNativeAds() > 0) {
            ColorDrawable background = ((ColorDrawable) naviteView.getBackground());
            NativeTemplateStyle styles = new NativeTemplateStyle.Builder().withMainBackgroundColor(background).build();
            template_temp = template;
            template = naviteView;
            template.setStyles(styles);
            template.setNativeAd(nativeAds.get(getIndexShowNative()));
            template.setVisibility(View.VISIBLE);

            App.self().logEventAppsflyer("af_native_displayed");
        } else {
            naviteView.setVisibility(View.GONE);
        }
    }

    private static int getIndexShowNative() {
        ++indexShowNative;
        if (indexShowNative >= nativeAds.size()) {
            indexShowNative = 0;
        }
        return indexShowNative;
    }

    public static void reloadIfAdsLongTime(Activity activity) {
        if (System.currentTimeMillis() - timeAddAds > 1800000) {
            destroyNative();
            loadNativeAd(activity, 4);
        }
    }

    public static void destroyNative() {
        if (nativeAds != null && nativeAds.size() > 0) {
            for (int i = 0; i < nativeAds.size(); i++) {
                try {
                    nativeAds.get(i).destroy();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            nativeAds.clear();
        }
        adLoaded = false;
        nativeAds = null;
        indexShowNative = 0;
        template = null;
        template_temp = null;
        timeAddAds = 0;
    }

    public static void hideNativeAd() {
        if (template != null) {
            try {
                template.setVisibility(View.GONE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (template_temp != null) {
            try {
                template_temp.setVisibility(View.GONE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static int getTotalNativeAds() {
        if (nativeAds == null) {
            return -1;
        } else {
            return nativeAds.size();
        }
    }

}
