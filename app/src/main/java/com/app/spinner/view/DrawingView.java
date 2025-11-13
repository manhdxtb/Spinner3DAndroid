package com.app.spinner.view;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DrawingView extends View {
    // =================================================================
    // =========== KHU VỰC CẤU HÌNH (BẠN CHỈNH SỬA TẠI ĐÂY) ===========
    // =================================================================
    private float innerStrokeWidth = 10f;
    private float outerStrokeWidth = 35f;
    private float glowRadius = 25f;
    private int symmetrySegments = 6;
    private int mirrorMode = 2;
    private int gocDoiXung = 10;

    // MỚI: Khoảng cách chặn vẽ gần viền (để glow không bị cắt)
    private float borderPaddingDp = 12f; // 5dp — bạn có thể tăng lên 8-10 nếu muốn an toàn hơn
    private float drawableRadius; // Sẽ được tính trong onSizeChanged

    // MỚI: Cấu hình loại nét vẽ
    private int strokeType = 1; // 1: Liền (mặc định), 2: Nét đứt (----), 3: Nét chấm (....), 4: Cầu vồng
    private float rainbowTileSize = 100f; // Có thể chỉnh số thành 150f, 100f, 50f ...  khoảng cách thay đổi màu cầu vồng khi strokeType = 4
    private final float[] dashEffectValues = new float[]{30f, 15f}; // [Nét liền, Khoảng trống] cho Type 2
    private final float[] dotEffectValues = new float[]{1f, 25f};   // [Nét liền, Khoảng trống] cho Type 3

    // =================================================================

    // THAY ĐỔI: Lớp Stroke giờ lưu cả 2 loại Paint
    private static class Stroke {
        Path[] paths;
        Paint outerPaint; // Đã đổi tên từ 'paint'
        Paint innerPaint; // MỚI:

        Stroke(Path[] paths, Paint outerPaint, Paint innerPaint) {
            this.paths = new Path[paths.length];
            for (int i = 0; i < paths.length; i++) {
                this.paths[i] = new Path(paths[i]);
            }
            // Lưu lại một bản sao của Paint tại thời điểm vẽ
            this.outerPaint = new Paint(outerPaint);
            this.innerPaint = new Paint(innerPaint);
        }
    }

    private final ArrayList<Stroke> strokes = new ArrayList<>();
    private final ArrayList<Stroke> undoneStrokes = new ArrayList<>();
    private Path[] symmetricalPaths;
    private float centerX, centerY;
    private final Paint innerPaint;
    private final Paint outerPaint;
    private final List<Integer> neonColors;
    private final Random random;
    private int viewSize;
    private Path clipPath;
    private Bitmap originalBackgroundBitmap;
    private Bitmap scaledBackgroundBitmap;
    private RectF viewBounds;

    // MỚI: Cho nét vẽ cầu vồng (Type 4)
    private int[] rainbowColors;
    private Shader rainbowShader;

    // === CỐ ĐỊNH TRỤC ĐỐI XỨNG TỪ LẦN CHẠM ĐẦU ===
    private static PointF globalVirtualBPoint = null;
    private static Float globalMirrorAxisAngle = null;
    private static boolean hasFirstTouch = false; // Đã có lần chạm đầu chưa?

    public DrawingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metrics);
        viewSize = (int) (metrics.widthPixels * 0.92);

        clipPath = new Path();
        random = new Random();

        innerPaint = new Paint();
        innerPaint.setColor(Color.WHITE);
        innerPaint.setAntiAlias(true);
        innerPaint.setStrokeWidth(innerStrokeWidth);
        innerPaint.setStyle(Paint.Style.STROKE);
        innerPaint.setStrokeJoin(Paint.Join.ROUND);
        innerPaint.setStrokeCap(Paint.Cap.ROUND); // Rất quan trọng cho nét chấm

        outerPaint = new Paint();
        outerPaint.setAntiAlias(true);
        outerPaint.setStrokeWidth(outerStrokeWidth);
        outerPaint.setStyle(Paint.Style.STROKE);
        outerPaint.setStrokeJoin(Paint.Join.ROUND);
        outerPaint.setStrokeCap(Paint.Cap.ROUND);
        outerPaint.setMaskFilter(new BlurMaskFilter(glowRadius, BlurMaskFilter.Blur.NORMAL));

        neonColors = new ArrayList<>();
        neonColors.add(Color.parseColor("#FF073A"));
        neonColors.add(Color.parseColor("#39FF14"));
        neonColors.add(Color.parseColor("#1F51FF"));
        neonColors.add(Color.parseColor("#FFFF00"));
        neonColors.add(Color.parseColor("#BC13FE"));

        // MỚI: Khởi tạo màu cầu vồng
        rainbowColors = new int[]{
                Color.parseColor("#FF073A"), // Red
                Color.parseColor("#FFFF00"), // Yellow
                Color.parseColor("#39FF14"), // Green
                Color.parseColor("#1F51FF"), // Blue
                Color.parseColor("#BC13FE"), // Purple
                Color.parseColor("#FF073A")  // Quay lại Red để lặp mượt
        };

        // MỚI: Áp dụng kiểu nét vẽ mặc định (Type 1)
        updateInnerPaint();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(viewSize, viewSize);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.centerX = (float) w / 2;
        this.centerY = (float) h / 2;
        viewBounds = new RectF(0, 0, w, h);

        float radius = Math.min(w, h) / 2f;
        float paddingPx = borderPaddingDp * getResources().getDisplayMetrics().density;
        this.drawableRadius = radius - paddingPx; // Vùng vẽ an toàn

        if (originalBackgroundBitmap != null) {
            updateScaledBackgroundBitmap();
        }

        clipPath.reset();
        clipPath.addCircle(centerX, centerY, radius, Path.Direction.CW);

        // MỚI: Tạo Shader cầu vồng
        rainbowShader = new LinearGradient(
                0, 0,               // Tọa độ bắt đầu (x, y)
                rainbowTileSize, 0, // Tọa độ kết thúc (x, y)
                rainbowColors,      // Mảng màu của bạn
                null,               // Phân bố màu đều
                Shader.TileMode.REPEAT // LẶP LẠI (Đây là mấu chốt)
        );

        // MỚI: Cập nhật lại paint nếu type hiện tại là 4
        if (strokeType == 4) {
            updateInnerPaint();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.clipPath(clipPath);
        if (scaledBackgroundBitmap != null) {
            canvas.drawBitmap(scaledBackgroundBitmap, 0, 0, null);
        } else {
            canvas.drawColor(Color.BLACK);
        }
        drawStrokes(canvas);
    }

    private void drawStrokes(Canvas canvas) {
        // THAY ĐỔI: Vẽ từ nét cũ đã lưu
        for (Stroke stroke : strokes) {
            for (Path path : stroke.paths) {
                // Dùng paint đã lưu trong từng Stroke
                canvas.drawPath(path, stroke.outerPaint);
                canvas.drawPath(path, stroke.innerPaint);
            }
        }

        // Vẽ nét hiện tại (đang vẽ live)
        if (symmetricalPaths != null) {
            for (Path path : symmetricalPaths) {
                // Dùng paint global (live)
                canvas.drawPath(path, outerPaint);
                canvas.drawPath(path, innerPaint);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();

        float dx = touchX - centerX;
        float dy = touchY - centerY;
        float distanceFromCenter = (float) Math.sqrt(dx * dx + dy * dy);

        // CHẶN: Nếu chạm quá gần viền → không xử lý MOVE, coi như đã UP
        if (distanceFromCenter > drawableRadius) {
            if (symmetricalPaths != null && event.getAction() == MotionEvent.ACTION_MOVE) {
                // Giả lập nhấc tay nếu đang vẽ và ra ngoài vùng an toàn
                if (symmetricalPaths != null) {
                    // THAY ĐỔI: Lưu cả 2 paint
                    strokes.add(new Stroke(symmetricalPaths, outerPaint, innerPaint));
                    symmetricalPaths = null;
                }
                invalidate();
                return true;
            }
            // Nếu là DOWN ngoài vùng → bỏ qua hoàn toàn
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                return true;
            }
        }

        int totalPaths = symmetrySegments * mirrorMode;
        PointF[][] allPoints = null;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (distanceFromCenter > drawableRadius) {
                    return true; // Bỏ qua chạm ngoài vùng
                }

                // === CHỈ TÍNH TRỤC OB 1 LẦN DUY NHẤT ===
                if (!hasFirstTouch && mirrorMode == 2) {
                    float angleA = (float) Math.atan2(dy, dx);
                    float radius = distanceFromCenter;
                    float angleB = angleA + (float) Math.toRadians(gocDoiXung);
                    float bX = centerX + radius * (float) Math.cos(angleB);
                    float bY = centerY + radius * (float) Math.sin(angleB);

                    globalVirtualBPoint = new PointF(bX, bY);
                    globalMirrorAxisAngle = angleB;
                    hasFirstTouch = true;
                }

                // Cấu hình outerPaint (glow)
                int randomColor = neonColors.get(random.nextInt(neonColors.size()));
                outerPaint.setColor(randomColor);

                // Cấu hình innerPaint (nét vẽ) đã được xử lý bởi setStrokeType()

                undoneStrokes.clear();

                symmetricalPaths = new Path[totalPaths];
                for (int i = 0; i < totalPaths; i++) {
                    symmetricalPaths[i] = new Path();
                }

                allPoints = calculateSymmetricalPoints(touchX, touchY);
                for (int i = 0; i < totalPaths; i++) {
                    PointF p = allPoints[i / symmetrySegments][i % symmetrySegments];
                    symmetricalPaths[i].moveTo(p.x, p.y);
                    symmetricalPaths[i].lineTo(p.x + 0.01f, p.y);
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if (symmetricalPaths != null && distanceFromCenter <= drawableRadius) {
                    allPoints = calculateSymmetricalPoints(touchX, touchY);
                    for (int i = 0; i < totalPaths; i++) {
                        PointF p = allPoints[i / symmetrySegments][i % symmetrySegments];
                        // Đảm bảo điểm đích cũng không vượt quá vùng an toàn
                        float px = p.x - centerX;
                        float py = p.y - centerY;
                        float dist = (float) Math.sqrt(px * px + py * py);
                        if (dist <= drawableRadius) {
                            symmetricalPaths[i].lineTo(p.x, p.y);
                        }
                    }
                }
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (symmetricalPaths != null) {
                    // THAY ĐỔI: Lưu cả 2 paint vào Stroke
                    strokes.add(new Stroke(symmetricalPaths, outerPaint, innerPaint));
                    symmetricalPaths = null;
                }
                break;
        }

        invalidate();
        return true;
    }

    private PointF[][] calculateSymmetricalPoints(float x, float y) {
        int origins = mirrorMode;
        PointF[][] result = new PointF[origins][symmetrySegments];

        PointF[] originsPoints = new PointF[origins];
        originsPoints[0] = new PointF(x, y);

        if (mirrorMode == 2 && globalMirrorAxisAngle != null) {
            originsPoints[1] = reflectPointOverAxis(new PointF(x, y), globalMirrorAxisAngle);
        }

        float angleStep = 360f / symmetrySegments;

        for (int orig = 0; orig < origins; orig++) {
            float px = originsPoints[orig].x - centerX;
            float py = originsPoints[orig].y - centerY;

            if (symmetrySegments <= 1) {
                result[orig][0] = new PointF(centerX + px, centerY + py);
                continue;
            }

            for (int i = 0; i < symmetrySegments; i++) {
                double angleRad = Math.toRadians(angleStep * i);
                double cos = Math.cos(angleRad);
                double sin = Math.sin(angleRad);
                float rotatedX = (float) (px * cos - py * sin);
                float rotatedY = (float) (px * sin + py * cos);
                result[orig][i] = new PointF(centerX + rotatedX, centerY + rotatedY);
            }
        }
        return result;
    }

    private PointF reflectPointOverAxis(PointF point, float axisAngleRad) {
        float px = point.x - centerX;
        float py = point.y - centerY;

        float cos = (float) Math.cos(-axisAngleRad);
        float sin = (float) Math.sin(-axisAngleRad);
        float x1 = px * cos - py * sin;
        float y1 = px * sin + py * cos;

        y1 = -y1;

        cos = (float) Math.cos(axisAngleRad);
        sin = (float) Math.sin(axisAngleRad);
        float x2 = x1 * cos - y1 * sin;
        float y2 = x1 * sin + y1 * cos;

        return new PointF(centerX + x2, centerY + y2);
    }

    private void updateScaledBackgroundBitmap() {
        if (originalBackgroundBitmap == null || getWidth() == 0 || getHeight() == 0) return;
        int viewWidth = getWidth();
        int viewHeight = getHeight();
        int bitmapWidth = originalBackgroundBitmap.getWidth();
        int bitmapHeight = originalBackgroundBitmap.getHeight();
        float scale;
        float dx = 0, dy = 0;
        if (bitmapWidth * viewHeight > viewWidth * bitmapHeight) {
            scale = (float) viewHeight / (float) bitmapHeight;
            dx = (viewWidth - bitmapWidth * scale) * 0.5f;
        } else {
            scale = (float) viewWidth / (float) bitmapWidth;
            dy = (viewHeight - bitmapHeight * scale) * 0.5f;
        }
        Matrix matrix = new Matrix();
        matrix.setScale(scale, scale);
        matrix.postTranslate(Math.round(dx), Math.round(dy));
        scaledBackgroundBitmap = Bitmap.createBitmap(viewWidth, viewHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(scaledBackgroundBitmap);
        canvas.drawBitmap(originalBackgroundBitmap, matrix, null);
    }

    // =================================================================
    // ======================= API CÔNG KHAI ===========================
    // =================================================================

    // MỚI: Hàm helper để cập nhật innerPaint dựa trên strokeType
    private void updateInnerPaint() {
        // Reset về trạng thái cơ bản
        innerPaint.setShader(null);
        innerPaint.setPathEffect(null);
        innerPaint.setColor(Color.WHITE);
        innerPaint.setStrokeCap(Paint.Cap.ROUND); // Luôn là ROUND

        switch (strokeType) {
            case 1: // Nét liền (mặc định)
                // Không cần làm gì thêm, đã reset ở trên
                break;
            case 2: // Nét đứt (----)
                innerPaint.setPathEffect(new DashPathEffect(dashEffectValues, 0));
                break;
            case 3: // Nét chấm (....)
                // Dùng DashPathEffect với stroke cap ROUND sẽ tạo ra nét chấm
                innerPaint.setPathEffect(new DashPathEffect(dotEffectValues, 0));
                break;
            case 4: // Nét cầu vồng
                if (rainbowShader != null) { // Đảm bảo shader đã được tạo (sau onSizeChanged)
                    innerPaint.setShader(rainbowShader);
                }
                // Shader sẽ ghi đè lên thiết lập Color.WHITE
                break;
        }
    }

    // MỚI: Hàm công khai để đổi kiểu nét vẽ
    public void setStrokeType(int type) {
        if (type < 1 || type > 4) {
            this.strokeType = 1; // Mặc định về 1 nếu giá trị không hợp lệ
        } else {
            this.strokeType = type;
        }
        // Cập nhật lại paint cho nét vẽ tiếp theo
        updateInnerPaint();
    }

    public int getStrokeType() {
        return this.strokeType;
    }

    public void setBackgroundImage(int resId) {
        originalBackgroundBitmap = BitmapFactory.decodeResource(getResources(), resId);
        updateScaledBackgroundBitmap();
        invalidate();
    }

    public Bitmap captureDrawing() {
        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.clipPath(clipPath);
        drawStrokes(canvas);
        return bitmap;
    }

    public void undo() {
        if (!strokes.isEmpty()) {
            undoneStrokes.add(strokes.remove(strokes.size() - 1));
            invalidate();
        }
    }

    public void redo() {
        if (!undoneStrokes.isEmpty()) {
            strokes.add(undoneStrokes.remove(undoneStrokes.size() - 1));
            invalidate();
        }
    }

    public void clear() {
        strokes.clear();
        undoneStrokes.clear();
        // === RESET TRỤC ĐỐI XỨNG ===
        globalVirtualBPoint = null;
        globalMirrorAxisAngle = null;
        hasFirstTouch = false;
        invalidate();
    }

    public void setSymmetrySegments(int segments) {
        if (segments > 0 && segments <= 12) {
            this.symmetrySegments = segments;
        }
    }

    public void setMirrorMode(int mode) {
        if (mode == 1 || mode == 2) {
            this.mirrorMode = mode;
        }
    }

    public void setGocDoiXung(int goc) {
        this.gocDoiXung = goc;
        // Nếu muốn reset trục khi đổi góc:
        globalVirtualBPoint = null;
        globalMirrorAxisAngle = null;
        hasFirstTouch = false;
    }

    public int getMirrorMode() {
        return mirrorMode;
    }
}