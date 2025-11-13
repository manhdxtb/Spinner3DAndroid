package com.app.spinner.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.spinner.R;
import com.app.spinner.adapter.AssetImageAdapter;
import com.app.spinner.databinding.ActivityListSpinnerDemoBinding;
import com.app.spinner.util.OnItemClickListener;

import java.util.ArrayList;

public class ListSpinnerDemoActivity extends AppCompatActivity {

    private ListSpinnerDemoActivity activity;
    private ActivityListSpinnerDemoBinding binding;
    private AssetImageAdapter adapter;
    private ArrayList<String> imagePaths;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        binding = ActivityListSpinnerDemoBinding.inflate((LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE));
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(binding.rootLayout, (v, insets) -> {
            androidx.core.graphics.Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        RecyclerView recyclerView = findViewById(R.id.image_recycler_view);

        imagePaths = new ArrayList<>();
        for (int i = 0; i <= 21; i++) {
            imagePaths.add("file:///android_asset/spinner/s_" + i + ".png");
        }

        adapter = new AssetImageAdapter(this, imagePaths);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                String imagePath = imagePaths.get(position);
                Intent intent = new Intent(activity, SpinnnerViewActivity.class);
                intent.putExtra("IMAGE_PATH", imagePath);
                activity.startActivity(intent);
            }
        });

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(gridLayoutManager);
    }

}
