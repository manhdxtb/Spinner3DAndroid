package com.app.spinner.activity.adslib;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.app.spinner.activity.MainActivity;
import com.app.spinner.databinding.ActivitySplashBinding;

import app.ads.AdmobAds;
import app.ads.App;
import app.ads.BaseAdsPopupActivity;
import app.ads.NativeAdmobAds;
import app.ads.SharedAdsGlobalUtil;
import app.ads.SplashAction;

public class SplashActivity extends BaseAdsPopupActivity {

    private ActivitySplashBinding binding;
    private SplashAction splashAction;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!isTaskRoot()) {
            finish();
            return;
        }

        //      Demo VIP        //////////////////////
//        SharedPreferencesGlobalUtil.setVipSub(true);
        /// //////////////////////////////////////////

        activity = this;

        binding = ActivitySplashBinding.inflate((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE));
        setContentView(binding.getRoot());
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        ViewCompat.setOnApplyWindowInsetsListener(binding.rootLayout, (v, insets) -> {
            androidx.core.graphics.Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        NativeAdmobAds.destroyNative();
        AdmobAds.setupKeyAds();

        ////////////        ADS         /////////////
        splashAction = new SplashAction(this, () -> {
            int skip_intro = (int) SharedAdsGlobalUtil.getLongValue(App.self(), "SKIP_INTRO");
            if (skip_intro == 0) {
                Intent intent = new Intent(splashAction.activity, LanguageActivity.class);
                intent.putExtra("SPLASH", true);
                splashAction.activity.startActivity(intent);
                splashAction.activity.finishAffinity();
            } else {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                splashAction.activity.startActivity(intent);
                splashAction.activity.finishAffinity();
            }
        });
    }

    @SuppressLint({"MissingSuperCall", "GestureBackNavigation"})
    @Override
    public void onBackPressed() {
    }

    @Override
    protected void onPause() {
        super.onPause();
        App.self().logScreenSAS("screen_Splash", timeResume);
    }

}