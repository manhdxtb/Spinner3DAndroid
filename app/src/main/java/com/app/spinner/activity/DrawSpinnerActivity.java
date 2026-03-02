package com.app.spinner.activity;

import android.animation.ValueAnimator;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.app.spinner.R;
import com.app.spinner.databinding.ActivityDrawSpinnerBinding;
import com.app.spinner.util.OnItemClickListener;
import com.app.spinner.util.PopupCustom;
import com.app.spinner.view.DrawingView;
import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Random;

import android.content.Context;
import android.view.LayoutInflater;

import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class DrawSpinnerActivity extends AppCompatActivity {

    private DrawSpinnerActivity activity;
    private ActivityDrawSpinnerBinding binding;
    private Handler handler;

    private DrawingView drawingView; // View để vẽ
    private RelativeLayout spinningLayout; // Layout chứa hai ImageView cho hình trụ
    private ImageView spinningImageViewUp; // Mặt trên của hình trụ
    private ImageView spinningImageViewDown; // Mặt dưới của hình trụ
    private Button clearButton, undoButton, saveButton, spinButton, randomButton; // Các nút điều khiển
    private Uri lastSavedImageUri = null, previewCacheImageUri = null; // URI của ảnh đã lưu

    private int selectColor = 0, selectBrush = 0, selectPattern = 3;      // Mặc đinh = 0 0 3

    // --- Biến cho hiệu ứng vật lý (quay) ---
    private ValueAnimator spinnerAnimator; // Animator cho hiệu ứng quay
    private float currentRPM = 0f; // Tốc độ quay hiện tại (vòng/phút)
    private final float flickStrength = 20f; // Lực vẩy để tăng tốc độ quay
    private final float friction = 5f; // Lực ma sát giảm tốc độ quay
    private long lastFrameTime = 0; // Thời gian của frame trước

    // --- Biến cho điều khiển góc nghiêng bằng touch ---
    private float pitch = 50f; // Góc nghiêng ban đầu (50 độ)
    private final float minPitch = 30f; // Góc nghiêng tối thiểu (30 độ)
    private final float maxPitch = 90f; // Góc nghiêng tối đa (90 độ, nhìn từ trên xuống)
    private final float sensitivity = 0.5f; // Độ nhạy của chuyển động touch
    private boolean isTouching = false; // Trạng thái chạm
    private float lastTouchY = 0f; // Vị trí Y của touch trước đó
    private final float maxMarginTop = 75f; // MarginTop tối đa tại pitch = 30 độ (dp)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        binding = ActivityDrawSpinnerBinding.inflate((LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE));
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(binding.rootLayout, (v, insets) -> {
            androidx.core.graphics.Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        handler = new Handler();
        setupView();
    }

    private void setupView() {
        // Khởi tạo các view
        drawingView = findViewById(R.id.drawing_view);
        drawingView.setBackgroundImage(android.R.color.black);

        drawingView.setStrokeType(selectBrush);
        applyPattern();

        binding.btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawingView.clear();
            }
        });
        binding.btnUndo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawingView.undo();
            }
        });
        binding.btnColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                saveDrawing();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (lastSavedImageUri != null) {
                            Intent intent = new Intent(activity, SuccessDrawActivity.class);
                            intent.putExtra("IMAGE_PATH", lastSavedImageUri.toString());
                            activity.startActivity(intent);
                        }
                    }
                }, 500);
            }
        });
        binding.btnPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                savePreviewDrawing();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (previewCacheImageUri != null) {
                            Intent intent = new Intent(activity, SpinnnerViewActivity.class);
                            intent.putExtra("IMAGE_PATH", previewCacheImageUri.toString());
                            activity.startActivity(intent);
                        }
                    }
                }, 500);
            }
        });

        spinningLayout = findViewById(R.id.spinning_layout);
        spinningImageViewUp = findViewById(R.id.spinning_image_view_up);
        spinningImageViewDown = findViewById(R.id.spinning_image_view_down);
        clearButton = findViewById(R.id.button_clear);
        undoButton = findViewById(R.id.button_undo);
        saveButton = findViewById(R.id.button_save);
        spinButton = findViewById(R.id.button_spin);
        randomButton = findViewById(R.id.button_random);

        // Gán sự kiện click cho các nút
        clearButton.setOnClickListener(v -> drawingView.clear());
        undoButton.setOnClickListener(v -> drawingView.undo());
        saveButton.setOnClickListener(v -> saveDrawing());
        randomButton.setOnClickListener(v -> randomSpinner());

        spinButton.setOnClickListener(v -> {
            if (spinnerAnimator == null || !spinnerAnimator.isRunning()) {
                startSpinAnimation();
            } else {
                flickSpinner();
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

    private void startSpinAnimation() {
        // Kiểm tra xem có ảnh để quay không
        if (lastSavedImageUri == null) {
            Toast.makeText(this, "Vui lòng lưu một ảnh trước!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Nếu animation đang chạy, dừng và reset
        if (spinnerAnimator != null && spinnerAnimator.isRunning()) {
            spinnerAnimator.cancel();
            spinningImageViewUp.setRotationX(0f);
            spinningImageViewDown.setRotationX(0f);
            spinningImageViewUp.setRotation(0f);
            spinningImageViewDown.setRotation(0f);
            // Reset marginTop
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) spinningImageViewDown.getLayoutParams();
            params.topMargin = 0;
            spinningImageViewDown.setLayoutParams(params);
            spinningLayout.setVisibility(View.GONE);
            drawingView.setVisibility(View.VISIBLE);
            spinningLayout.setOnTouchListener(null);
            return;
        }

        // Hiển thị spinningLayout và ẩn drawingView
        drawingView.setVisibility(View.GONE);
        spinningLayout.setVisibility(View.VISIBLE);
        spinningImageViewUp.setImageURI(lastSavedImageUri);
        spinningImageViewDown.setImageURI(lastSavedImageUri);

        // Thiết lập hiệu ứng 3D
        float distance = 8000 * getResources().getDisplayMetrics().density;
        spinningImageViewUp.setCameraDistance(distance);
        spinningImageViewDown.setCameraDistance(distance);

        // Thiết lập góc nghiêng và marginTop ban đầu
        float rotationAngle = maxPitch - pitch; // 90 - pitch để pitch = 90 cho rotationX = 0
        spinningImageViewUp.setRotationX(rotationAngle);
        spinningImageViewDown.setRotationX(-rotationAngle);
        // Tính marginTop tại pitch = 50 độ để đảm bảo khoảng cách 50dp
        float marginTop = maxMarginTop * (maxPitch - pitch) / (maxPitch - minPitch);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) spinningImageViewDown.getLayoutParams();
        params.topMargin = (int) (marginTop * getResources().getDisplayMetrics().density); // Chuyển dp sang px
        spinningImageViewDown.setLayoutParams(params);

        // Thiết lập touch listener để điều chỉnh góc nghiêng và marginTop
        spinningLayout.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    isTouching = true;
                    lastTouchY = event.getY();
                    return true;
                case MotionEvent.ACTION_MOVE:
                    if (isTouching) {
                        float deltaY = event.getY() - lastTouchY;
                        // Cập nhật pitch, giới hạn từ 30 đến 90 độ
                        pitch -= deltaY * sensitivity;
                        pitch = Math.max(minPitch, Math.min(maxPitch, pitch));
                        // Cập nhật góc nghiêng: rotationX = 90 - pitch
                        float newRotationAngle = maxPitch - pitch;
                        spinningImageViewUp.setRotationX(newRotationAngle);
                        spinningImageViewDown.setRotationX(-newRotationAngle);
                        // Cập nhật marginTop để điều chỉnh khoảng cách
                        float margin = maxMarginTop * (maxPitch - pitch) / (maxPitch - minPitch);
                        RelativeLayout.LayoutParams newParams = (RelativeLayout.LayoutParams) spinningImageViewDown.getLayoutParams();
                        newParams.topMargin = (int) (margin * getResources().getDisplayMetrics().density); // Chuyển dp sang px
                        spinningImageViewDown.setLayoutParams(newParams);
                        lastTouchY = event.getY();
                    }
                    return true;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    isTouching = false;
                    return true;
            }
            return false;
        });

        // Khởi tạo và chạy vòng lặp quay
        spinnerAnimator = ValueAnimator.ofFloat(0f, 1f);
        spinnerAnimator.setRepeatCount(ValueAnimator.INFINITE);
        spinnerAnimator.setDuration(1000);
        spinnerAnimator.setInterpolator(new LinearInterpolator());

        spinnerAnimator.addUpdateListener(animation -> {
            if (lastFrameTime == 0) {
                lastFrameTime = System.nanoTime();
                return;
            }

            long currentTime = System.nanoTime();
            float deltaTime = (currentTime - lastFrameTime) / 1_000_000_000.0f;
            lastFrameTime = currentTime;

            // Áp dụng ma sát
            currentRPM = Math.max(0, currentRPM - friction * deltaTime);

            // Tính và áp dụng góc quay
            if (currentRPM > 0) {
                float degreesPerSecond = currentRPM * 6;
                float degreesToRotate = degreesPerSecond * deltaTime;
                spinningImageViewUp.setRotation(spinningImageViewUp.getRotation() + degreesToRotate);
                spinningImageViewDown.setRotation(spinningImageViewDown.getRotation() + degreesToRotate);
            }
        });

        spinnerAnimator.start();
        flickSpinner();
    }

    public void flickSpinner() {
        // Tăng tốc độ quay khi vẩy
        currentRPM += flickStrength;
    }

    private void randomSpinner() {
        // Tải ảnh ngẫu nhiên từ assets
        int min = 1;
        int max = 21;
        int randomIndex = new Random().nextInt((max - min) + 1) + min;
        String imagePathInAssets = "spinner/s_" + randomIndex + ".png";
        String assetUri = "file:///android_asset/" + imagePathInAssets;

        Glide.with(this)
                .load(assetUri)
                .into(spinningImageViewUp);
        Glide.with(this)
                .load(assetUri)
                .into(spinningImageViewDown);

        drawingView.setVisibility(View.GONE);
        spinningLayout.setVisibility(View.VISIBLE);

        Toast.makeText(this, "Đã tải ảnh: " + imagePathInAssets, Toast.LENGTH_SHORT).show();
    }
}
