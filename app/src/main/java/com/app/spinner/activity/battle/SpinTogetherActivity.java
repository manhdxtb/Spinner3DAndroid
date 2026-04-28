package com.app.spinner.activity.battle;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.app.spinner.R;
import com.app.spinner.databinding.ActivitySpinTogetherBinding;
import com.bumptech.glide.Glide;

import java.util.Random;

import app.ads.BaseAdsPopupActivity;
import app.ads.NativeAdmobAds;

public class SpinTogetherActivity extends BaseAdsPopupActivity {

    private ActivitySpinTogetherBinding binding;
    private float currentSpeedYou = 0;
    private float currentSpeedP2 = 0;
    private float angleYou = 0;
    private float angleP2 = 0;

    private final Handler rotationHandler = new Handler(Looper.getMainLooper());
    private long lastFrameTime = 0;

    private float lastTouchX;
    private boolean isSpinning = false;
    private final float friction = 0.99f;
    private final float swipePowerMultiplier = 1.0f;

    private String shapeYou;
    private int colorYou;

    private String shapeP2;
    private int colorP2;

    private final Random random = new Random();
    private float targetRpmP2;

    private boolean isTimerStarted = false;
    private boolean isCountdownEffectRunning = false;
    private ObjectAnimator handAnimator;

    private final String[] hexColors = {
            "#f9aa6c", "#7bdad2", "#fe7271", "#fea0b2", "#fabddc",
            "#fce389", "#bde5a9", "#ffd9e8", "#fdeaca", "#bde4f3"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySpinTogetherBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        NativeAdmobAds.loadNativeAd(this, 2);

        shapeYou = BattleData.shapeYou;
        colorYou = BattleData.colorYou;

        BattleData.randomizeNameP2();
        binding.tvNameP2.setText(BattleData.nameP2);

        targetRpmP2 = 700 + random.nextInt(601);

        setupSpinners();
        startRotationLoop();
        setupInput();
        startHandAnimation();

        binding.btnBack.setOnClickListener(v -> finish());
    }

    @Override
    protected void onResume() {
        super.onResume();
        showNativeAdsActivity();
    }

    private void setupSpinners() {
        if (shapeYou != null) {
            Glide.with(this).load(shapeYou).into(binding.spinningImageViewUpYou);
            Glide.with(this).load(shapeYou).into(binding.spinningImageViewDownYou);
        }

        int typeIdx = 1 + random.nextInt(6);
        int colorIdx = 1 + random.nextInt(10);
        shapeP2 = "file:///android_asset/battle/spiner" + typeIdx + "/" + colorIdx + ".png";
        colorP2 = android.graphics.Color.parseColor(hexColors[colorIdx - 1]);

        Glide.with(this).load(shapeP2).into(binding.ivSpinnerP2);
        binding.ivSpinnerP2.clearColorFilter();

        float density = getResources().getDisplayMetrics().density;
        float cameraDistance = 8000 * density;
        binding.spinningImageViewUpYou.setCameraDistance(cameraDistance);
        binding.spinningImageViewDownYou.setCameraDistance(cameraDistance);

        float rotationAngle = 50f;
        binding.spinningImageViewUpYou.setRotationX(rotationAngle);
        binding.spinningImageViewDownYou.setRotationX(-rotationAngle);

        float marginDp = 30f;
        float halfMarginPx = (marginDp / 2f) * density;

        android.widget.RelativeLayout.LayoutParams upParams = (android.widget.RelativeLayout.LayoutParams) binding.spinningImageViewUpYou.getLayoutParams();
        upParams.topMargin = (int) (-halfMarginPx);
        binding.spinningImageViewUpYou.setLayoutParams(upParams);

        android.widget.RelativeLayout.LayoutParams downParams = (android.widget.RelativeLayout.LayoutParams) binding.spinningImageViewDownYou.getLayoutParams();
        downParams.topMargin = (int) (halfMarginPx);
        binding.spinningImageViewDownYou.setLayoutParams(downParams);
    }

    private void startHandAnimation() {
        binding.ivTutHand.setTranslationX(-150f);
        handAnimator = ObjectAnimator.ofFloat(binding.ivTutHand, "translationX", -150f, 150f);
        handAnimator.setDuration(1500);
        handAnimator.setRepeatCount(ValueAnimator.INFINITE);
        handAnimator.start();
    }

    private void stopHandAnimation() {
        if (handAnimator != null) {
            handAnimator.cancel();
            binding.ivTutHand.setVisibility(View.GONE);
        }
    }

