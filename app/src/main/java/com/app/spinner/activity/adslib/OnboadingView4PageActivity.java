package com.app.spinner.activity.adslib;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.TextView;

import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager.widget.ViewPager;

import com.app.spinner.R;
import com.app.spinner.activity.MainActivity;
import com.app.spinner.databinding.ActivityOnboardingBinding;
import com.google.android.ads.nativetemplates.TemplateView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import app.ads.AdmobAds;
import app.ads.App;
import app.ads.BaseAdsPopupActivity;
import app.ads.NativeAdmobAdsPreview;
import app.ads.PopupNetworkAds;
import app.ads.RemoteConfig;
import app.ads.SharedAdsGlobalUtil;

public class OnboadingView4PageActivity extends BaseAdsPopupActivity {

    private static long timeCreate;
    private ActivityOnboardingBinding binding;
    private int total_page = 4;
    private RelativeLayout.LayoutParams layoutParamsFull;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        activity = this;
        if (System.currentTimeMillis() - timeCreate > 5000) {
            timeCreate = System.currentTimeMillis();
        }
        super.onCreate(savedInstanceState);
        setColorIconStatusBarTopBlack(false);

        try {
            getSupportActionBar().hide();
        } catch (Exception e) {
        }
        if (!RemoteConfig.remote_show_onboarding_preview) {
            goNextActivity();
            return;
        }

