package com.app.spinner.activity.battle;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.app.spinner.databinding.ActivityBattleLoadingAdsBinding;
import com.bumptech.glide.Glide;

import java.util.Random;

import app.ads.BaseAdsPopupActivity;
import app.ads.NativeAdmobAds;

public class BattleLoadingAdsActivity extends BaseAdsPopupActivity {

    private ActivityBattleLoadingAdsBinding binding;
    private final Random random = new Random();

    private String shapeYou, shapeP2;
    private int colorYou, colorP2;
    private float rpmYou, rpmP2;

    private float currentVisualRpmUser = 30f;
    private float currentVisualRpmP2 = 30f;
    private float targetVisualRpmUser;
    private float targetVisualRpmP2;

    private float angleUser = 0f;
    private float angleP2 = 0f;

    private long lastFrameTime = 0;
    private final Handler rotationHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Delay 6s then go to Showdown
        setPopupAdsCallback(new PopupAdsCallback() {
            @Override
            public void onAction() {
                rotationHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        BattleLoadingAdsActivity.this.startRunProcess();
                        BattleLoadingAdsActivity.this.startRotationLoop();
                    }
                });
            }
        });

        binding = ActivityBattleLoadingAdsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        NativeAdmobAds.loadNativeAd(this, 2);

        shapeYou = BattleData.shapeYou;
        colorYou = BattleData.colorYou;
        shapeP2 = BattleData.shapeP2;
        colorP2 = BattleData.colorP2;
        rpmYou = BattleData.rpmYou;
        rpmP2 = BattleData.rpmP2;

        // Targets: 400 - 600
        targetVisualRpmUser = 300 + random.nextInt(301);
        targetVisualRpmP2 = 300 + random.nextInt(301);

        setup3DSpinners();
    }

    private void setup3DSpinners() {
        float density = getResources().getDisplayMetrics().density;
        float cameraDistance = 8000 * density;
        float rotationAngle = 50f; // Standard 50-degree tilt
        float marginDp = 62.5f; // Standard offset for 50-degree tilt
        float halfMarginPx = (marginDp / 2f) * density;

        // User Spinner
        Glide.with(this).load(shapeYou).into(binding.spinningImageViewUpUser);
        Glide.with(this).load(shapeYou).into(binding.spinningImageViewDownUser);
        binding.spinningImageViewUpUser.setCameraDistance(cameraDistance);
        binding.spinningImageViewDownUser.setCameraDistance(cameraDistance);
        binding.spinningImageViewUpUser.setRotationX(rotationAngle);
        binding.spinningImageViewDownUser.setRotationX(-rotationAngle);

        RelativeLayout.LayoutParams userUpParams = (RelativeLayout.LayoutParams) binding.spinningImageViewUpUser.getLayoutParams();
        userUpParams.topMargin = (int) (-halfMarginPx);
        binding.spinningImageViewUpUser.setLayoutParams(userUpParams);

        RelativeLayout.LayoutParams userDownParams = (RelativeLayout.LayoutParams) binding.spinningImageViewDownUser.getLayoutParams();
        userDownParams.topMargin = (int) (halfMarginPx);
        binding.spinningImageViewDownUser.setLayoutParams(userDownParams);

        // P2 Spinner
        Glide.with(this).load(shapeP2).into(binding.spinningImageViewUpP2);
        Glide.with(this).load(shapeP2).into(binding.spinningImageViewDownP2);
        binding.spinningImageViewUpP2.setCameraDistance(cameraDistance);
        binding.spinningImageViewDownP2.setCameraDistance(cameraDistance);
        binding.spinningImageViewUpP2.setRotationX(rotationAngle);
        binding.spinningImageViewDownP2.setRotationX(-rotationAngle);

        RelativeLayout.LayoutParams p2UpParams = (RelativeLayout.LayoutParams) binding.spinningImageViewUpP2.getLayoutParams();
        p2UpParams.topMargin = (int) (-halfMarginPx);
        binding.spinningImageViewUpP2.setLayoutParams(p2UpParams);

        RelativeLayout.LayoutParams p2DownParams = (RelativeLayout.LayoutParams) binding.spinningImageViewDownP2.getLayoutParams();
        p2DownParams.topMargin = (int) (halfMarginPx);
        binding.spinningImageViewDownP2.setLayoutParams(p2DownParams);
    }

    private void startRotationLoop() {
        lastFrameTime = System.currentTimeMillis();
        rotationHandler.post(rotationRunnable);
    }

    private void startRunProcess() {
        ValueAnimator animator = ValueAnimator.ofInt(0, 100);
        animator.setDuration(4000); // 4 seconds
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(animation -> {
            int progress = (int) animation.getAnimatedValue();
            binding.progressBar.setProgress(progress);
        });
        animator.addListener(new android.animation.AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(android.animation.Animator animation) {
                rotationHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        BattleLoadingAdsActivity.this.goToShowdown();
                    }
                }, 1000);
            }
        });
        animator.start();
    }

    private final Runnable rotationRunnable = new Runnable() {
        @Override
        public void run() {
            long currentTime = System.currentTimeMillis();
            float deltaTime = (currentTime - lastFrameTime) / 1000f;
            lastFrameTime = currentTime;

            // Ramping speed: increase by (target-200)/duration per second
            // 6 seconds duration
            float rampStepUser = (targetVisualRpmUser - 10f) / 6f * deltaTime;
            float rampStepP2 = (targetVisualRpmP2 - 10f) / 6f * deltaTime;

            currentVisualRpmUser = Math.min(targetVisualRpmUser, currentVisualRpmUser + rampStepUser);
            currentVisualRpmP2 = Math.min(targetVisualRpmP2, currentVisualRpmP2 + rampStepP2);

            // Update Rotations
            angleUser += currentVisualRpmUser * deltaTime * 6;
            binding.spinningImageViewUpUser.setRotation(angleUser);
            binding.spinningImageViewDownUser.setRotation(angleUser);

            angleP2 += currentVisualRpmP2 * deltaTime * 6;
            binding.spinningImageViewUpP2.setRotation(angleP2);
            binding.spinningImageViewDownP2.setRotation(angleP2);

            rotationHandler.postDelayed(this, 16);
        }
    };

    private void goToShowdown() {
        // Stop background rotation loop
        rotationHandler.removeCallbacks(rotationRunnable);

        Intent intent = new Intent(this, SpinShowdownActivity.class);
        startActivity(intent);
        activity.finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        showNativeAdsActivity();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        rotationHandler.removeCallbacks(rotationRunnable);
    }
}
