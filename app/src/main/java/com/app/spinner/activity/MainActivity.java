package com.app.spinner.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;

import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.app.spinner.App;
import com.app.spinner.activity.battle.CustomSpinnerActivity;
import com.app.spinner.databinding.ActivityHomeBinding;

import app.ads.AdmobAds;
import app.ads.BaseAdsPopupActivity;
import app.ads.NativeAdmobAds;
import app.ads.PopupNetworkAds;
import app.ads.RemoteConfig;

public class MainActivity extends BaseAdsPopupActivity {

    private MainActivity activity;
    private ActivityHomeBinding binding;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        binding = ActivityHomeBinding.inflate((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE));
        setContentView(binding.getRoot());
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        ViewCompat.setOnApplyWindowInsetsListener(binding.rootLayout, (v, insets) -> {
            androidx.core.graphics.Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if (RemoteConfig.remote_ads_interval == 0 && RemoteConfig.remote_ads_offset_openapp == 0) {
            RemoteConfig.loadFromPreferences();
            NativeAdmobAds.destroyNative();
            AdmobAds.setupKeyAds();
            AdmobAds.initAdmob(App.self());
        }
        PopupNetworkAds.setupPopupAds(this);
        NativeAdmobAds.loadNativeAd(this, 4);

        handler = new Handler(Looper.getMainLooper());
        new Thread() {
            @Override
            public void run() {
                try {
                    for (int i = 0; i < 60; i++) {
                        Thread.sleep(300);
                        if (NativeAdmobAds.getTotalNativeAds() > 0) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        activity.showNativeAdsActivity();
                                    } catch (Exception e) {
                                    }
                                }
                            });
                            return;
                        }
                    }
                } catch (Exception e) {
                }
            }
        }.start();

        binding.homeBtnSpinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupNetworkAds.showPopupAds(activity, "", new PopupNetworkAds.OnShowAdCompleteListener() {
                    @Override
                    public void onCloseAdComplete() {
                        Intent intent = new Intent(activity, ListSpinnerTuVeActivity.class);
                        activity.startActivity(intent);
                    }
                });
            }
        });

        binding.homeBtnCollection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, ListSpinnerDemoActivity.class);
                activity.startActivity(intent);
            }
        });

        binding.homeBtnBattle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, CustomSpinnerActivity.class);
                activity.startActivity(intent);
            }
        });
    }
}
