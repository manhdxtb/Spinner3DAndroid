package com.app.spinner.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.app.spinner.databinding.ActivityHomeBinding;

public class HomeActivity extends AppCompatActivity {

    private HomeActivity activity;
    private ActivityHomeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        binding = ActivityHomeBinding.inflate((LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE));
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(binding.rootLayout, (v, insets) -> {
            androidx.core.graphics.Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding.homeBtnSpinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, ListSpinnerDemoActivity.class);
                activity.startActivity(intent);
            }
        });
    }
}
