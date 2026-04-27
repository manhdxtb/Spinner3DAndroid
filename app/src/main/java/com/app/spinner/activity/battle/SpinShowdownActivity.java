package com.app.spinner.activity.battle;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.spinner.R;
import com.app.spinner.databinding.ActivitySpinShowdownBinding;
import com.app.spinner.util.BitmapUtils;
import com.app.spinner.view.BattleView;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import app.ads.BaseAdsPopupActivity;

public class SpinShowdownActivity extends BaseAdsPopupActivity {

    private ActivitySpinShowdownBinding binding;
    private float initialRpmYou, initialRpmP2;
    private String shapeYou, shapeP2;
    private int colorYou, colorP2;

    private static final float RPM_REDUCTION = 10f;

    private final Random random = new Random();
    private final Handler emojiHandler = new Handler(Looper.getMainLooper());

    // Expanded emoji list
    private final List<String> possibleEmojis = Arrays.asList(
            "❤️", "😎", "😂", "🤬", "🖕", "😏", "💩", "😁", "😆", "😱", "😤", "😡", "🔥", "⚡️"
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySpinShowdownBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        shapeYou = BattleData.shapeYou;
        colorYou = BattleData.colorYou;

        shapeP2 = BattleData.shapeP2;
        colorP2 = BattleData.colorP2;

        initialRpmYou = BattleData.rpmYou;
        initialRpmP2 = BattleData.rpmP2;

        initBattle();
        setupEmojiControls();
        startAIReactions();

        binding.btnBack.setOnClickListener(v -> finish());
    }

    @Override
    protected void onResume() {
        super.onResume();
        showNativeAdsActivity();
    }

    private void setupEmojiControls() {
        EmojiAdapter adapter = new EmojiAdapter(possibleEmojis, emoji -> {
            binding.battleView.spawnEmoji(emoji, true);
        });
        binding.rvReactions.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.rvReactions.setAdapter(adapter);
    }

    private void startAIReactions() {
        emojiHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                String randomEmoji = possibleEmojis.get(random.nextInt(possibleEmojis.size()));
                binding.battleView.spawnEmoji(randomEmoji, false);
                emojiHandler.postDelayed(this, 2000 + random.nextInt(3000));
            }
        }, 3000);
    }

    private void initBattle() {
        // Force power bars to full initially
        binding.progressP2.getBackground().setLevel(10000);
        binding.progressYou.getBackground().setLevel(10000);

        // Load Bitmaps without additional tinting
        Bitmap bYou = BitmapUtils.getTintedBitmapFromAsset(this, shapeYou, 0xFFFFFFFF);
        Bitmap bP2 = BitmapUtils.getTintedBitmapFromAsset(this, shapeP2, 0xFFFFFFFF);

        if (bYou != null && bP2 != null) {
            binding.battleView.setRpmReduction(RPM_REDUCTION);
            binding.battleView.init(bYou, bP2, colorYou, colorP2, initialRpmYou, initialRpmP2, new BattleView.OnBattleListener() {
                @Override
                public void onCollision(float speedYou, float speedP2) {
                    runOnUiThread(() -> {
                        updateProgress(speedYou, speedP2);
                        showNativeAdsActivity();
                    });
                }

                @Override
                public void onGameOver(boolean youWin) {
                    runOnUiThread(() -> {
                        BattleData.youWin = youWin;
                        BattleData.finalSpeed = youWin ? initialRpmYou : initialRpmP2;
                        
                        Intent intent = new Intent(SpinShowdownActivity.this, BattleResultActivity.class);
                        startActivity(intent);
                        finish();
                    });
                }

                @Override
                public void onUpdateP2Emoji(String emoji) {
                    runOnUiThread(() -> binding.tvP2AvatarEmoji.setText(emoji));
                }
            });
        }
    }

    private void updateProgress(float speedYou, float speedP2) {
        float ratioP2 = Math.max(0, Math.min(1, speedP2 / initialRpmP2));
        float ratioYou = Math.max(0, Math.min(1, speedYou / initialRpmYou));

        binding.progressP2.getBackground().setLevel((int) (ratioP2 * 10000));
        binding.progressYou.getBackground().setLevel((int) (ratioYou * 10000));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        emojiHandler.removeCallbacksAndMessages(null);
    }

    static class EmojiAdapter extends RecyclerView.Adapter<EmojiAdapter.ViewHolder> {
        private final List<String> emojis;
        private final OnEmojiClickListener listener;

        EmojiAdapter(List<String> emojis, OnEmojiClickListener listener) {
            this.emojis = emojis;
            this.listener = listener;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_emoji_button, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            String emoji = emojis.get(position);
            holder.tvEmoji.setText(emoji);
            holder.itemView.setOnClickListener(v -> listener.onEmojiClick(emoji));
        }

        @Override
        public int getItemCount() {
            return emojis.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvEmoji;

            ViewHolder(View v) {
                super(v);
                tvEmoji = v.findViewById(R.id.tvEmoji);
            }
        }
    }

    interface OnEmojiClickListener {
        void onEmojiClick(String emoji);
    }
}
