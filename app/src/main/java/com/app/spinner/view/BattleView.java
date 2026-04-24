package com.app.spinner.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BattleView extends View {

    public interface OnBattleListener {
        void onCollision(float speedYou, float speedP2);
        void onGameOver(boolean youWin);
        void onUpdateP2Emoji(String emoji);
    }

    private OnBattleListener listener;
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint sparkPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint emojiPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint debrisPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint confettiPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    
    private Bitmap spinnerYou, spinnerP2;
    private int colorYou, colorP2;
    private float x1, y1, vx1, vy1;
    private float x2, y2, vx2, vy2;
    private float logicRpm1, logicRpm2;
    private float visualAngle1, visualAngle2;
    private final float radius = 175;   // size spinner to nhỏ
    private final float VISUAL_RPM = 300f;  // vòng quay mặc định
    
    private static final float NORMAL_SPEED = 20f;  // tốc độ di chuyển
    private static final float MAGNETIC_FORCE = 0.8f; 
    private static final float MIN_DISTANCE_FOR_ATTRACTION = 350f;

    private float rpmReduction = 10;    // giảm bao nhiêu 1 lần lúc va chạm (càng thấp time trận đấu càng lâu)

    private final List<Spark> sparks = new ArrayList<>();
    private final List<FloatingEmoji> emojis = new ArrayList<>();
    private final List<GlassShard> fragments = new ArrayList<>();
    private final List<Confetti> confettiList = new ArrayList<>();
    private final Random random = new Random();
    private long lastTime;
    private boolean isGameOver = false;
    private boolean isWaitingToStart = true;
    private boolean celebrationStarted = false;
    private boolean youWinBattle = false;
    private long battleStartTime;
    private long lastEmojiChangeTime;
    private long lastConfettiBurstTime;
    private final String[] p2Emojis = {"😱", "😤", "😜", "😏", "🔥", "⚡️", "😵", "🤡"};
    
    private final Path clipPath = new Path();
    private final RectF viewRect = new RectF();

    public BattleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        sparkPaint.setStrokeWidth(4);
        emojiPaint.setTextSize(60);
        debrisPaint.setStyle(Paint.Style.FILL);
        confettiPaint.setStyle(Paint.Style.FILL);
    }

    public void setRpmReduction(float reduction) {
        this.rpmReduction = reduction;
    }

    public void init(Bitmap s1, Bitmap s2, int c1, int c2, float startRpm1, float startRpm2, OnBattleListener listener) {
        this.spinnerYou = s1;
        this.spinnerP2 = s2;
        this.colorYou = c1;
        this.colorP2 = c2;
        this.logicRpm1 = startRpm1;
        this.logicRpm2 = startRpm2;
        this.listener = listener;
        this.isGameOver = false;
        this.isWaitingToStart = true;
        this.celebrationStarted = false;
        this.battleStartTime = System.currentTimeMillis();
        this.lastEmojiChangeTime = 0;
        this.lastConfettiBurstTime = 0;
        
        fragments.clear();
        sparks.clear();
        emojis.clear();
        confettiList.clear();
        
        lastTime = System.currentTimeMillis();
        setupStartingPositions();
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w > 0 && h > 0) {
            setupStartingPositions();
            viewRect.set(0, 0, w, h);
            clipPath.reset();
            clipPath.addRoundRect(viewRect, 45, 45, Path.Direction.CW); 
        }
    }

    private void setupStartingPositions() {
        if (getWidth() == 0) return;
        x2 = getWidth() / 2f + (random.nextFloat() - 0.5f) * 200;
        y2 = radius + 20;
        x1 = getWidth() / 2f + (random.nextFloat() - 0.5f) * 200;
        y1 = getHeight() - radius - 20;
        vx1 = 0; vy1 = 0;
        vx2 = 0; vy2 = 0;
    }

    public void spawnEmoji(String emoji, boolean fromYou) {
        float startX, startY;
        if (fromYou) {
            startX = getWidth();
            startY = getHeight();
        } else {
            startX = 0;
            startY = 0;
        }
        emojis.add(new FloatingEmoji(emoji, startX, startY, fromYou, random));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        long currentTime = System.currentTimeMillis();
        float dt = (currentTime - lastTime) / 1000f;
        lastTime = currentTime;

        drawConfetti(canvas, dt);

        canvas.save();
        canvas.clipPath(clipPath);

        if (!isGameOver && currentTime - lastEmojiChangeTime > 1500) {
            lastEmojiChangeTime = currentTime;
            if (listener != null) {
                listener.onUpdateP2Emoji(p2Emojis[random.nextInt(p2Emojis.length)]);
                spawnEmoji(p2Emojis[random.nextInt(p2Emojis.length)], false);
            }
        }

        if (isWaitingToStart && currentTime - battleStartTime > 2000) {
            isWaitingToStart = false;
            float dx = x2 - x1;
            float dy = y2 - y1;
            double angle = Math.atan2(dy, dx);
            double variation = (random.nextFloat() * 10 - 5) * (Math.PI / 180.0);
            angle += variation;
            vx1 = (float) Math.cos(angle) * NORMAL_SPEED;
            vy1 = (float) Math.sin(angle) * NORMAL_SPEED;
            vx2 = -vx1;
            vy2 = -vy1;
        }

        if (celebrationStarted && currentTime - lastConfettiBurstTime > 100) {
            lastConfettiBurstTime = currentTime;
            spawnConfettiBurst(youWinBattle);
        }

        updatePhysics(dt);
        drawDebris(canvas, dt);
        if (spinnerYou != null) drawSpinner(canvas, spinnerYou, x1, y1, visualAngle1);
        if (spinnerP2 != null) drawSpinner(canvas, spinnerP2, x2, y2, visualAngle2);
        drawSparks(canvas, dt);
        drawEmojis(canvas, dt);
        
        canvas.restore();
        invalidate();
    }

    private void drawSpinner(Canvas canvas, Bitmap b, float x, float y, float angle) {
        canvas.save();
        canvas.translate(x, y);
        canvas.rotate(angle);
        float scale = (radius * 2) / Math.max(b.getWidth(), b.getHeight());
        canvas.scale(scale, scale);
        canvas.drawBitmap(b, -b.getWidth() / 2f, -b.getHeight() / 2f, paint);
        canvas.restore();
    }

    private void updatePhysics(float dt) {
        visualAngle1 += VISUAL_RPM * dt * 6;
        visualAngle2 += VISUAL_RPM * dt * 6;

        if (isWaitingToStart) return;

        if (isGameOver) {
            float gameOverFriction = 0.995f; 
            if (spinnerYou != null) {
                vx1 *= gameOverFriction; vy1 *= gameOverFriction;
                x1 += vx1; y1 += vy1;
                handleWallCollision(true);
            }
            if (spinnerP2 != null) {
                vx2 *= gameOverFriction; vy2 *= gameOverFriction;
                x2 += vx2; y2 += vy2;
                handleWallCollision(false);
            }
            return;
        }

        if (spinnerYou != null && spinnerP2 != null) {
            float dx = x2 - x1;
            float dy = y2 - y1;
            float dist = (float) Math.sqrt(dx * dx + dy * dy);
            
            if (dist > MIN_DISTANCE_FOR_ATTRACTION) {
                float ax = (dx / dist) * MAGNETIC_FORCE;
                float ay = (dy / dist) * MAGNETIC_FORCE;
                vx1 += ax; vy1 += ay;
                vx2 -= ax; vy2 -= ay;
            }
            
            normalizeVelocity(true);
            normalizeVelocity(false);
        }

        if (spinnerYou != null) {
            x1 += vx1; y1 += vy1;
            handleWallCollision(true);
        }
        if (spinnerP2 != null) {
            x2 += vx2; y2 += vy2;
            handleWallCollision(false);
        }

        if (spinnerYou != null && spinnerP2 != null) {
            float dx = x1 - x2;
            float dy = y1 - y2;
            float dist = (float) Math.sqrt(dx * dx + dy * dy);
            if (dist < radius * 1.6f) {
                handleCollision(dx, dy, dist);
            }
        }
    }

    private void handleWallCollision(boolean isYou) {
        if (isYou) {
            if (x1 < radius || x1 > getWidth() - radius) vx1 = -vx1;
            if (y1 < radius || y1 > getHeight() - radius) vy1 = -vy1;
            x1 = Math.max(radius, Math.min(x1, getWidth() - radius));
            y1 = Math.max(radius, Math.min(y1, getHeight() - radius));
        } else {
            if (x2 < radius || x2 > getWidth() - radius) vx2 = -vx2;
            if (y2 < radius || y2 > getHeight() - radius) vy2 = -vy2;
            x2 = Math.max(radius, Math.min(x2, getWidth() - radius));
            y2 = Math.max(radius, Math.min(y2, getHeight() - radius));
        }
    }

    private void handleCollision(float dx, float dy, float dist) {
        float nx = dx / dist;
        float ny = dy / dist;
        float v1n = vx1 * nx + vy1 * ny;
        float v2n = vx2 * nx + vy2 * ny;
        float overlap = radius * 1.6f - dist;
        x1 += nx * (overlap / 2f + 5); 
        y1 += ny * (overlap / 2f + 5);
        x2 -= nx * (overlap / 2f + 5);
        y2 -= ny * (overlap / 2f + 5);
        float temp = v1n;
        v1n = v2n;
        v2n = temp;
        vx1 = vx1 - (temp - v1n) * nx;
        vy1 = vy1 - (temp - v1n) * ny;
        vx2 = vx2 - (v2n - temp) * nx;
        vy2 = vy2 - (v2n - temp) * ny;
        float kick = 15f; 
        vx1 += nx * kick;
        vy1 += ny * kick;
        vx2 -= nx * kick;
        vy2 -= ny * kick;
        normalizeVelocity(true);
        normalizeVelocity(false);
        logicRpm1 = Math.max(0, logicRpm1 - rpmReduction);
        logicRpm2 = Math.max(0, logicRpm2 - rpmReduction);
        
        float cx = (x1 + x2) / 2f;
        float cy = (y1 + y2) / 2f;
        for (int i = 0; i < 40; i++) sparks.add(new Spark(cx, cy, random));

        if (listener != null) {
            listener.onCollision(logicRpm1, logicRpm2);
            if (logicRpm1 <= 0 || logicRpm2 <= 0) {
                isGameOver = true;
                youWinBattle = logicRpm1 > 0;
                shatterSpinner(!youWinBattle);
                postDelayed(() -> celebrationStarted = true, 5000);
                postDelayed(() -> {
                    if (listener != null) listener.onGameOver(youWinBattle);
                }, 10000);
            }
        }
    }

    private void normalizeVelocity(boolean isYou) {
        float vx = isYou ? vx1 : vx2;
        float vy = isYou ? vy1 : vy2;
        float currentSpeed = (float) Math.sqrt(vx * vx + vy * vy);
        if (currentSpeed > 0) {
            float targetSpeed = NORMAL_SPEED; 
            float factor = targetSpeed / currentSpeed;
            if (isYou) { vx1 *= factor; vy1 *= factor; }
            else { vx2 *= factor; vy2 *= factor; }
        }
    }

    private void spawnConfettiBurst(boolean youWin) {
        float sx, sy;
        if (youWin) {
            sx = getWidth() - 50;
            sy = getHeight() - 50;
        } else {
            sx = 50;
            sy = 50;
        }
        for (int i = 0; i < 20; i++) {
            confettiList.add(new Confetti(sx, sy, random, youWin));
        }
    }

    private void shatterSpinner(boolean isYou) {
        float sx = isYou ? x1 : x2;
        float sy = isYou ? y1 : y2;
        int shatterColor = isYou ? colorYou : colorP2;
        for (int i = 0; i < 15; i++) { 
            fragments.add(new GlassShard(sx, sy, shatterColor, random));
        }
        
        if (isYou) spinnerYou = null;
        else spinnerP2 = null;
    }

    private void drawSparks(Canvas canvas, float dt) {
        for (int i = sparks.size() - 1; i >= 0; i--) {
            Spark s = sparks.get(i);
            s.update(dt);
            if (s.life <= 0) sparks.remove(i);
            else {
                sparkPaint.setColor(s.color);
                sparkPaint.setAlpha((int) (s.life * 255));
                canvas.drawLine(s.x, s.y, s.x - s.vx * 0.035f, s.y - s.vy * 0.035f, sparkPaint);
            }
        }
    }

    private void drawDebris(Canvas canvas, float dt) {
        for (int i = fragments.size() - 1; i >= 0; i--) {
            GlassShard d = fragments.get(i);
            d.update(dt);
            debrisPaint.setColor(d.color);
            debrisPaint.setAlpha((int) (Math.max(0.2f, d.life) * 255));
            canvas.save();
            canvas.translate(d.x, d.y);
            canvas.rotate(d.angle);
            canvas.drawPath(d.shardPath, debrisPaint);
            canvas.restore();
        }
    }

    private void drawEmojis(Canvas canvas, float dt) {
        for (int i = emojis.size() - 1; i >= 0; i--) {
            FloatingEmoji e = emojis.get(i);
            e.update(dt);
            if (e.life <= 0) emojis.remove(i);
            else {
                emojiPaint.setAlpha((int) (e.life * 255));
                canvas.drawText(e.text, e.x, e.y, emojiPaint);
            }
        }
    }

    private void drawConfetti(Canvas canvas, float dt) {
        for (int i = confettiList.size() - 1; i >= 0; i--) {
            Confetti c = confettiList.get(i);
            c.update(dt);
            if (c.life <= 0) confettiList.remove(i);
            else {
                confettiPaint.setColor(c.color);
                confettiPaint.setAlpha((int) (c.life * 255));
                canvas.save();
                canvas.translate(c.x, c.y);
                canvas.rotate(c.angle);
                canvas.drawPath(c.triPath, confettiPaint);
                canvas.restore();
            }
        }
    }

    private static class Spark {
        float x, y, vx, vy, life;
        int color;
        Spark(float x, float y, Random r) {
            this.x = x; this.y = y;
            this.vx = (r.nextFloat() - 0.5f) * 1500;
            this.vy = (r.nextFloat() - 0.5f) * 1500;
            this.life = 0.7f; // Increased life
            // Single blended Red-Yellow (Orange-ish) color
            this.color = Color.rgb(255, 100 + r.nextInt(100), 0);
        }
        void update(float dt) {
            x += vx * dt; y += vy * dt;
            life -= dt * 3.0f; // Slower decay
        }
    }

    private static class Confetti {
        float x, y, vx, vy, life, angle, vAngle;
        int color;
        Path triPath;
        Confetti(float sx, float sy, Random r, boolean fromBottom) {
            this.x = sx; this.y = sy;
            if (fromBottom) {
                this.vx = -400 - r.nextFloat() * 1200; 
                this.vy = -400 - r.nextFloat() * 1200; 
            } else {
                this.vx = 400 + r.nextFloat() * 1200; 
                this.vy = 400 + r.nextFloat() * 1200; 
            }
            this.life = 2.0f;
            this.angle = r.nextFloat() * 360;
            this.vAngle = (r.nextFloat() - 0.5f) * 1080;
            int[] colors = {Color.RED, Color.YELLOW, Color.GREEN, Color.BLUE, Color.MAGENTA, Color.CYAN, Color.WHITE};
            this.color = colors[r.nextInt(colors.length)];
            triPath = new Path();
            float size = 15 + r.nextFloat() * 15;
            triPath.moveTo(0, -size);
            triPath.lineTo(size, size);
            triPath.lineTo(-size, size);
            triPath.close();
        }
        void update(float dt) {
            x += vx * dt; y += vy * dt;
            angle += vAngle * dt;
            life -= dt * 1.5f; 
        }
    }

    private static class GlassShard {
        float x, y, vx, vy, life, angle, vAngle;
        float targetX, targetY;
        int color;
        Path shardPath;
        boolean settled = false;

        GlassShard(float sx, float sy, int color, Random r) {
            this.x = sx; this.y = sy;
            this.color = color;
            this.life = 4.0f;
            this.angle = r.nextFloat() * 360;
            this.vAngle = (r.nextFloat() - 0.5f) * 1440;
            double scatterAngle = r.nextFloat() * 2 * Math.PI;
            float dist = 100 + r.nextFloat() * 300;
            this.targetX = sx + (float) (Math.cos(scatterAngle) * dist);
            this.targetY = sy + (float) (Math.sin(scatterAngle) * dist);
            this.vx = (targetX - sx) * 5; 
            this.vy = (targetY - sy) * 5;
            
            shardPath = new Path();
            int numPoints = 3 + r.nextInt(5); 
            float s = 60 + r.nextFloat() * 50; // Smaller shards
            for (int j = 0; j < numPoints; j++) {
                float pointAngle = (float) (j * (2 * Math.PI / numPoints) + (r.nextFloat() - 0.5f) * 0.5);
                float currentRadius = s * (0.4f + 0.6f * r.nextFloat());
                float px = (float) Math.cos(pointAngle) * currentRadius;
                float py = (float) Math.sin(pointAngle) * currentRadius;
                if (j == 0) shardPath.moveTo(px, py);
                else shardPath.lineTo(px, py);
            }
            shardPath.close();
        }

        void update(float dt) {
            if (settled) {
                life -= dt * 0.02f;
                return;
            }
            x += vx * dt; y += vy * dt;
            angle += vAngle * dt;
            vx *= 0.90f; vy *= 0.90f; vAngle *= 0.90f;
            life -= dt * 0.2f;
            if (Math.abs(vx) < 3 && Math.abs(vy) < 3) {
                settled = true;
                vx = 0; vy = 0; vAngle = 0;
            }
        }
    }

    private static class FloatingEmoji {
        String text;
        float x, y, vx, vy, life;
        FloatingEmoji(String text, float x, float y, boolean fromYou, Random r) {
            this.text = text;
            this.x = x; this.y = y;
            if (fromYou) {
                this.vx = -100 - r.nextFloat() * 400; 
                this.vy = -100 - r.nextFloat() * 400;
            } else {
                this.vx = 100 + r.nextFloat() * 400;
                this.vy = 100 + r.nextFloat() * 400;
            }
            this.life = 1.5f;
        }
        void update(float dt) {
            x += vx * dt; y += vy * dt;
            life -= dt * 0.8f;
        }
    }
}
