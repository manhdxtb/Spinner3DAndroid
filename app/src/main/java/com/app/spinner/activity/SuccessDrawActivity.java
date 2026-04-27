package com.app.spinner.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import app.ads.BaseAdsPopupActivity;

import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.app.spinner.R;
import com.app.spinner.activity.battle.SpinTogetherActivity;
import com.app.spinner.databinding.ActivitySuccessDrawBinding;
import com.bumptech.glide.Glide;

public class SuccessDrawActivity extends BaseAdsPopupActivity {

    private SuccessDrawActivity activity;
    private ActivitySuccessDrawBinding binding;

    private String imagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        binding = ActivitySuccessDrawBinding.inflate((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE));
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
        } else {
            finish();
        }

        binding.btnBattleNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, SpinTogetherActivity.class);
                intent.putExtra("shape", imagePath);
                intent.putExtra("color", Color.parseColor("#FFFFFF"));
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        showNativeAdsActivity();
    }
}
