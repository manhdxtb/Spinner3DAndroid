package com.app.spinner.activity.adslib;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.app.spinner.databinding.ActivityLanguageBinding;

import java.util.ArrayList;

import app.ads.App;
import app.ads.BaseAdsPopupActivity;
import app.ads.LangUtils;
import app.ads.NativeAdmobAds;
import app.ads.NativeAdmobAdsLanguage;
import app.ads.NativeAdmobAdsPreview;
import app.ads.PopupNetworkAds;
import app.ads.RemoteConfig;
import app.ads.SharedAdsGlobalUtil;

public class LanguageActivity extends BaseAdsPopupActivity {

    private static long timeCreate;
    private LanguageActivity activity;
    private ActivityLanguageBinding binding;
    private LanguageAdapter adapterLanguage;
    private boolean isFromSplash;
    private int index_choose = -69;
    private ArrayList<LanguageModel> listLanguage;
    private String languageOld, languageDefault = "";
    private Handler handler;

    @SuppressLint({"MissingSuperCall", "GestureBackNavigation"})
    @Override
    public void onBackPressed() {
        if (isFromSplash) {
            activity = LanguageActivity.this;
            if (index_choose >= 0) {
                SharedAdsGlobalUtil.setLanguage(activity, languageDefault);

                goNextActivity();
            } else {
                Toast.makeText(activity, "Please select language", Toast.LENGTH_LONG).show();
            }
        } else {
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        activity = this;
        isFromSplash = getIntent().getBooleanExtra("SPLASH", false);
        if (System.currentTimeMillis() - timeCreate > 5000 && isFromSplash) {
            timeCreate = System.currentTimeMillis();

            if (RemoteConfig.remote_show_onboarding_preview) {
                NativeAdmobAdsPreview.loadNativeAd(this);
            }
        }
        super.onCreate(savedInstanceState);
        setColorIconStatusBarTopBlack(false);

        binding = ActivityLanguageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        ViewCompat.setOnApplyWindowInsetsListener(binding.rootLayout, (v, insets) -> {
            androidx.core.graphics.Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        ViewCompat.setOnApplyWindowInsetsListener(binding.rootLayout, (v, insets) -> {
            androidx.core.graphics.Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        try {
            getSupportActionBar().hide();
        } catch (Exception e) {
        }

        handler = new Handler();
        activity = this;
        if (isFromSplash) {
            userSetShowPopupAds(false);

            if (!RemoteConfig.remote_show_language_screen) {
                languageDefault = "en";
                SharedAdsGlobalUtil.setLanguage(activity, languageDefault);
                goNextActivity();
                return;
            }
        } else {
            userSetShowPopupAds(true);
        }

        languageDefault = SharedAdsGlobalUtil.getLanguage(this);
        languageOld = languageDefault;

        adapterLanguage = new LanguageAdapter(new LanguageAdapter.LanguageAdapterListener() {
            @Override
            public void onClick(LanguageModel languageModel) {
                // ADS  ////////////////////////////
                if (RemoteConfig.remote_show_language_ads && (System.currentTimeMillis() - timeCreate > 600)) {
                    activity = LanguageActivity.this;
                    if (NativeAdmobAdsLanguage.getTotalNativeAds() > 0 || NativeAdmobAds.getTotalNativeAds() > 0) {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (NativeAdmobAdsLanguage.getTotalNativeAds() > 0) {
                                    try {
                                        NativeAdmobAdsLanguage.hideNativeAd();
                                        NativeAdmobAdsLanguage.showNativeAd(binding.appNativeads);
                                    } catch (Exception e) {
                                    }
                                } else if (NativeAdmobAds.getTotalNativeAds() > 0) {
                                    try {
                                        NativeAdmobAds.hideNativeAd();
                                        NativeAdmobAds.showNativeAd(binding.appNativeads);
                                    } catch (Exception e) {
                                    }
                                }
                                try {
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                if (isFromSplash) {
                                                    binding.btnOk.setVisibility(View.VISIBLE);
                                                }
                                            } catch (Exception e) {
                                            }
                                        }
                                    }, 1000);
                                } catch (Exception e) {
                                }
                            }
                        });
                    } else {
                        binding.btnOk.setVisibility(View.VISIBLE);
                    }
                } else {
                    binding.btnOk.setVisibility(View.VISIBLE);
                }
                /// ///////////////////////////////////


                if (binding.btnOk.getVisibility() != View.VISIBLE) {
                    binding.btnOk.setVisibility(View.VISIBLE);
                }

                languageDefault = languageModel.getLg().toLowerCase();
                SharedAdsGlobalUtil.setLanguage(activity, languageDefault);
                setLanguageApp(languageDefault);
                setLanguageWithoutNotification(languageModel.getLg());
            }
        }, getCurrentLanguage().getLanguage());

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapterLanguage);

        if (isFromSplash) {
            binding.btnBack.setVisibility(View.GONE);
            binding.btnOk.setVisibility(View.GONE);
            binding.btnOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    activity = LanguageActivity.this;
                    SharedAdsGlobalUtil.setLanguage(activity, languageDefault);
                    setLanguageWithoutNotification(languageDefault);
                    LangUtils.updateLanguage(App.self(), languageDefault);

                    goNextActivity();
                }
            });
        } else {
            binding.btnBack.setVisibility(View.VISIBLE);
            binding.btnOk.setVisibility(View.VISIBLE);
            binding.btnOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PopupNetworkAds.notOpenAppAdsNow(activity);

                    activity = LanguageActivity.this;
                    SharedAdsGlobalUtil.setLanguage(activity, languageDefault);
                    setLanguageWithoutNotification(languageDefault);
                    LangUtils.updateLanguage(App.self(), languageDefault);

                    PopupNetworkAds.notOpenAppAdsNow(activity);
                    Intent intent = new Intent(activity, SplashActivity.class);
                    activity.finishAffinity();
                    activity.startActivity(intent);
                }
            });
        }

        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.onBackPressed();
            }
        });

        if (NativeAdmobAds.getTotalNativeAds() < 1 && NativeAdmobAds.getTotalNativeAds() < 1) {
            binding.appNativeads.setVisibility(View.GONE);
            binding.progressBar.setVisibility(View.GONE);
        }

        if (RemoteConfig.remote_show_language_ads) {
            new Thread() {
                @Override
                public void run() {
                    try {
                        while (true) {
                            if (activity != null && !activity.isDestroyed()) {
                                if (NativeAdmobAdsLanguage.getTotalNativeAds() > 0) {
                                    activity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                binding.progressBar.setVisibility(View.GONE);
                                                NativeAdmobAdsLanguage.showNativeAd(binding.appNativeads);
                                            } catch (Exception e) {
                                            }
                                        }
                                    });
                                    break;
                                } else if (NativeAdmobAds.getTotalNativeAds() > 0) {
                                    activity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                binding.progressBar.setVisibility(View.GONE);
                                                NativeAdmobAds.showNativeAd(binding.appNativeads);
                                            } catch (Exception e) {
                                            }
                                        }
                                    });
                                    break;
                                }
                                if (PopupNetworkAds.IS_PRO) {
                                    activity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                binding.progressBar.setVisibility(View.GONE);
                                                binding.appNativeads.setVisibility(View.GONE);
                                            } catch (Exception e) {
                                            }
                                        }
                                    });
                                    break;
                                }
                                Thread.sleep(300);
                            } else {
                                break;
                            }
                        }
                    } catch (Exception e) {
                    }
                }
            }.start();
        } else {
            binding.appNativeads.setVisibility(View.GONE);
            binding.progressBar.setVisibility(View.GONE);
        }
    }

    private void goNextActivity() {
        Intent intent = new Intent(LanguageActivity.this, OnboadingView4PageActivity.class);
        intent.putExtra("SPLASH", true);

        LanguageActivity.this.startActivity(intent);
        LanguageActivity.this.finishAffinity();
    }

    private void setLanguageApp(String language) {
        languageDefault = language;
    }

    @Override
    protected void onResume() {
        super.onResume();
        activity = this;
    }

    @Override
    protected void onPause() {
        super.onPause();
        App.self().logScreenSAS("screen_Language", timeResume);
    }
}