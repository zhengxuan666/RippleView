package zhuwentao.com.rippleview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import java.util.ArrayList;
import java.util.List;

public class MultiRippleView extends View {
    private Paint paint;
    private float maxRadius = 300; // 最大半径
    private float strokeWidth = 40; // 最大线条宽度
    private List<Ripple> ripples; // 水波纹列表

    class Ripple {
        float radius; // 半径
        float speed; // 扩散速度

        Ripple(float radius, float speed) {
            this.radius = radius;
            this.speed = speed;
        }

        // 更新水波纹的状态
        void update() {
            this.radius += this.speed;
            if (this.radius > maxRadius) {
                this.radius = 0; // 重置半径
            }
        }
    }

    public MultiRippleView(Context context) {
        this(context, null);
    }

    public MultiRippleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MultiRippleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(0xFF00AAEE); // 设置颜色为蓝色

        ripples = new ArrayList<>();
        // 初始化多个水波纹
        ripples.add(new Ripple(0, 3)); // 初始半径为0，速度为5
        ripples.add(new Ripple(100, 3)); // 初始半径为50，速度为3
        ripples.add(new Ripple(200, 3)); // 初始半径为100，速度为2
        update();
    }

    private void update() {
        for (Ripple ripple : ripples) {
            ripple.update();
        }
        invalidate(); // 重绘视图
        postDelayed(new Runnable() {
            @Override
            public void run() {
                update();
            }
        }, 16); // 每16毫秒更新一次，约等于60FPS
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;

        for (Ripple ripple : ripples) {
            float currentStrokeWidth = strokeWidth * (1 - ripple.radius / maxRadius);
            paint.setStrokeWidth(currentStrokeWidth);
            canvas.drawCircle(centerX, centerY, ripple.radius, paint);
        }
    }
}