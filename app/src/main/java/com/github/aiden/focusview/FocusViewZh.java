package com.github.aiden.focusview;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.github.aiden.focusview.util.DpUtils;

/**
 * @author sunwei
 * email：tianmu19@gmail.com
 * date：2019/7/17 21:25
 * package：com.github.aiden.focusview
 * version：1.0
 * <p>description：   仿知乎关注按钮           </p>
 */
public class FocusViewZh extends View {

    private int color_focus;
    private int color_unfocus;
    private String text_focus;
    private String text_unfocus;
    private Drawable drawable_progressive;
    private Paint paint;

    private int mWidth;
    private int mHeight;
    private boolean focus;
    //是否正在中间状态
    private boolean progressing;
    private Paint paint_text;
    private int rx = 10;
    private int ry  = 10;

    public FocusViewZh(Context context) {
        this(context, null);
    }

    public FocusViewZh(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FocusViewZh(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint_text = new Paint(Paint.ANTI_ALIAS_FLAG);
        if (null != attrs) {
            TypedArray attributes = context.getApplicationContext().obtainStyledAttributes(attrs, R.styleable.FocusViewZh);
            color_focus = attributes.getColor(R.styleable.FocusViewZh_color_focus, Color.BLUE);
            color_unfocus = attributes.getColor(R.styleable.FocusViewZh_color_unfocus, Color.GRAY);
            text_focus = attributes.getString(R.styleable.FocusViewZh_text_focus);
            text_unfocus = attributes.getString(R.styleable.FocusViewZh_text_unfocus);
            drawable_progressive = attributes.getDrawable(R.styleable.FocusViewZh_drawable_progressive);
            attributes.recycle();
        }
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setDither(true);
        paint_text.setStyle(Paint.Style.FILL_AND_STROKE);
        paint_text.setColor(Color.WHITE);
        paint_text.setTypeface(Typeface.DEFAULT_BOLD);
        paint_text.setTextSize(DpUtils.sp2px(context, 15));
        mWidth = DpUtils.dip2px(context, 100);
        mHeight = DpUtils.dip2px(context, 30);
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        RectF rect = new RectF(0, 0, mWidth, mHeight);
        if (progressing) {
            if (focus) {
                //已关注
                paint.setColor(color_focus);
                canvas.drawRoundRect(rect, rx, ry, paint);
            } else {
                //取消关注了
                paint.setColor(color_unfocus);
                canvas.drawRoundRect(rect, rx, ry, paint);
            }
            //中间状态，转圈
            BitmapDrawable bitmapDrawable = (BitmapDrawable) this.drawable_progressive;
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmapDrawable.getBitmap(), mHeight / 2, mHeight / 2, true);
            canvas.drawBitmap(scaledBitmap, (mWidth-scaledBitmap.getWidth())/2, (mHeight-scaledBitmap.getHeight())/2, paint);
            isAnimating = true;
        } else if (focus) {
            //已关注
            paint.setColor(color_focus);
            canvas.drawRoundRect(rect, rx, ry, paint);
            drawCenterText(canvas, paint_text, text_focus);
            isAnimating = true;
            RippleAnimation.create(this).setDuration(DURATION).setOnAnimationEndListener(new RippleAnimation.OnAnimationEndListener() {
                @Override
                public void onAnimationEnd() {
                    isAnimating = false;
                }
            }).start();
        } else {
            //取消关注了
            paint.setColor(color_unfocus);
            canvas.drawRoundRect(rect, rx, ry, paint);
            drawCenterText(canvas, paint_text, text_unfocus);
            isAnimating = true;
            RippleAnimation.create(this).setDuration(DURATION).setOnAnimationEndListener(new RippleAnimation.OnAnimationEndListener() {
                @Override
                public void onAnimationEnd() {
                    isAnimating = false;
                }
            }).start();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isAnimating) {
            return true;
        }
        return super.onTouchEvent(event);
    }

    private boolean isAnimating = false;

    private final static int DURATION = 2100;

    /**
     * 改变状态，重绘
     *
     * @param b
     */
    public void setFocus(boolean b) {
        this.focus = b;
        this.progressing = false;
        invalidate();
    }

    public void setProgressive() {
        this.progressing = true;
        invalidate();
    }

    public void setResetStatus() {
        this.progressing = false;
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int modew = MeasureSpec.getMode(widthMeasureSpec);
        int modeh = MeasureSpec.getMode(heightMeasureSpec);
        int sizew = MeasureSpec.getSize(widthMeasureSpec);
        int sizeh = MeasureSpec.getSize(heightMeasureSpec);
        if (modew == MeasureSpec.EXACTLY) {
            mWidth = sizew;
        }
        if (modeh == MeasureSpec.EXACTLY) {
            mHeight = sizeh;
        }
        setMeasuredDimension(mWidth, mHeight);
    }

    private void drawCenterText(Canvas canvas, Paint textPaint, String text) {
        Rect rect = new Rect(0, 0, mWidth, mHeight);
        textPaint.setTextAlign(Paint.Align.CENTER);

        Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
        float top = fontMetrics.top;
        float bottom = fontMetrics.bottom;

        int centerY = (int) (rect.centerY() - top / 2 - bottom / 2);

        canvas.drawText(text, rect.centerX(), centerY, textPaint);
    }

    public void setColor_focus(int color_focus) {
        this.color_focus = color_focus;
    }


    public void setColor_unfocus(int color_unfocus) {
        this.color_unfocus = color_unfocus;
    }


    public void setText_focus(String text_focus) {
        this.text_focus = text_focus;
    }


    public void setText_unfocus(String text_unfocus) {
        this.text_unfocus = text_unfocus;
    }


    public void setDrawable_progressive(Drawable drawable_progressive) {
        this.drawable_progressive = drawable_progressive;
    }

}
