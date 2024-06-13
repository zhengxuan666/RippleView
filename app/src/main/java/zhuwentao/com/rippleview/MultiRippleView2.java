package zhuwentao.com.rippleview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class MultiRippleView2 extends View {
    private Paint mPaint;
    private float maxRadius = 300; // 最大半径
    private float strokeWidth = 40; // 最大线条宽度
    private List<Ripple> ripples; // 水波纹列表

    // View宽
    private float mWidth;

    // View高
    private float mHeight;
    // 圆圈扩散的速度
    private float mSpeed;
    // 圆圈之间的密度
    private int mDensity;
    // 圆圈的颜色
    private int mColor;
    // 圆圈是否为填充模式
    private boolean mIsFill;
    // 圆圈是否为渐变模式
    private boolean mIsAlpha;
    private Context mContext;
    private Ripple zeroRipple;
    private Runnable runnable;
    private int mCount;

    class Ripple {
        float radius; // 半径
        float speed; // 扩散速度

        int alpha = 255; // 透明度

        Ripple(float radius, float speed) {
            this.radius = radius;
            this.speed = speed;
        }

        public Ripple(float radius, float speed, int alpha) {
            this.radius = radius;
            this.speed = speed;
            this.alpha = alpha;
        }

        // 更新水波纹的状态
        void update() {
            this.radius += this.speed;
            if (this.radius > maxRadius) {
                this.radius = 0; // 重置半径
            }
            if (mIsAlpha){
                double alpha = 255 - radius * (255 / ((double) mWidth / 2));
                this.alpha = (int) alpha;
            }
        }
    }

    public MultiRippleView2(Context context) {
        this(context, null);
    }

    public MultiRippleView2(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MultiRippleView2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray tya = context.obtainStyledAttributes(attrs, R.styleable.MultiRippleView2);
        mColor = tya.getColor(R.styleable.MultiRippleView2_Color, Color.BLUE);
        mSpeed = tya.getInt(R.styleable.MultiRippleView2_Speed, 2);
        mDensity = tya.getInt(R.styleable.MultiRippleView2_Density, 10);
        mIsFill = tya.getBoolean(R.styleable.MultiRippleView2_IsFill, false);
        mIsAlpha = tya.getBoolean(R.styleable.MultiRippleView2_IsAlpha, true);
        tya.recycle();

        init();
        // 设置View的圆为半透明
        setBackgroundColor(Color.TRANSPARENT);
    }

    private void init() {
        mContext = getContext();

        // 设置画笔样式
        mPaint = new Paint();
        mPaint.setColor(mColor);
        strokeWidth = DensityUtil.dip2px(mContext, 8);
        mPaint.setStrokeWidth(strokeWidth);
        if (mIsFill) {
            mPaint.setStyle(Paint.Style.FILL);
        } else {
            mPaint.setStyle(Paint.Style.STROKE);
        }
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setAntiAlias(true);

        mDensity = DensityUtil.dip2px(mContext, mDensity);


        ripples = new ArrayList<>();

        // 第一个水波纹
        zeroRipple = new Ripple(0, mSpeed);
        runnable = new Runnable() {
            @Override
            public void run() {
                update();
            }
        };
    }

    private void update() {
        for (Ripple ripple : ripples) {
            ripple.update();
        }
        if (ripples.size() < mCount) {
            if (ripples.get(ripples.size() - 1).radius >= mDensity) {
                ripples.add(new Ripple(0, mSpeed));
            }
        }
        invalidate(); // 重绘视图
        postDelayed(runnable, 20); // 每16毫秒更新一次，约等于60FPS
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int myWidthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int myWidthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int myHeightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int myHeightSpecSize = MeasureSpec.getSize(heightMeasureSpec);

        // 获取宽度
        if (myWidthSpecMode == MeasureSpec.EXACTLY) {
            // match_parent
            mWidth = myWidthSpecSize;
        } else {
            // wrap_content
            mWidth = DensityUtil.dip2px(mContext, 120);
        }

        // 获取高度
        if (myHeightSpecMode == MeasureSpec.EXACTLY) {
            mHeight = myHeightSpecSize;
        } else {
            // wrap_content
            mHeight = DensityUtil.dip2px(mContext, 120);
        }
        maxRadius = mWidth / 2;

        // 设置该view的宽高
        setMeasuredDimension((int) mWidth, (int) mHeight);

        ripples.clear();
        ripples.add(zeroRipple);

        mCount = (int) (mWidth / 2 / mDensity);


        removeCallbacks(runnable);
        update();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;

        for (Ripple ripple : ripples) {
            float currentStrokeWidth = strokeWidth * (1 - ripple.radius / maxRadius);
            mPaint.setStrokeWidth(currentStrokeWidth);
            mPaint.setAlpha(ripple.alpha);
            canvas.drawCircle(centerX, centerY, ripple.radius, mPaint);
        }
    }
}