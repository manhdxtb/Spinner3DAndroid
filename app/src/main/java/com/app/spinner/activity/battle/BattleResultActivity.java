package com.app.spinner.activity.battle;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.app.spinner.R;
import com.app.spinner.activity.MainActivity;
import com.app.spinner.databinding.ActivityBattleResultBinding;
import com.bumptech.glide.Glide;

import app.ads.BaseAdsPopupActivity;

public class BattleResultActivity extends BaseAdsPopupActivity {

    private ActivityBattleResultBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBattleResultBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        boolean youWin = getIntent().getBooleanExtra("youWin", false);
        float finalSpeed = getIntent().getFloatExtra("finalSpeed", 0);
        String shape = getIntent().getStringExtra("shape");
        int color = getIntent().getIntExtra("color", Color.WHITE);

        binding.tvResultStatus.setText(youWin ? "YOU WIN!" : "YOU LOSE!");
        binding.tvResultStatus.setTextColor(youWin ? 0xFF00E5FF : Color.RED);
        binding.tvFinalSpeed.setText(String.format("%.1f ", finalSpeed) + getString(R.string.vong_phut));

        if (shape != null) {
            Glide.with(this).load(shape).into(binding.ivFinalSpinner);
            binding.ivFinalSpinner.setColorFilter(color, PorterDuff.Mode.SRC_IN);
        }

        binding.btnBack.setOnClickListener(v -> finish());
        binding.btnHome.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        showNativeAdsActivity();
    }
}
