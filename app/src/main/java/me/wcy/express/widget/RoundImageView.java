package me.wcy.express.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Xfermode;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

import me.wcy.express.R;

/**
 * 支持圆角和边框的ImageView
 * Created by hzwangchenyan on 2016/10/19.
 */
public class RoundImageView extends AppCompatImageView {
    private Paint mPaint = new Paint();
    private Path mPath = new Path();
    private Xfermode mXfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_IN);
    private RectF mRectF = new RectF();
    private float[] mRadii = new float[8];

    // 存储原始图像
    private Bitmap mOriginalBitmap;
    private Canvas mOriginalCanvas;

    private float mRadiusTopLeft;
    private float mRadiusTopRight;
    private float mRadiusBottomRight;
    private float mRadiusBottomLeft;

    private float mBorderWidth;
    private int mBorderColor;

    public RoundImageView(Context context) {
        super(context);
        init(null);
    }

    public RoundImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public RoundImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.RoundImageView);
        float radius = ta.getDimension(R.styleable.RoundImageView_riv_corner_radius, 0);
        mRadiusTopLeft = ta.getDimension(R.styleable.RoundImageView_riv_corner_radius_top_left, -1);
        mRadiusTopRight = ta.getDimension(R.styleable.RoundImageView_riv_corner_radius_top_right, -1);
        mRadiusBottomRight = ta.getDimension(R.styleable.RoundImageView_riv_corner_radius_bottom_right, -1);
        mRadiusBottomLeft = ta.getDimension(R.styleable.RoundImageView_riv_corner_radius_bottom_left, -1);

        radius = radius < 0 ? 0 : radius;
        mRadiusTopLeft = mRadiusTopLeft < 0 ? radius : mRadiusTopLeft;
        mRadiusTopRight = mRadiusTopRight < 0 ? radius : mRadiusTopRight;
        mRadiusBottomRight = mRadiusBottomRight < 0 ? radius : mRadiusBottomRight;
        mRadiusBottomLeft = mRadiusBottomLeft < 0 ? radius : mRadiusBottomLeft;

        mBorderWidth = ta.getDimension(R.styleable.RoundImageView_riv_border_width, 0);
        mBorderColor = ta.getColor(R.styleable.RoundImageView_riv_border_color, Color.WHITE);
        ta.recycle();

        setLayerType(LAYER_TYPE_HARDWARE, null);
    }

    public void setCornerRadius(float radius) {
        if (radius >= 0) {
            mRadiusTopLeft = mRadiusTopRight = mRadiusBottomRight = mRadiusBottomLeft = radius;
            postInvalidate();
        }
    }

    public void setCornerRadiusTopLeft(float radius) {
        if (radius >= 0) {
            mRadiusTopLeft = radius;
            postInvalidate();
        }
    }

    public void setCornerRadiusTopRight(float radius) {
        if (radius >= 0) {
            mRadiusTopRight = radius;
            postInvalidate();
        }
    }

    public void setCornerRadiusBottomRight(float radius) {
        if (radius >= 0) {
            mRadiusBottomRight = radius;
            postInvalidate();
        }
    }

    public void setCornerRadiusBottomLeft(float radius) {
        if (radius >= 0) {
            mRadiusBottomLeft = radius;
            postInvalidate();
        }
    }

    public void setBorderWidth(float borderWidth) {
        if (borderWidth > 0) {
            mBorderWidth = borderWidth;
            postInvalidate();
        }
    }

    public void setBorderColor(int borderColor) {
        mBorderColor = borderColor;
        postInvalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mOriginalBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mOriginalCanvas = new Canvas(mOriginalBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // 先清理画布
        mOriginalCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

        // 绘制原始图像
        super.onDraw(mOriginalCanvas);

        // 画原始图形
        mPaint.reset();
        mPaint.setAntiAlias(true);
        canvas.drawBitmap(mOriginalBitmap, 0, 0, mPaint);

        // 剪裁圆角
        mPath.reset();
        mRectF.set(0, 0, getWidth(), getHeight());
        initRadii();
        mPath.addRoundRect(mRectF, mRadii, Path.Direction.CW);

        mPaint.reset();
        mPaint.setAntiAlias(true);
        mPaint.setXfermode(mXfermode);
        canvas.drawPath(mPath, mPaint);

        // 画边框
        if (mBorderWidth > 0) {
            mPaint.reset();
            mPaint.setAntiAlias(true);
            mPaint.setColor(mBorderColor);
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeWidth(mBorderWidth * 2);
            canvas.drawPath(mPath, mPaint);

            mPaint.reset();
            mPaint.setAntiAlias(true);
            mPaint.setXfermode(mXfermode);
            canvas.drawPath(mPath, mPaint);
        }
    }

    private void initRadii() {
        mRadii[0] = mRadii[1] = mRadiusTopLeft;
        mRadii[2] = mRadii[3] = mRadiusTopRight;
        mRadii[4] = mRadii[5] = mRadiusBottomRight;
        mRadii[6] = mRadii[7] = mRadiusBottomLeft;
    }
}
