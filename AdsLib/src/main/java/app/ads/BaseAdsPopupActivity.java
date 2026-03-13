package app.ads;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.Window;

import androidx.annotation.Nullable;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.akexorcist.localizationactivity.ui.LocalizationActivity;
import com.google.android.ads.nativetemplates.R;
import com.google.android.ads.nativetemplates.TemplateView;

import java.util.Locale;

public class BaseAdsPopupActivity extends LocalizationActivity {

    public interface PopupAdsCallback {
        public void onAction();
    }

    public BaseAdsPopupActivity activity;
    private PopupAdsCallback popupAdsCallback;

    public void setPopupAdsCallback(PopupAdsCallback callback) {
        popupAdsCallback = callback;
    }

    private static long timeClickBack = 0;

    private boolean userSetShowPopupAds = true;
    private boolean isCanShowPopupAdsNow = false;
    protected boolean setupColorStatusBar = true;
    protected String screenName = "";
    protected long timeResume;

    public void userSetShowPopupAds(boolean isShow) {
        userSetShowPopupAds = isShow;
    }

    public boolean isCanShowPopupAdsNow() {
        return isCanShowPopupAdsNow;
    }

    public Context getLocalizedContext() {
        Locale locale = new Locale(SharedAdsGlobalUtil.getLanguage(App.self()));
        Locale.setDefault(locale);

        Configuration configuration = new Configuration(getResources().getConfiguration());
        configuration.setLocale(locale);

        return createConfigurationContext(configuration);
    }

    public String getScreenName() {
        if (TextUtils.isEmpty(screenName)) {
            String nameClass = this.getClass().getSimpleName();
            if (nameClass.equals("SplashActivity")) {
                return "screen_Splash";
            }
            if (nameClass.equals("LanguageActivity")) {
                return "screen_Language";
            }
            if (nameClass.equals("SplashPreviewNormalActivity")) {
                return "screen_Onboarding";
            }
            if (nameClass.equals("VipSubsBuyActivity")) {
                return "screen_IAP";
            }
            if (nameClass.equals("VipSubsLimitedActivity")) {
                return "screen_LimitedOffer";
            }
            if (nameClass.equals("HomeStartActivity")) {
                return "screen_Home";
            }
            if (nameClass.equals("HomeActivity")) {
                return "screen_Themes";
            }
            if (nameClass.equals("ThumbThemeActivity")) {
                return "screen_PreviewTheme";
            }
            if (nameClass.equals("CustomThemeBgActivity")) {
                return "screen_ChooseBackground";
            }
            if (nameClass.equals("CustomThemeAnimActivity")) {
                return "screen_CustomEffect";
            }
            if (nameClass.equals("WidgetFullActivity")) {
                return "screen_PreviewWidget";
            }
            if (nameClass.equals("ViewWallpaperImageActivity")) {
                return "screen_PreviewWallpaper";
            }
            if (nameClass.equals("ApplyWallpaperActivity")) {
                return "screen_SetWallpaper";
            }
            if (nameClass.equals("ApplyThemeSuccessActivity") || nameClass.equals("AddSuccessActivity")) {
                return "screen_SetSuccess";
            }
            return "";
        }
        return screenName;
    }

