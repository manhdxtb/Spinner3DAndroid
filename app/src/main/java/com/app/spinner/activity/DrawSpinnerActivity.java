package com.app.spinner.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;

import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.app.spinner.R;
import com.app.spinner.databinding.ActivityDrawSpinnerBinding;
import com.app.spinner.util.LoadingDialog;
import com.app.spinner.util.OnItemClickListener;
import com.app.spinner.util.PopupCustom;
import com.app.spinner.view.DrawingView;

import java.io.File;
import java.io.FileOutputStream;

import app.ads.BaseAdsPopupActivity;

public class DrawSpinnerActivity extends BaseAdsPopupActivity {

    private DrawSpinnerActivity activity;
    private ActivityDrawSpinnerBinding binding;
    private Handler handler;
    private LoadingDialog loadingDialog;

    private DrawingView drawingView; // View để vẽ
    private Uri lastSavedImageUri = null, previewCacheImageUri = null; // URI của ảnh đã lưu

    private int selectColor = 0, selectBrush = 0, selectPattern = 3;      // Mặc đinh = 0 0 3

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        binding = ActivityDrawSpinnerBinding.inflate((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE));
        setContentView(binding.getRoot());
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        ViewCompat.setOnApplyWindowInsetsListener(binding.rootLayout, (v, insets) -> {
            androidx.core.graphics.Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        handler = new Handler();
        setupView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        showNativeAdsActivity();
    }

    private void setupView() {
        loadingDialog = new LoadingDialog();
        // Khởi tạo các view
        drawingView = findViewById(R.id.drawing_view);
        drawingView.setBackgroundImage(android.R.color.black);

        drawingView.setStrokeType(selectBrush);
        applyPattern();

        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.finish();
            }
        });
        binding.btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showNativeAdsActivity();
                drawingView.clear();
            }
        });
        binding.btnUndo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showNativeAdsActivity();
                drawingView.undo();
            }
        });
        binding.btnColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showNativeAdsActivity();
                PopupCustom.showPopupChooseColor(activity, selectColor, new OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        selectColor = position;
                        if (selectColor == 0) {
                            drawingView.setNeonColors(null);
                        }
                        if (selectColor == 1) {
                            drawingView.setNeonColors("#FE9225");
                        }
                        if (selectColor == 2) {
                            drawingView.setNeonColors("#FED940");
                        }
                        if (selectColor == 3) {
                            drawingView.setNeonColors("#78FE11");
                        }
                        if (selectColor == 4) {
                            drawingView.setNeonColors("#35E8FF");
                        }
                        if (selectColor == 5) {
                            drawingView.setNeonColors("#005EFE");
                        }
                        if (selectColor == 6) {
                            drawingView.setNeonColors("#C241FE");
                        }
                        if (selectColor == 7) {
                            drawingView.setNeonColors("#FE37C6");
                        }
                        if (selectColor == 8) {
                            drawingView.setNeonColors("#FE1818");
                        }
                        if (selectColor == 9) {
                            drawingView.setNeonColors("#FFFFFF");
                        }
                    }
                });
            }
        });
        binding.btnBrush.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showNativeAdsActivity();
                PopupCustom.showPopupChooseStrokeType(activity, selectBrush, new OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        selectBrush = position;
                        drawingView.setStrokeType(selectBrush);
                    }
                });
            }
        });
        binding.btnPattern.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showNativeAdsActivity();
                PopupCustom.showPopupChoosePattern(activity, selectPattern, new OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        selectPattern = position;
                        applyPattern();
                    }
                });
            }
        });
        binding.btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!drawingView.isEmptyNetVe()) {
                    loadingDialog.show(activity);
                    saveDrawing();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (lastSavedImageUri != null) {
                                Intent intent = new Intent(activity, SuccessDrawActivity.class);
                                intent.putExtra("IMAGE_PATH", lastSavedImageUri.toString());
                                activity.startActivity(intent);
                                loadingDialog.dismiss();
                            }
                        }
                    }, 500);
                }
            }
        });
        binding.btnPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!drawingView.isEmptyNetVe()) {
                    loadingDialog.show(activity);
                    savePreviewDrawing();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (previewCacheImageUri != null) {
                                Intent intent = new Intent(activity, SpinnnerViewActivity.class);
                                intent.putExtra("IMAGE_PATH", previewCacheImageUri.toString());
                                activity.startActivity(intent);
                                loadingDialog.dismiss();
                            }
                        }
                    }, 500);
                }
            }
        });
    }

    private void applyPattern() {
        if (selectPattern == 0) {
            drawingView.setMirrorMode(1);
            drawingView.setSymmetrySegments(1);
        }
        if (selectPattern == 1) {
            drawingView.setMirrorMode(1);
            drawingView.setSymmetrySegments(2);
        }
        if (selectPattern == 2) {
            drawingView.setMirrorMode(1);
            drawingView.setSymmetrySegments(3);
        }
        if (selectPattern == 3) {
            drawingView.setMirrorMode(2);
            drawingView.setSymmetrySegments(3);
        }
        if (selectPattern == 4) {
            drawingView.setMirrorMode(1);
            drawingView.setSymmetrySegments(4);
        }
        if (selectPattern == 5) {
            drawingView.setMirrorMode(2);
            drawingView.setSymmetrySegments(4);
        }
        if (selectPattern == 6) {
            drawingView.setMirrorMode(1);
            drawingView.setSymmetrySegments(5);
        }
        if (selectPattern == 7) {
            drawingView.setMirrorMode(2);
            drawingView.setSymmetrySegments(5);
        }
        if (selectPattern == 8) {
            drawingView.setMirrorMode(1);
            drawingView.setSymmetrySegments(6);
        }
        if (selectPattern == 9) {
            drawingView.setMirrorMode(2);
            drawingView.setSymmetrySegments(6);
        }
        if (selectPattern == 10) {
            drawingView.setMirrorMode(1);
            drawingView.setSymmetrySegments(7);
        }
        if (selectPattern == 11) {
            drawingView.setMirrorMode(2);
            drawingView.setSymmetrySegments(7);
        }
    }

    private void saveDrawing() {
        // Lưu bản vẽ thành file PNG
        Bitmap bitmap = drawingView.captureDrawing();
        String fileName = "drawing_" + System.currentTimeMillis() + ".png";

        try (FileOutputStream fos = openFileOutput(fileName, Context.MODE_PRIVATE)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 95, fos);

            File file = new File(getFilesDir(), fileName);
            lastSavedImageUri = Uri.fromFile(file);
        } catch (Exception e) {
            e.printStackTrace();
            lastSavedImageUri = null;
        }
    }

    private void savePreviewDrawing() {
        // Lưu bản vẽ thành file PNG
        Bitmap bitmap = drawingView.captureDrawing();
        String fileName = "cache_preview_" + System.currentTimeMillis() + ".png";

        try (FileOutputStream fos = openFileOutput(fileName, Context.MODE_PRIVATE)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 95, fos);

            File file = new File(getFilesDir(), fileName);
            previewCacheImageUri = Uri.fromFile(file);
        } catch (Exception e) {
            e.printStackTrace();
            previewCacheImageUri = null;
        }
    }

}
