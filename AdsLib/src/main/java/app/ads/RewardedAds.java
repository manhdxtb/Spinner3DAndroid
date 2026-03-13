package app.ads;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdValue;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnPaidEventListener;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd;


public class RewardedAds {

    private static String LOG_TAG = "Admob Video Ads";

    public interface OnShowAdRewardListener {
        void onCloseAdComplete();

        void onNotComplete();
    }

    public static void setupRewardedAds(Activity activity) {
        setupAdmobRewardedAds(activity);
    }

    private static RewardedAd rewardedAd;
    private static RewardedInterstitialAd rewardedInterstitialAd;
    private static OnShowAdRewardListener onAdRewardAdmob;
    private static boolean rewardDoneOK = false, isLoadingVideoAds = false;

    private static void setupAdmobRewardedAds(Activity activity) {
        if (PopupNetworkAds.IS_PRO) {
            return;
        }

        try {
            if (AdmobAds.isInitAdmobDone() && rewardedAd == null && !isLoadingVideoAds) {
                isLoadingVideoAds = true;
                rewardDoneOK = false;
                AdRequest adRequest = new AdRequest.Builder().build();
                RewardedAd.load(activity, AdmobAds.key_admob_video_reward,
                        adRequest, new RewardedAdLoadCallback() {
                            @Override
                            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                                Log.d(LOG_TAG, loadAdError.toString());
                                rewardedAd = null;
                                isLoadingVideoAds = false;
                            }

                            @Override
                            public void onAdLoaded(@NonNull RewardedAd ad) {
                                rewardedAd = ad;
                                isLoadingVideoAds = false;
                                Log.d(LOG_TAG, "Ad RewardedAd was loaded.");
                                ad.setOnPaidEventListener(new OnPaidEventListener() {
                                    @Override
                                    public void onPaidEvent(@NonNull AdValue adValue) {
                                        App.self().logAd_Impression_Admob_AppsFlyer(adValue, ad.getResponseInfo(), "RewardedAd Ads");
                                    }
                                });
                                rewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                                    @Override
                                    public void onAdClicked() {
                                        // Called when a click is recorded for an ad.
                                        Log.d(LOG_TAG, "Ad RewardedAd was clicked.");
                                    }

                                    @Override
                                    public void onAdDismissedFullScreenContent() {
                                        // Called when ad is dismissed.
                                        // Set the ad reference to null so you don't show the ad a second time.
                                        Log.d(LOG_TAG, "Ad RewardedAd dismissed fullscreen content.");
                                        rewardedAd = null;

                                        if (onAdRewardAdmob != null) {
                                            if (rewardDoneOK) {
                                                activity.runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        try {
                                                            onAdRewardAdmob.onCloseAdComplete();
                                                        } catch (Exception e) {
                                                        }
                                                        clearRewardAdmobAds();
                                                    }
                                                });
                                            } else {
                                                onAdRewardAdmob.onNotComplete();
                                            }
                                        }

                                        PopupNetworkAds.setScreen_popup(null);
                                    }

                                    @Override
                                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                                        // Called when ad fails to show.
                                        Log.e(LOG_TAG, "Ad RewardedAd failed to show fullscreen content.");
                                        rewardedAd = null;

                                        try {
                                            App.self().logEventFirebaseSAS("ad_reward_status", "ad_status", "fail_" + PopupNetworkAds.getScreen_popup(),
                                                    "fail_reason", adError.getMessage());
                                        } catch (Exception e) {
                                        }
                                    }

                                    @Override
                                    public void onAdImpression() {
                                        // Called when an impression is recorded for an ad.
                                        Log.d(LOG_TAG, "Ad RewardedAd recorded an impression.");
                                    }

                                    @Override
                                    public void onAdShowedFullScreenContent() {
                                        // Called when ad is shown.
                                        Log.d(LOG_TAG, "Ad RewardedAd showed fullscreen content.");
                                    }
                                });
                            }
                        });
            }
        } catch (Exception e) {
        }
    }

    private static void clearRewardAdmobAds() {
        rewardedAd = null;
        rewardedInterstitialAd = null;
        onAdRewardAdmob = null;
        rewardDoneOK = false;
    }

    public static boolean isReadyReward() {
        if (rewardedAd != null) {
            return true;
        } else {
            return false;
        }
    }

    public static void showVideoAds(Activity activity, OnShowAdRewardListener listener) {
        onAdRewardAdmob = listener;
        if (onAdRewardAdmob != null) {
            if (isReadyReward()) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (rewardedAd != null) {
                                try {
                                    if (activity instanceof BaseAdsPopupActivity) {
                                        ((BaseAdsPopupActivity) activity).notShowPopupResumeAdsNow(60000);
                                    }
                                } catch (Exception e) {
                                }
                                rewardedAd.show(activity, new OnUserEarnedRewardListener() {
                                    @Override
                                    public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                                        // Handle the reward.
                                        rewardDoneOK = true;
                                        rewardedAd = null;
                                        int rewardAmount = rewardItem.getAmount();
                                        String rewardType = rewardItem.getType();
                                        Log.e(LOG_TAG, "Reward  ---  " + rewardAmount + "  ---   " + rewardType);
                                    }
                                });
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            try {
                                onAdRewardAdmob.onCloseAdComplete();
                            } catch (Exception e1) {
                                e1.printStackTrace();
                            }
                            onAdRewardAdmob = null;
                        }
                    }
                });
            } else {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            onAdRewardAdmob.onCloseAdComplete();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        onAdRewardAdmob = null;
                    }
                });
            }
        }
    }

}