    public void showPopupAdsCreateView(View view) {
        if (getClass().getSimpleName().equals("SplashActivity") ||
                getClass().getSimpleName().equals("LanguageActivity") ||
                getClass().getSimpleName().equals("OnboadingView4PageActivity") ||
                getClass().getSimpleName().contains("MainActivity")) {
            userSetShowPopupAds = false;
            return;
        }

        long timeNow = System.currentTimeMillis();
        if (timeNow - timeClickBack < 500) {
            return;
        }
        isCanShowPopupAdsNow = PopupNetworkAds.canShowPopupAdsNow(this);
        if (isCanShowPopupAdsNow) {
            LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View welcomeAdsView = inflater.inflate(R.layout.ads_popup_welcome, null);
            ((ViewGroup) view.getRootView()).addView(welcomeAdsView);

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        ((ViewGroup) view.getRootView()).removeView(welcomeAdsView);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, 2000);

            PopupNetworkAds.showPopupAds(this, getScreenName(), new PopupNetworkAds.OnShowAdCompleteListener() {
                @Override
                public void onCloseAdComplete() {
                    try {
                        BaseAdsPopupActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    ((ViewGroup) view.getRootView()).removeView(welcomeAdsView);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                try {
                                    if (popupAdsCallback != null) {
                                        popupAdsCallback.onAction();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                popupAdsCallback = null;
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } else {
            try {
                if (popupAdsCallback != null) {
                    popupAdsCallback.onAction();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            popupAdsCallback = null;
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        hideNavigationBar();
    }

    @Override
    public void setContentView(View view) {
        AdmobAds.notShowPopupResumeAdsNow();
        super.setContentView(view);

        if (userSetShowPopupAds && App.self().isQuyenShowPopupAds) {
            try {
                showPopupAdsCreateView((ViewGroup) view);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("Admob Applovin", "Error showPopupAds showPopupAdsCreateView  " + this.toString());
            }
        }
    }

    @Override
    public void setContentView(int layoutResID) {
        AdmobAds.notShowPopupResumeAdsNow();
        super.setContentView(layoutResID);

        if (userSetShowPopupAds && App.self().isQuyenShowPopupAds) {
            try {
                showPopupAdsCreateView((ViewGroup) ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0));
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("Admob Applovin", "Error showPopupAds showPopupAdsCreateView  " + this.toString());
            }
        }
    }

    public static void setTimeClickBack() {
        timeClickBack = System.currentTimeMillis();
    }

    @SuppressLint("GestureBackNavigation")
    @Override
    public void onBackPressed() {
        setTimeClickBack();
        super.onBackPressed();
    }

    public long timeAtOnScreen;

    @Override
    protected void onResume() {
        super.onResume();
        activity = this;
        timeResume = System.currentTimeMillis();
        App.self().activityLast = this;
        timeAtOnScreen = System.currentTimeMillis();

        long timeNow = System.currentTimeMillis();
        long timeActionHome = 0;
        try {
            timeActionHome = Long.parseLong(SharedAdsGlobalUtil.getValue(getApplicationContext(), "TIME_HOME_ACTION"));
        } catch (Exception e) {
        }
        if (Math.abs(timeNow - timeActionHome) > 3000) {
            if (!PopupNetworkAds.IS_PRO && AdmobAds.isReadyResumeAds() &&
                    !getClass().getSimpleName().equals("SplashActivity") && !getClass().getSimpleName().equals("LanguageActivity") && !getClass().getSimpleName().equals("OnboadingView4PageActivity")) {
                LogUtil.w("showPopupResumeAds  OK");
                AdmobAds.showPopupResumeAds(BaseAdsPopupActivity.this, null);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        long timeNow = System.currentTimeMillis();
        SharedAdsGlobalUtil.setValue(getApplicationContext(), "TIME_HOME_ACTION", "" + timeNow);
    }

    @Override
    protected void onStop() {
        super.onStop();

        try {
            PopupNetworkAds.setupPopupAds(this);
        } catch (Exception e) {
        }
        try {
            if (!isFinishing() &&
                    !getClass().getSimpleName().equals("SplashActivity") && !getClass().getSimpleName().equals("LanguageActivity") && !getClass().getSimpleName().equals("OnboadingView4PageActivity")) {
                AdmobAds.setupPopupResumeAds(App.self());
            }
        } catch (Exception e) {
        }
    }

    private void hideNavigationBar() {
        WindowInsetsControllerCompat windowInsetsController =
                WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        if (windowInsetsController != null) {
            // 1. Ẩn thanh điều hướng (Navigation Bar)
            windowInsetsController.hide(WindowInsetsCompat.Type.navigationBars());

            // BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE: Vuốt cạnh màn hình sẽ hiện tạm thời rồi tự ẩn lại (Chế độ Immersive Sticky - Dùng cho Game/Video)
            windowInsetsController.setSystemBarsBehavior(
                    WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            );
        }
    }

    public void setColorIconStatusBarTopBlack(boolean isColorBlack) {
        Window window = this.getWindow();
        View decorView = window.getDecorView();
        WindowInsetsControllerCompat controllerCompat = new WindowInsetsControllerCompat(window, decorView);
        controllerCompat.setAppearanceLightStatusBars(isColorBlack);
        controllerCompat.setAppearanceLightNavigationBars(isColorBlack);
    }

    public void notShowPopupResumeAdsNow() {
        AdmobAds.notShowPopupResumeAdsNow();
    }

    public void notShowPopupResumeAdsNow(int addTimeDelayMs) {
        AdmobAds.notShowPopupResumeAdsNow(addTimeDelayMs);
    }

    private void showLayoutNativeAdsParentNeuCo(TemplateView adsNativeViewChild, boolean isShow) {
        try {
            ViewParent parent = adsNativeViewChild.getParent();
            if (parent instanceof ViewGroup) {
                ViewGroup viewGroupParent = (ViewGroup) parent;
                if (viewGroupParent.getId() == R.id.layout_nativeads) {
                    if (isShow) {
                        viewGroupParent.setVisibility(View.VISIBLE);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showNativeAdsActivity() {
        try {
            TemplateView adsNativeView = findViewById(R.id.app_nativeads);
            if (PopupNetworkAds.IS_PRO || NativeAdmobAds.getTotalNativeAds() == 0 || !RemoteConfig.remote_ads_native_on) {
                adsNativeView.setVisibility(View.GONE);
                showLayoutNativeAdsParentNeuCo(adsNativeView, false);
                return;
            }

            NativeAdmobAds.showNativeAd(adsNativeView);
        } catch (Exception e) {
        }
    }

    public void showNativeAdsOnParent(View viewParent) {
        try {
            TemplateView adsNativeView = viewParent.findViewById(R.id.app_nativeads);
            if (PopupNetworkAds.IS_PRO || NativeAdmobAds.getTotalNativeAds() == 0 || !RemoteConfig.remote_ads_native_on) {
                adsNativeView.setVisibility(View.GONE);
                showLayoutNativeAdsParentNeuCo(adsNativeView, false);
                return;
            }

            NativeAdmobAds.showNativeAd(adsNativeView);
        } catch (Exception e) {
        }
    }

    public void finishAppAll() {
        NativeAdmobAds.destroyNative();
        AdmobAds.clearPopupAdmobAds();
        AdmobAds.clearPopupResumeAds();
        AdmobAds.clearOpenAppAds();
        finishAffinity();
    }

    private ViewGroup getRootView() {
        return (ViewGroup) findViewById(android.R.id.content);
    }

    protected boolean loadedBanner = false;

    public void showBannerCollapActivity() {
        if (loadedBanner) {
            BannerAds.reShowBannerAds(getRootView());
        } else {
            loadedBanner = true;
            BannerAds.loadBannerCollapAds(activity, getRootView(), null);
        }

        Handler mHandler = new Handler(Looper.getMainLooper());
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {

            }
        }, 1000);
    }

}
