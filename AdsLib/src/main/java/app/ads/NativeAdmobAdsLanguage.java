package app.ads;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.text.TextUtils;
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

public class NativeAdmobAdsLanguage {

    private static boolean adLoaded = false;
    private static Context context;
    private static ArrayList<NativeAd> nativeAds;
    private static int indexShowNative = 0;
    private static TemplateView template, template_temp;

    public static void loadNativeAd() {
        context = App.self();

        if (PopupNetworkAds.IS_PRO || !RemoteConfig.remote_ads_native_on) {
            return;
        }

        requestNativeAds(AdmobAds.key_admob_native_language);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                requestNativeAds(AdmobAds.key_admob_native_language_2);
            }
        }, 3000);
    }

    private static void requestNativeAds(String keyAds) {
        if (TextUtils.isEmpty(keyAds)) {
            keyAds = AdmobAds.key_admob_native;
        }
        LogUtil.i("Language NativeAd Admob " + keyAds);
        AdLoader adLoader = new AdLoader.Builder(App.self().getApplicationContext(), keyAds)
                .forNativeAd(new OnNativeAdLoadedListener() {

                    @Override
                    public void onNativeAdLoaded(@NonNull NativeAd ad) {
                        if (nativeAds == null) {
                            nativeAds = new ArrayList<>();
                        }
                        ad.setOnPaidEventListener(new OnPaidEventListener() {
                            @Override
                            public void onPaidEvent(@NonNull AdValue adValue) {
                                App.self().logAd_Impression_Admob_AppsFlyer(adValue, ad.getResponseInfo(), "Language Native Ads");
                            }
                        });
                        nativeAds.add(ad);
                        adLoaded = true;
                        LogUtil.d("Language NativeAd Admob loaded  " + nativeAds.size());
                    }

                })
                .withAdListener(new AdListener() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError error) {
                        LogUtil.e("Language NativeAd Admob error  " + error.getMessage());
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

    public static void destroyNative() {
        try {
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
        } catch (Exception e) {
        }
        adLoaded = false;
        nativeAds = null;
        indexShowNative = 0;
        template = null;
        template_temp = null;
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
