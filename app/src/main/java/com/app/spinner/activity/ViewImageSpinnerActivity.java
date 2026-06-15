package com.app.spinner.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.app.spinner.activity.battle.BattleData;
import com.app.spinner.activity.battle.SpinTogetherActivity;
import com.app.spinner.databinding.ActivityViewImageSpinnerBinding;
import com.bumptech.glide.Glide;

import app.ads.BaseAdsPopupActivity;
import app.ads.RemoteConfig;

public class ViewImageSpinnerActivity extends BaseAdsPopupActivity {

    private ActivityViewImageSpinnerBinding binding;

    private String imagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        binding = ActivityViewImageSpinnerBinding.inflate((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE));
        setContentView(binding.getRoot());
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        ViewCompat.setOnApplyWindowInsetsListener(binding.rootLayout, (v, insets) -> {
            androidx.core.graphics.Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.finish();
            }
        });
        imagePath = getIntent().getStringExtra("IMAGE_PATH");
        if (!TextUtils.isEmpty(imagePath)) {
            Glide.with(this)
                    .load(imagePath)
                    .into(binding.imgDraw);
            binding.btnSpinNow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(activity, SpinnnerViewActivity.class);
                    intent.putExtra("IMAGE_PATH", imagePath);
                    activity.startActivity(intent);
                    activity.finish();
                }
            });
            binding.btnDrawSpin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(activity, DrawSpinnerActivity.class);
                    activity.startActivity(intent);
                    activity.finish();
                }
            });
            binding.btnBattleNow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(activity, SpinTogetherActivity.class);
                    BattleData.shapeYou = imagePath;
                    BattleData.colorYou = Color.parseColor("#FFFFFF");
                    startActivity(intent);
                }
            });
        } else {
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (RemoteConfig.remote_max_native_ads >= 4) {
            showNativeAdsActivity();
        }
    }
}
