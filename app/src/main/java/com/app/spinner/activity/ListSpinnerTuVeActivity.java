package com.app.spinner.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
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
import com.app.spinner.databinding.ActivityListSpinnerTuveBinding;
import com.app.spinner.util.OnItemClickListener;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import app.ads.BaseAdsPopupActivity;

public class ListSpinnerTuVeActivity extends BaseAdsPopupActivity {

    private ListSpinnerTuVeActivity activity;
    private ActivityListSpinnerTuveBinding binding;
    private AssetImageAdapter adapter;
    private ArrayList<String> imagePaths;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        binding = ActivityListSpinnerTuveBinding.inflate((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE));
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

        imagePaths = getAllDrawingFiles();

        if (imagePaths.isEmpty()) {
            Intent intent = new Intent(activity, DrawSpinnerActivity.class);
            activity.startActivity(intent);
            finish();
            return;
        }

        imagePaths.add(0, "file:///android_asset/spinner/s_0.png");

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
                    Intent intent = new Intent(activity, ViewImageSpinnerActivity.class);
                    intent.putExtra("IMAGE_PATH", imagePath);
                    activity.startActivity(intent);
                }
            }
        });

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(gridLayoutManager);

    }

    private ArrayList<String> getAllDrawingFiles() {
        File filesDir = getFilesDir();

        File[] files = filesDir.listFiles((dir, name) ->
                name.startsWith("drawing_") && name.endsWith(".png")
        );

        if (files == null || files.length == 0) {
            return new ArrayList<>();
        }

        Arrays.sort(files, (f1, f2) -> Long.compare(f2.lastModified(), f1.lastModified()));

        ArrayList<String> filePaths = new ArrayList<>();
        for (File file : files) {
            filePaths.add(Uri.fromFile(file).toString());
        }

        return filePaths;
    }

    @Override
    protected void onResume() {
        super.onResume();
        showNativeAdsActivity();

        try {
            new Thread() {
                @Override
                public void run() {
                    try {
                        ArrayList<String> temp = getAllDrawingFiles();
                        if ((imagePaths.size() - 1) != temp.size()) {
                            imagePaths.clear();
                            imagePaths.addAll(temp);
                            imagePaths.add(0, "file:///android_asset/spinner/s_0.png");
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        adapter.notifyDataSetChanged();
                                    } catch (Exception e) {
                                    }
                                }
                            });
                        }
                    } catch (Exception e) {
                    }
                }
            }.start();
        } catch (Exception e) {
        }
    }
}