    private void startCountdown() {
        if (isTimerStarted) return;
        isTimerStarted = true;
        showNativeAdsActivity();
        stopHandAnimation();

        Toast.makeText(activity, activity.getString(R.string.let_spinner_fast), Toast.LENGTH_LONG).show();

        new CountDownTimer(5000, 100) {
            public void onTick(long millisUntilFinished) {
                binding.tvTimer.setText((millisUntilFinished / 1000) + "s");
                float progress = 1.0f - (millisUntilFinished / 5000f);
                currentSpeedP2 = progress * targetRpmP2;
                if (currentSpeedP2 < 600) {
                    binding.tvSpeedP2.setText(String.valueOf((int) currentSpeedP2));
                } else {
                    binding.tvSpeedP2.setText("??? ");
                }
            }

            public void onFinish() {
                binding.tvTimer.setText("0s");
                isSpinning = false;
                isCountdownEffectRunning = true;
                showLetGoAnimation();
            }
        }.start();
    }

    private void showLetGoAnimation() {
        showNativeAdsActivity();
        binding.viewDim.setVisibility(View.VISIBLE);
        binding.ivCountdownEffect.setVisibility(View.VISIBLE);
        int[] drawables = {R.drawable.ic_let_go_3, R.drawable.ic_let_go_2, R.drawable.ic_let_go_1, R.drawable.ic_let_go_fight};

        Handler handler = new Handler(Looper.getMainLooper());
        for (int i = 0; i < drawables.length; i++) {
            final int index = i;
            handler.postDelayed(() -> {
                binding.ivCountdownEffect.setImageResource(drawables[index]);
                binding.ivCountdownEffect.setScaleX(0.2f);
                binding.ivCountdownEffect.setScaleY(0.2f);

                if (drawables[index] == R.drawable.ic_let_go_fight) {
                    binding.ivCountdownEffect.animate().scaleX(1.0f).scaleY(1.0f).setDuration(600).start();
                } else {
                    binding.ivCountdownEffect.animate().scaleX(0.7f).scaleY(0.7f).setDuration(600).start();
                }

                if (index == drawables.length - 1) {
                    handler.postDelayed(this::goToLoading, 1200);
                }
            }, i * 1200);
        }
    }

    private void goToLoading() {
        // Stop all rotations and animations before leaving
        rotationHandler.removeCallbacks(rotationRunnable);
        if (handAnimator != null) handAnimator.cancel();

        BattleData.shapeP2 = shapeP2;
        BattleData.colorP2 = colorP2;
        BattleData.rpmYou = currentSpeedYou;
        BattleData.rpmP2 = currentSpeedP2;

        Intent intent = new Intent(this, BattleLoadingAdsActivity.class);
        startActivity(intent);
        activity.finish();
    }

    private void startRotationLoop() {
        lastFrameTime = System.currentTimeMillis();
        rotationHandler.post(rotationRunnable);
    }

    private final Runnable rotationRunnable = new Runnable() {
        @Override
        public void run() {
            long currentTime = System.currentTimeMillis();
            float deltaTime = (currentTime - lastFrameTime) / 1000f;
            lastFrameTime = currentTime;

            angleYou += currentSpeedYou * deltaTime * 6;
            binding.spinningImageViewUpYou.setRotation(angleYou);
            binding.spinningImageViewDownYou.setRotation(angleYou);

            if (!isSpinning && !isCountdownEffectRunning) {
                currentSpeedYou *= friction;
                if (currentSpeedYou < 1) currentSpeedYou = 0;
            }
            binding.tvSpeedYou.setText(String.valueOf((int) currentSpeedYou));

            angleP2 += currentSpeedP2 * deltaTime * 6;
            binding.ivSpinnerP2.setRotation(angleP2);

            rotationHandler.postDelayed(this, 16);
        }
    };

    private void setupInput() {
        binding.cardYou.setOnTouchListener((v, event) -> {
            if (isCountdownEffectRunning) return false;
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    lastTouchX = event.getX();
                    isSpinning = true;
                    return true;
                case MotionEvent.ACTION_MOVE:
                    float dx = event.getX() - lastTouchX;
                    if (dx > 0) {
                        if (!isTimerStarted) startCountdown();
                        currentSpeedYou += dx * swipePowerMultiplier;
                        if (currentSpeedYou > 2000) currentSpeedYou = 2000;
                    }
                    lastTouchX = event.getX();
                    return true;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    isSpinning = false;
                    return true;
            }
            return false;
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        rotationHandler.removeCallbacks(rotationRunnable);
        if (handAnimator != null) handAnimator.cancel();
    }
}
