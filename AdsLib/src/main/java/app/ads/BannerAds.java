package app.ads;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowMetrics;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.ads.nativetemplates.R;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdValue;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnPaidEventListener;

public class BannerAds {

    public static void loadBannerCollapAds(Activity activity, View view, ResultListener listener) {
        if (PopupNetworkAds.IS_PRO) {
            return;
        }

        try {
            if (view.findViewById(R.id.rootAdBanner) == null) {
                return;
            }
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (AdmobAds.isInitAdmobDone()) {
                        try {
                            AdmobAds.setTestDevice();
                            AdView adViewAdmob = new AdView(activity);
                            AdSize adSize = getAdSize(activity);
                            adViewAdmob.setAdSize(adSize);

                            Bundle extras = new Bundle();
                            if (RemoteConfig.remote_show_collap_ad) {
                                extras.putString("collapsible", "bottom");
                            }

                            adViewAdmob.setAdUnitId(AdmobAds.key_admob_banner_collap);
                            AdRequest adRequest = new AdRequest.Builder()
                                    .addNetworkExtrasBundle(AdMobAdapter.class, extras)
                                    .build();
                            adViewAdmob.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                            adViewAdmob.setBackgroundColor(Color.WHITE);

                            adViewAdmob.setAdListener(new AdListener() {

                                @Override
                                public void onAdLoaded() {
                                    Log.d("Admob Collap Banner", "Ad was loaded.");
                                    adViewAdmob.setOnPaidEventListener(new OnPaidEventListener() {
                                        @Override
                                        public void onPaidEvent(@NonNull AdValue adValue) {
                                            App.self().logAd_Impression_Admob_AppsFlyer(adValue, adViewAdmob.getResponseInfo(), "Collap Banner Ads");
                                        }
                                    });
                                    try {
                                        ((View) adViewAdmob.getParent()).setVisibility(View.VISIBLE);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onAdFailedToLoad(LoadAdError adError) {
                                    Log.d("Admob Collap Banner", adError.getMessage());

                                    try {
                                        ((ViewGroup) adViewAdmob.getParent()).removeAllViews();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                    try {
                                        if (listener != null) {
                                            listener.onError();
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onAdClicked() {
                                }

                                @Override
                                public void onAdClosed() {
                                }

                                @Override
                                public void onAdImpression() {
                                }

                                @Override
                                public void onAdOpened() {
                                }
                            });

                            adViewAdmob.loadAd(adRequest);

                            ViewGroup rootView = view.findViewById(R.id.rootAdBanner);
                            if (rootView == null) return;
                            rootView.removeAllViews();
                            rootView.addView(adViewAdmob);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        } catch (Exception e) {
        }
    }

    public static void loadBannerAds(Activity activity, View view) {
        if (PopupNetworkAds.IS_PRO) {
            return;
        }

        try {
            if (view.findViewById(R.id.rootAdBanner) == null) {
                return;
            }
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (!AdmobAds.isInitAdmobDone()) {
                        ViewGroup rootView = activity.findViewById(R.id.rootAdBanner);
                        rootView.getLayoutParams().height = 1;
                        rootView.requestLayout();
                        return;
                    }
                    if (AdmobAds.isInitAdmobDone()) {
                        try {
                            AdmobAds.setTestDevice();
                            AdView adViewAdmob = new AdView(activity);
                            AdSize adSize = getAdSize(activity);
                            adViewAdmob.setAdSize(adSize);

                            adViewAdmob.setAdUnitId(AdmobAds.key_admob_banner);
                            AdRequest adRequest = new AdRequest.Builder().build();
                            adViewAdmob.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                            adViewAdmob.setBackgroundColor(Color.WHITE);

                            adViewAdmob.setAdListener(new AdListener() {

                                @Override
                                public void onAdLoaded() {
                                    Log.d("Admob Banner", "Ad was loaded.");
                                    adViewAdmob.setOnPaidEventListener(new OnPaidEventListener() {
                                        @Override
                                        public void onPaidEvent(@NonNull AdValue adValue) {
                                            App.self().logAd_Impression_Admob_AppsFlyer(adValue, adViewAdmob.getResponseInfo(), "Banner Ads");
                                        }
                                    });
                                    try {
                                        ((View) adViewAdmob.getParent()).setVisibility(View.VISIBLE);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onAdFailedToLoad(LoadAdError adError) {
                                    Log.d("Admob Banner", adError.getMessage());

                                    try {
                                        ((ViewGroup) adViewAdmob.getParent()).removeAllViews();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onAdClicked() {
                                }

                                @Override
                                public void onAdClosed() {
                                }

                                @Override
                                public void onAdImpression() {
                                }

                                @Override
                                public void onAdOpened() {
                                }
                            });

                            adViewAdmob.loadAd(adRequest);

                            ViewGroup rootView = view.findViewById(R.id.rootAdBanner);
                            if (rootView == null) return;
                            rootView.removeAllViews();
                            rootView.addView(adViewAdmob);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        } catch (Exception e) {
        }
    }

    public static void destroy(View view) {
        try {
            ViewGroup rootView = view.findViewById(R.id.rootAdBanner);
            if (rootView == null) {
                return;
            }
            for (int i = 0; i < rootView.getChildCount(); i++) {
                View tmp = rootView.getChildAt(i);
                try {
                    AdView adBanner = (AdView) tmp;
                    if (adBanner != null) {
                        adBanner.destroy();
                        rootView.removeView(adBanner);
                        break;
                    }
                } catch (Exception e) {
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void hidenBannerAds(View view) {
        if (PopupNetworkAds.IS_PRO) {
            return;
        }
        try {
            ViewGroup rootView = view.findViewById(R.id.rootAdBanner);
            for (int i = 0; i < rootView.getChildCount(); i++) {
                View tmp = rootView.getChildAt(i);
                try {
                    AdView adView = (AdView) tmp;
                    if (adView != null) {
                        adView.pause();
                        adView.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void reShowBannerAds(View view) {
        if (PopupNetworkAds.IS_PRO) {
            return;
        }
        try {
            ViewGroup rootView = view.findViewById(R.id.rootAdBanner);
            for (int i = 0; i < rootView.getChildCount(); i++) {
                View tmp = rootView.getChildAt(i);
                try {
                    AdView adView = (AdView) tmp;
                    if (adView != null) {
                        adView.resume();
                        adView.setVisibility(View.VISIBLE);
                    }
                } catch (Exception e) {
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static AdSize getAdSize(Activity activity) {
        DisplayMetrics outMetrics = new DisplayMetrics();
        int adWidthPixels = 0;

        // Android 11 (API 30)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowMetrics windowMetrics = activity.getWindowManager().getCurrentWindowMetrics();
            adWidthPixels = windowMetrics.getBounds().width();
        } else {
            // Old Device
            activity.getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
            adWidthPixels = outMetrics.widthPixels;
        }

        float density = activity.getResources().getDisplayMetrics().density;
        int adWidth = (int) (adWidthPixels / density);

        return AdSize.getPortraitAnchoredAdaptiveBannerAdSize(activity, adWidth);
    }

}

