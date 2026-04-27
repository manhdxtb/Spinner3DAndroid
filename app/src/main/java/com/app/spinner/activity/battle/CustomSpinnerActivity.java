package com.app.spinner.activity.battle;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.spinner.R;
import com.app.spinner.databinding.ActivityCustomSpinnerBinding;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import app.ads.BaseAdsPopupActivity;

public class CustomSpinnerActivity extends BaseAdsPopupActivity {

    private ActivityCustomSpinnerBinding binding;
    private final List<List<String>> allSpinnerPaths = new ArrayList<>();
    private final List<String> typeDemoPaths = new ArrayList<>();

    private final String[] hexColors = {
            "#f9aa6c", "#7bdad2", "#fe7271", "#fea0b2", "#fabddc",
            "#fce389", "#bde5a9", "#ffd9e8", "#fdeaca", "#bde4f3"
    };

    private int selectedTypeIndex = 0;
    private int selectedColorIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCustomSpinnerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initData();
        setupUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        showNativeAdsActivity();
    }

    private void initData() {
        try {
            for (int i = 1; i <= 6; i++) {
                String folderName = "battle/spiner" + i;
                typeDemoPaths.add("file:///android_asset/" + folderName + "/demo.png");

                List<String> typePaths = new ArrayList<>();
                for (int j = 1; j <= 10; j++) {
                    typePaths.add("file:///android_asset/" + folderName + "/" + j + ".png");
                }
                allSpinnerPaths.add(typePaths);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupUI() {
        binding.btnBack.setOnClickListener(v -> finish());

        // Shapes list (using demo.png)
        ShapeAdapter shapeAdapter = new ShapeAdapter(typeDemoPaths, index -> {
            selectedTypeIndex = index;
            updatePreview();
        });
        binding.rvShapes.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.rvShapes.setAdapter(shapeAdapter);

        // Colors list (using provided hex colors)
        ColorCircleAdapter colorAdapter = new ColorCircleAdapter(hexColors, index -> {
            selectedColorIndex = index;
            updatePreview();
        });
        colorAdapter.setSelectedPos(0); // Select first item by default
        binding.rvColors.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.rvColors.setAdapter(colorAdapter);

        binding.btnRandom.setOnClickListener(v -> {
            Random r = new Random();
            selectedTypeIndex = r.nextInt(allSpinnerPaths.size());
            selectedColorIndex = r.nextInt(hexColors.length);

            shapeAdapter.setSelectedPos(selectedTypeIndex);
            colorAdapter.setSelectedPos(selectedColorIndex);
            updatePreview();
        });

        binding.btnReady.setOnClickListener(v -> {
            String finalPath = allSpinnerPaths.get(selectedTypeIndex).get(selectedColorIndex);
            
            BattleData.shapeYou = finalPath;
            BattleData.colorYou = Color.parseColor(hexColors[selectedColorIndex]);
            
            Intent intent = new Intent(this, SpinTogetherActivity.class);
            startActivity(intent);
        });

        updatePreview();
    }

    private void updatePreview() {
        if (selectedTypeIndex < allSpinnerPaths.size()) {
            String path = allSpinnerPaths.get(selectedTypeIndex).get(selectedColorIndex);
            Glide.with(this).load(path).into(binding.ivPreview);
            binding.ivPreview.clearColorFilter();
        }
    }

    static class ShapeAdapter extends RecyclerView.Adapter<ShapeAdapter.ViewHolder> {
        private final List<String> paths;
        private final OnIndexClickListener listener;
        private int selectedPos = 0;

        ShapeAdapter(List<String> paths, OnIndexClickListener listener) {
            this.paths = paths;
            this.listener = listener;
        }

        public void setSelectedPos(int pos) {
            int oldPos = selectedPos;
            selectedPos = pos;
            notifyItemChanged(oldPos);
            notifyItemChanged(selectedPos);
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_spinner_shape, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Glide.with(holder.itemView.getContext()).load(paths.get(position)).into(holder.ivShape);
            holder.itemView.setSelected(selectedPos == position);
            holder.itemView.setOnClickListener(v -> {
                int pos = holder.getBindingAdapterPosition();
                setSelectedPos(pos);
                listener.onIndexClick(pos);
            });
        }

        @Override
        public int getItemCount() { return paths.size(); }

        static class ViewHolder extends RecyclerView.ViewHolder {
            ImageView ivShape;
            ViewHolder(View v) { super(v); ivShape = v.findViewById(R.id.ivShape); }
        }
    }

    static class ColorCircleAdapter extends RecyclerView.Adapter<ColorCircleAdapter.ViewHolder> {
        private final String[] hexColors;
        private final OnIndexClickListener listener;
        private int selectedPos = 0;

        ColorCircleAdapter(String[] hexColors, OnIndexClickListener listener) {
            this.hexColors = hexColors;
            this.listener = listener;
        }

        public void setSelectedPos(int pos) {
            int oldPos = selectedPos;
            selectedPos = pos;
            notifyItemChanged(oldPos);
            notifyItemChanged(selectedPos);
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_color, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            int color = Color.parseColor(hexColors[position]);
            holder.viewColor.getBackground().setColorFilter(color, PorterDuff.Mode.SRC_IN);
            holder.itemView.setSelected(selectedPos == position);
            holder.itemView.setOnClickListener(v -> {
                int pos = holder.getBindingAdapterPosition();
                setSelectedPos(pos);
                listener.onIndexClick(pos);
            });
        }

        @Override
        public int getItemCount() { return hexColors.length; }

        static class ViewHolder extends RecyclerView.ViewHolder {
            View viewColor;
            ViewHolder(View v) { super(v); viewColor = v.findViewById(R.id.viewColor); }
        }
    }

    interface OnIndexClickListener { void onIndexClick(int index); }
}
