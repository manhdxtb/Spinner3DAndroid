package app.ads;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
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
import com.google.android.gms.ads.nativead.NativeAdOptions;


public class NativeAdmobOverlayAds {

    private static String LOG_TAG = "Admob Native Overlay";

    private static Context context;
    private static NativeAd nativeAds;
    private static TemplateView template;

    public static void loadNativeAd() {
        context = App.self();

        if (PopupNetworkAds.IS_PRO || !RemoteConfig.remote_ads_native_overlay) {
            destroyNative();
            return;
        }

        if (nativeAds == null) {
            AdLoader adLoader = new AdLoader.Builder(context, AdmobAds.key_admob_native_overlay)
                    .forNativeAd(new NativeAd.OnNativeAdLoadedListener() {

                        @Override
                        public void onNativeAdLoaded(@NonNull NativeAd ad) {
                            ad.setOnPaidEventListener(new OnPaidEventListener() {
                                @Override
                                public void onPaidEvent(@NonNull AdValue adValue) {
                                    App.self().logAd_Impression_Admob_AppsFlyer(adValue, ad.getResponseInfo(), "Native Ads");
                                }
                            });
                            nativeAds = ad;
                            Log.d(LOG_TAG, "Overlay NativeAd was loaded");
                        }
                    })
                    .withAdListener(new AdListener() {
                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError error) {
                            LogUtil.e("Overlay NativeAd Admob error  " + error.getMessage());
                        }

                        @Override
                        public void onAdLoaded() {
                        }
                    })
                    .withNativeAdOptions(new NativeAdOptions.Builder().setMediaAspectRatio(MediaAspectRatio.PORTRAIT).build())
                    .build();

            AdRequest adRequest = new AdRequest.Builder().build();
            adLoader.loadAd(adRequest);
        }
    }

    public static boolean isAdLoaded() {
        if (nativeAds != null) {
            return true;
        }
        return false;
    }

    public static void showNativeAd(TemplateView naviteView) {
        if (PopupNetworkAds.IS_PRO || !RemoteConfig.remote_ads_native_on) {
            naviteView.setVisibility(View.GONE);
            return;
        }

        if (isAdLoaded()) {
            ColorDrawable background = ((ColorDrawable) naviteView.getBackground());
            NativeTemplateStyle styles = new NativeTemplateStyle.Builder().withMainBackgroundColor(background).build();
            template = naviteView;
            template.setStyles(styles);
            template.setNativeAd(nativeAds);
            template.setVisibility(View.VISIBLE);
        } else {
            naviteView.setVisibility(View.GONE);
        }
    }

    public static void destroyNative() {
        if (nativeAds != null) {
            nativeAds.destroy();
        }
        nativeAds = null;
        template = null;
    }

}
