package com.app.spinner.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.spinner.R;
import com.app.spinner.adapter.AssetImageAdapter;
import com.app.spinner.databinding.ActivityListSpinnerDemoBinding;
import com.app.spinner.util.OnItemClickListener;

import java.util.ArrayList;

import app.ads.BaseAdsPopupActivity;

public class ListSpinnerDemoActivity extends BaseAdsPopupActivity {

    private ListSpinnerDemoActivity activity;
    private ActivityListSpinnerDemoBinding binding;
    private AssetImageAdapter adapter;
    private ArrayList<String> imagePaths;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        binding = ActivityListSpinnerDemoBinding.inflate((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE));
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
        RecyclerView recyclerView = findViewById(R.id.image_recycler_view);

        imagePaths = new ArrayList<>();
        for (int i = 0; i <= 33; i++) {
            imagePaths.add("file:///android_asset/spinner/s_" + i + ".png");
        }

        adapter = new AssetImageAdapter(this, imagePaths);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                if (position == 0) {
                    Intent intent = new Intent(activity, DrawSpinnerActivity.class);
                    activity.startActivity(intent);
                } else {
                    String imagePath = imagePaths.get(position);
                    Intent intent = new Intent(activity, SpinnnerViewActivity.class);
                    intent.putExtra("IMAGE_PATH", imagePath);
                    activity.startActivity(intent);
                }
            }
        });

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(gridLayoutManager);
    }

    @Override
    protected void onResume() {
        super.onResume();
        showBannerCollapActivity();
    }
}
