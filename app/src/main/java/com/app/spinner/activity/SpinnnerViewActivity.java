package com.app.spinner.activity;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import app.ads.BaseAdsPopupActivity;

import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.app.spinner.R;
import com.app.spinner.databinding.ActivitySpinnerViewBinding;
import com.app.spinner.util.Utils;
import com.bumptech.glide.Glide;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class SpinnnerViewActivity extends BaseAdsPopupActivity {

    private SpinnnerViewActivity activity;
    private ActivitySpinnerViewBinding binding;
    private boolean isOnScreen;
    private Handler handler;
    private long timeOnCreate, timeStartCount;

    private RelativeLayout spinningLayout; // Layout chứa hai ImageView cho hình trụ
    private ImageView spinningImageViewUp; // Mặt trên của hình trụ
    private ImageView spinningImageViewDown; // Mặt dưới của hình trụ
    private View spinningMoveLeftRight; // View để vuốt left/right

    // --- Biến cho hiệu ứng vật lý (quay) ---
    private ValueAnimator spinnerAnimator; // Animator cho hiệu ứng quay
    private float currentRPM = 0f; // Tốc độ quay hiện tại (vòng/phút)
    private final float flickStrength = 10f; // Lực vẩy để tăng tốc độ quay
    private final float smallFlickStrength = 1f; // Lực nhẹ cho di chuyển chậm (nhỏ hơn flickStrength)
    private final float friction = 8f; // Lực ma sát giảm tốc độ quay
    private long lastFrameTime = 0; // Thời gian của frame trước
    private final float brakeMultiplierHighSpeed = 3.5f; // ← Tăng lực phanh khi > 300 RPM và vuốt ngược (bạn có thể chỉnh 2.5f ~ 5.0f)

    // --- Biến cho điều khiển góc nghiêng bằng touch ---
    private float pitch = 40f; // Góc nghiêng ban đầu (50 độ)
    private final float minPitch = 30f; // Góc nghiêng tối thiểu (30 độ)
    private final float maxPitch = 90f; // Góc nghiêng tối đa (90 độ, nhìn từ trên xuống)
    private final float sensitivity = -0.5f; // Độ nhạy của chuyển động touch
    private boolean isTouching = false; // Trạng thái chạm
    private float lastTouchY = 0f; // Vị trí Y của touch trước đó
    private final float maxMarginTop = 75f; // MarginTop tối đa tại pitch = 30 độ (dp)

    private GestureDetector gestureDetector; // Để phát hiện fling (vuốt)
    private float lastTouchX = 0f; // Vị trí X của touch trước đó cho xử lý di chuyển

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        handler = new Handler();
        timeOnCreate = Utils.timeNow();
        setPopupAdsCallback(new PopupAdsCallback() {
            @Override
            public void onAction() {
                try {
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                startSpinAnimation();
                                showHandTut();
                                demoGocNghieng();
                            } catch (Exception e) {
                            }
                        }
                    }, 500);
                } catch (Exception e) {
                }
            }
        });
        super.onCreate(savedInstanceState);
        activity = this;
        binding = ActivitySpinnerViewBinding.inflate((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE));
        setContentView(binding.getRoot());
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        ViewCompat.setOnApplyWindowInsetsListener(binding.rootLayout, (v, insets) -> {
            androidx.core.graphics.Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        spinningLayout = findViewById(R.id.spinning_layout);
        spinningImageViewUp = findViewById(R.id.spinning_image_view_up);
        spinningImageViewDown = findViewById(R.id.spinning_image_view_down);
        spinningMoveLeftRight = findViewById(R.id.spinning_move_left_right);

        // Thiết lập GestureDetector cho vuốt left/right
        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                // Bỏ qua nếu vuốt vertical mạnh hơn horizontal
                if (Math.abs(velocityY) > Math.abs(velocityX)) {
                    return false;
                }
                // Tính strength dựa trên velocityX (scale để phù hợp với flickStrength)
                float strength = Math.abs(velocityX) / 1000f * flickStrength;
                // Vuốt sang phải (velocityX > 0): quay ngược chiều (âm)
                // Vuốt sang trái (velocityX < 0): quay thuận chiều (dương)
                float amount = (velocityX > 0) ? -strength : strength;
                applyFlick(amount);
                return true;
            }
        });

        // Thiết lập OnTouchListener cho spinning_move_left_right
        spinningMoveLeftRight.setOnTouchListener((v, event) -> {
            gestureDetector.onTouchEvent(event); // Giữ nguyên xử lý GestureDetector cho fling

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    lastTouchX = event.getX();
                    return true;
                case MotionEvent.ACTION_MOVE:
                    float deltaX = event.getX() - lastTouchX;
                    // Bỏ qua nếu di chuyển vertical mạnh hơn horizontal để tránh xung đột với điều chỉnh pitch
                    if (Math.abs(event.getY() - lastTouchY) > Math.abs(deltaX)) {
                        return false;
                    }
                    // Tính lượng lực nhẹ dựa trên deltaX
                    // Di chuyển sang phải (deltaX > 0): quay ngược chiều (âm)
                    // Di chuyển sang trái (deltaX < 0): quay thuận chiều (dương)
                    float smallAmount = (deltaX > 0) ? -smallFlickStrength : smallFlickStrength;
                    // Scale thêm với độ lớn di chuyển để mượt mà hơn (tỷ lệ với |deltaX|)
                    smallAmount *= Math.abs(deltaX) / 50f; // Điều chỉnh divisor để kiểm soát độ nhạy (50f là ví dụ)
                    applyFlick(smallAmount);
                    lastTouchX = event.getX();
                    return true;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    // Không cần xử lý thêm vì fling đã được GestureDetector xử lý
                    return true;
            }
            return false;
        });

        String imagePath = getIntent().getStringExtra("IMAGE_PATH");
        if (!TextUtils.isEmpty(imagePath)) {
            addImageToSpinner(imagePath);
        } else {
            finish();
            return;
        }

        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.finish();
            }
        });
    }

    private void showHandTut() {
        // 1. Tính toán khoảng cách di chuyển (ví dụ: qua trái 60dp, qua phải 60dp)
        float distancePx = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 60, getResources().getDisplayMetrics());

        // 2. Tạo hiệu ứng di chuyển ngang (translationX)
        // Di chuyển từ -distancePx (trái) đến distancePx (phải)
        ObjectAnimator animator = ObjectAnimator.ofFloat(binding.tutHand, "translationX", -distancePx, distancePx);

        // Thiết lập thời gian cho 1 lượt (đi từ trái sang phải hết 500ms)
        animator.setDuration(800);

        // Thiết lập kiểu lặp: REVERSE (đi tới rồi đi lui)
        animator.setRepeatMode(ValueAnimator.REVERSE);

        // Tính toán số lần lặp để khớp với 3 giây:
        // 1 lượt = 0.5s -> 3s cần 6 lượt. repeatCount = 5 (vì lượt đầu không tính là lặp)
        animator.setRepeatCount(4);

        // 3. Lắng nghe sự kiện kết thúc
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                // Tự động ẩn toàn bộ layout hướng dẫn khi xong 3s
                binding.layoutTut.setVisibility(View.GONE);
            }
        });

        // Bắt đầu chạy
        animator.start();
    }

    private void demoGocNghieng() {
        // Tổng thời gian 2s cho 3 giai đoạn: 90->30 (666ms), 30->90 (666ms), 90->50 (668ms)
        final long durationPerSegment = 666L; // Khoảng 2/3 giây mỗi segment

        // Giai đoạn 1: Từ 90 về 30
        ValueAnimator animator1 = ValueAnimator.ofFloat(maxPitch, minPitch);
        animator1.setDuration(durationPerSegment);
        animator1.setInterpolator(new LinearInterpolator());
        animator1.addUpdateListener(animation -> {
            pitch = (float) animation.getAnimatedValue();
            updatePitchVisuals();
        });

        // Giai đoạn 2: Từ 30 về 90
        ValueAnimator animator2 = ValueAnimator.ofFloat(minPitch, maxPitch);
        animator2.setDuration(durationPerSegment);
        animator2.setInterpolator(new LinearInterpolator());
        animator2.addUpdateListener(animation -> {
            pitch = (float) animation.getAnimatedValue();
            updatePitchVisuals();
        });

        // Giai đoạn 3: Từ 90 về 50 (mặc định)
        ValueAnimator animator3 = ValueAnimator.ofFloat(maxPitch, 50f);
        animator3.setDuration(2000L - 2 * durationPerSegment); // Phần còn lại để tổng là 2s
        animator3.setInterpolator(new LinearInterpolator());
        animator3.addUpdateListener(animation -> {
            pitch = (float) animation.getAnimatedValue();
            updatePitchVisuals();
        });

        // Chain các animator: start animator2 khi animator1 end, start animator3 khi animator2 end
        animator1.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                animator2.start();
            }
        });
        animator2.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                animator3.start();
            }
        });

        // Bắt đầu từ animator1, nhưng trước tiên set pitch ban đầu là 90 để demo bắt đầu từ đó
        pitch = maxPitch;
        updatePitchVisuals();
        animator1.start();
    }

    private void updatePitchVisuals() {
        float rotationAngle = maxPitch - pitch;
        spinningImageViewUp.setRotationX(rotationAngle);
        spinningImageViewDown.setRotationX(-rotationAngle);

        float margin = maxMarginTop * (maxPitch - pitch) / (maxPitch - minPitch);
        float halfMargin = margin / 2f;
        float density = getResources().getDisplayMetrics().density;

        RelativeLayout.LayoutParams upParams = (RelativeLayout.LayoutParams) spinningImageViewUp.getLayoutParams();
        upParams.topMargin = (int) (-halfMargin * density);
        spinningImageViewUp.setLayoutParams(upParams);

        RelativeLayout.LayoutParams downParams = (RelativeLayout.LayoutParams) spinningImageViewDown.getLayoutParams();
        downParams.topMargin = (int) (halfMargin * density);
        spinningImageViewDown.setLayoutParams(downParams);
    }

    private void startSpinAnimation() {
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
            spinningLayout.setOnTouchListener(null);
            return;
        }

        // Thiết lập hiệu ứng 3D
        float distance = 8000 * getResources().getDisplayMetrics().density;
        spinningImageViewUp.setCameraDistance(distance);
        spinningImageViewDown.setCameraDistance(distance);

        // Thiết lập góc nghiêng và marginTop ban đầu
        float rotationAngle = maxPitch - pitch; // 90 - pitch để pitch = 90 cho rotationX = 0
        spinningImageViewUp.setRotationX(rotationAngle);
        spinningImageViewDown.setRotationX(-rotationAngle);
        // Tính margin tổng để điều chỉnh khoảng cách
        float margin = maxMarginTop * (maxPitch - pitch) / (maxPitch - minPitch);
        float halfMargin = margin / 2f;
        float density = getResources().getDisplayMetrics().density;
        RelativeLayout.LayoutParams upParams = (RelativeLayout.LayoutParams) spinningImageViewUp.getLayoutParams();
        upParams.topMargin = (int) (-halfMargin * density); // Chuyển dp sang px, di chuyển lên
        spinningImageViewUp.setLayoutParams(upParams);
        RelativeLayout.LayoutParams downParams = (RelativeLayout.LayoutParams) spinningImageViewDown.getLayoutParams();
        downParams.topMargin = (int) (halfMargin * density); // Chuyển dp sang px, di chuyển xuống
        spinningImageViewDown.setLayoutParams(downParams);

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
                        float newMargin = maxMarginTop * (maxPitch - pitch) / (maxPitch - minPitch);
                        float newHalfMargin = newMargin / 2f;
                        float newDensity = getResources().getDisplayMetrics().density;
                        RelativeLayout.LayoutParams newUpParams = (RelativeLayout.LayoutParams) spinningImageViewUp.getLayoutParams();
                        newUpParams.topMargin = (int) (-newHalfMargin * newDensity); // di chuyển lên
                        spinningImageViewUp.setLayoutParams(newUpParams);
                        RelativeLayout.LayoutParams newDownParams = (RelativeLayout.LayoutParams) spinningImageViewDown.getLayoutParams();
                        newDownParams.topMargin = (int) (newHalfMargin * newDensity); // di chuyển xuống
                        spinningImageViewDown.setLayoutParams(newDownParams);
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

            // Áp dụng ma sát (hỗ trợ cả chiều dương và âm)
            float frictionAmount = friction * deltaTime;
            if (Math.abs(currentRPM) > frictionAmount) {
                currentRPM -= Math.signum(currentRPM) * frictionAmount;
            } else {
                currentRPM = 0f;
            }

            // Tính và áp dụng góc quay
            if (currentRPM != 0) {
                float degreesPerSecond = currentRPM * 6; // Dương: thuận chiều, Âm: ngược chiều
                float degreesToRotate = degreesPerSecond * deltaTime;
                spinningImageViewUp.setRotation(spinningImageViewUp.getRotation() + degreesToRotate);
                spinningImageViewDown.setRotation(spinningImageViewDown.getRotation() + degreesToRotate);
            }
        });

        spinnerAnimator.start();
    }

    private void flickSpinner() {
        // Tăng tốc độ quay khi nhấn button (thuận chiều kim đồng hồ)
        applyFlick(flickStrength);
    }

    private void applyFlick(float amount) {
        // Bắt đầu animation nếu chưa chạy
        if (spinnerAnimator == null || !spinnerAnimator.isRunning()) {
            startSpinAnimation();
        }
        // Áp dụng lực (có thể dương hoặc âm). Nếu amount ngược dấu với currentRPM, sẽ làm chậm đi.
        // ====================== PHANH NHANH HƠN KHI > 300 RPM ======================
        // Chỉ áp dụng khi vuốt NGƯỢC chiều quay và tốc độ cao
        if (currentRPM != 0f
                && Math.signum(amount) == -Math.signum(currentRPM)
                && Math.abs(currentRPM) > 300f) {

            amount *= brakeMultiplierHighSpeed;   // Phanh mạnh gấp 3 lần
        }
        // =============================================================================

        // Áp dụng lực (giữ nguyên logic cũ)
        currentRPM += amount;
    }

    private void addImageToSpinner(String urlImage) {
        Glide.with(this)
                .load(urlImage)
                .into(spinningImageViewUp);
        Glide.with(this)
                .load(urlImage)
                .into(spinningImageViewDown);
    }

    private Runnable runnableUpdateText = new Runnable() {
        @Override
        public void run() {
            try {
                binding.txtSpeedPhut.setText("" + Math.abs((int) currentRPM));

                if (currentRPM != 0 && timeStartCount == 0) {
                    timeStartCount = Utils.timeNow();
                }
                if (timeStartCount > 0) {
                    long dentaTime = (Utils.timeNow() - timeStartCount) / 1000;
                    binding.txtTimePlay.setText(Utils.formatTimeMMSS(dentaTime));
                }
            } catch (Exception e) {
            }

            if (isOnScreen) {
                handler.postDelayed(runnableUpdateText, 200);
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        activity = this;
        isOnScreen = true;

        showBannerCollapActivity();

        handler.postDelayed(runnableUpdateText, 1000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        isOnScreen = false;
    }
}