        binding = ActivityOnboardingBinding.inflate((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE));
        setContentView(binding.getRoot());

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        ViewCompat.setOnApplyWindowInsetsListener(binding.rootLayout, (v, insets) -> {
            androidx.core.graphics.Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        layoutParamsFull = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        initViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        activity = this;
    }

    @Override
    protected void onPause() {
        super.onPause();
        App.self().logScreenSAS("screen_Onboarding", timeResume);

        activity = null;
    }

    private void initViews() {
        OnboadingPageAdapter adapter = new OnboadingPageAdapter();
        adapter.setData(createPageList(total_page));

        binding.viewPager.setAdapter(adapter);
        binding.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        try {
            Field mScroller;
            mScroller = ViewPager.class.getDeclaredField("mScroller");
            mScroller.setAccessible(true);

            FixedSpeedScroller scroller = new FixedSpeedScroller(binding.viewPager.getContext());
            scroller.setScrollDuration(800);

            mScroller.set(binding.viewPager, scroller);
        } catch (Exception e) {
        }
    }

    private void goNextActivity() {
        long timeNow = System.currentTimeMillis();
        PopupNetworkAds.saveTimeOpenAppAds(App.self());
        SharedAdsGlobalUtil.setValue(getApplicationContext(), "TIME_SPLASH_ACTION", "" + timeNow);
        SharedAdsGlobalUtil.setValue(getApplicationContext(), "TIME_HOME_ACTION", "" + timeNow);
        SharedAdsGlobalUtil.setLongValue(getApplicationContext(), "SKIP_INTRO", 2);

        Intent intent = new Intent(OnboadingView4PageActivity.this, MainActivity.class);

        OnboadingView4PageActivity.this.startActivity(intent);
        OnboadingView4PageActivity.this.finishAffinity();
    }

    private List<View> createPageList(int count_page) {
        List<View> pageList = new ArrayList<>();
        for (int i = 1; i <= count_page; i++) {
            pageList.add(createPageView(i));
        }

        return pageList;
    }

    private View createPageView_Small() {
        View view = LayoutInflater.from(App.self()).inflate(R.layout.page_view_preview, null);
        View btnNext = view.findViewById(R.id.preview_btn_next);
        btnNext.setOnClickListener(v -> {
            if (binding.viewPager.getCurrentItem() < (total_page - 1)) {
                binding.viewPager.setCurrentItem(binding.viewPager.getCurrentItem() + 1, true);
            } else {
                if (AdmobAds.isReadyOpenApp() && PopupNetworkAds.checkConditionOpenAppAds(App.self())) {
                    PopupNetworkAds.showOpenAppAds(OnboadingView4PageActivity.this, new PopupNetworkAds.OnShowAdCompleteListener() {
                        @Override
                        public void onCloseAdComplete() {
                            goNextActivity();
                        }
                    });
                } else {
                    goNextActivity();
                }
            }
        });
        return view;
    }

    private View createPageView_Full() {
        View view = LayoutInflater.from(App.self()).inflate(R.layout.page_view_preview_full, null);
        View btnNext = view.findViewById(R.id.preview_btn_next);
        btnNext.setOnClickListener(v -> {
            if (binding.viewPager.getCurrentItem() < (total_page - 1)) {
                binding.viewPager.setCurrentItem(binding.viewPager.getCurrentItem() + 1, true);
            } else {
                if (AdmobAds.isReadyOpenApp() && PopupNetworkAds.checkConditionOpenAppAds(App.self())) {
                    PopupNetworkAds.showOpenAppAds(OnboadingView4PageActivity.this, new PopupNetworkAds.OnShowAdCompleteListener() {
                        @Override
                        public void onCloseAdComplete() {
                            goNextActivity();
                        }
                    });
                } else {
                    goNextActivity();
                }
            }
        });
        return view;
    }

    private View createPageView(int index) {
        activity = OnboadingView4PageActivity.this;
        View view = null;
        try {
            if (index == 1 || index == 2 || index == 4) {
                view = createPageView_Small();
                TemplateView nativeAds_Ngan = view.findViewById(R.id.app_nativeads_ngan);
                if (NativeAdmobAdsPreview.getTotalNativeAds() > 0) {
                    nativeAds_Ngan.setVisibility(View.VISIBLE);
                    NativeAdmobAdsPreview.showNativeAd(nativeAds_Ngan);
                } else {
                    nativeAds_Ngan.setVisibility(View.INVISIBLE);
                }
            }
            if (index == 3) {
                view = createPageView_Full();
                TemplateView nativeAds_Full = view.findViewById(R.id.app_nativeads_full);
                if (NativeAdmobAdsPreview.getTotalNativeAds() > 0) {
                    view.findViewById(R.id.img_preview_view).setVisibility(View.GONE);
                    NativeAdmobAdsPreview.showNativeAd(nativeAds_Full);
                }
            }
        } catch (Exception e) {
        }

        ImageView icDot = view.findViewById(R.id.preview_ic_dot);
        View btnNext = view.findViewById(R.id.preview_btn_next);
        ImageView imageview = view.findViewById(R.id.img_preview_view);

        switch (index) {
            case 1:
                icDot.setImageResource(com.google.android.ads.nativetemplates.R.drawable.ic_dot_preview_1);
                imageview.setImageResource(R.drawable.obd_0);
                ((TextView) btnNext).setText(getResources().getString(R.string.onboarding_continue));
                break;
            case 2:
                icDot.setImageResource(com.google.android.ads.nativetemplates.R.drawable.ic_dot_preview_2);
                imageview.setImageResource(R.drawable.obd_1);
                ((TextView) btnNext).setText(getResources().getString(R.string.onboarding_continue));
                break;
            case 3:
                icDot.setImageResource(com.google.android.ads.nativetemplates.R.drawable.ic_dot_preview_3);
                imageview.setImageResource(R.drawable.obd_2);
                ((TextView) btnNext).setText(getResources().getString(R.string.onboarding_continue));
                break;
            case 4:
                icDot.setImageResource(com.google.android.ads.nativetemplates.R.drawable.ic_dot_preview_4);
                imageview.setImageResource(R.drawable.obd_3);
                ((TextView) btnNext).setText(getResources().getString(R.string.onboarding_get_started));
                break;
        }

        return view;
    }

    public class FixedSpeedScroller extends Scroller {
        private int mDuration = 500;

        public FixedSpeedScroller(Context context) {
            super(context);
        }

        public FixedSpeedScroller(Context context, Interpolator interpolator) {
            super(context, interpolator);
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy, int duration) {
            super.startScroll(startX, startY, dx, dy, mDuration);
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy) {
            super.startScroll(startX, startY, dx, dy, mDuration);
        }

        public void setScrollDuration(int duration) {
            mDuration = duration;
        }
    }

}
