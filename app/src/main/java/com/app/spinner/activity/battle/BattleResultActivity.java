package com.app.spinner.activity.battle;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.RelativeLayout;

import com.app.spinner.R;
import com.app.spinner.databinding.ActivityBattleResultBinding;
import com.bumptech.glide.Glide;

import app.ads.BaseAdsPopupActivity;

public class BattleResultActivity extends BaseAdsPopupActivity {

    private ActivityBattleResultBinding binding;
    private final Handler rotationHandler = new Handler(Looper.getMainLooper());
    private long lastFrameTime = 0;
    private float currentVisualRpm = 20f;
    private float angle = 0f;
    private boolean increasing = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBattleResultBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        boolean youWin = BattleData.youWin;
        float finalSpeed = BattleData.finalSpeed;
        String shape = BattleData.shapeYou;

        binding.tvResultStatus.setText(youWin ? getString(R.string.you_win) : getString(R.string.you_lose));
        binding.tvResultStatus.setTextColor(youWin ? 0xFF00E5FF : Color.RED);
        binding.tvFinalSpeed.setText(String.format("%.1f ", finalSpeed) + getString(R.string.vong_phut));

        if (shape != null) {
            setup3DSpinner(shape);
        }

        binding.btnBack.setOnClickListener(v -> finish());
        binding.btnReset.setOnClickListener(v -> {
            // Cleanup rotation
            rotationHandler.removeCallbacks(rotationRunnable);

            Intent intent = new Intent(this, SpinTogetherActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);

            activity.finish();
        });

        startRotationLoop();
    }

    private void setup3DSpinner(String shape) {
        Glide.with(this).load(shape).into(binding.spinningImageViewUpFinal);
        Glide.with(this).load(shape).into(binding.spinningImageViewDownFinal);

        float density = getResources().getDisplayMetrics().density;
        float cameraDistance = 8000 * density;
        binding.spinningImageViewUpFinal.setCameraDistance(cameraDistance);
        binding.spinningImageViewDownFinal.setCameraDistance(cameraDistance);

        float rotationAngle = 50f;
        binding.spinningImageViewUpFinal.setRotationX(rotationAngle);
        binding.spinningImageViewDownFinal.setRotationX(-rotationAngle);

        float marginDp = 62.5f;
        float halfMarginPx = (marginDp / 2f) * density;

        RelativeLayout.LayoutParams upParams = (RelativeLayout.LayoutParams) binding.spinningImageViewUpFinal.getLayoutParams();
        upParams.topMargin = (int) (-halfMarginPx);
        binding.spinningImageViewUpFinal.setLayoutParams(upParams);

        RelativeLayout.LayoutParams downParams = (RelativeLayout.LayoutParams) binding.spinningImageViewDownFinal.getLayoutParams();
        downParams.topMargin = (int) (halfMarginPx);
        binding.spinningImageViewDownFinal.setLayoutParams(downParams);
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

            // Oscillate RPM between 200 and 500
            float speedStep = 20f * deltaTime; // Adjust speed of oscillation
            if (increasing) {
                currentVisualRpm += speedStep;
                if (currentVisualRpm >= 500f) {
                    currentVisualRpm = 500f;
                    increasing = false;
                }
            } else {
                currentVisualRpm -= speedStep;
                if (currentVisualRpm <= 50f) {
                    currentVisualRpm = 50f;
                    increasing = true;
                }
            }

            angle += currentVisualRpm * deltaTime * 6;
            binding.spinningImageViewUpFinal.setRotation(angle);
            binding.spinningImageViewDownFinal.setRotation(angle);

            rotationHandler.postDelayed(this, 20);
        }
    };

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
