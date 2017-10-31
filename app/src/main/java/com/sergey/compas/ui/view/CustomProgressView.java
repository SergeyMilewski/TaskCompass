package com.sergey.compas.ui.view;

import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import com.sergey.compas.R;

/**
 * Created by smilevkiy on 30.10.17.
 */

public class CustomProgressView extends View {
    public CustomProgressView(Context context) {
        super(context);
        inti();
    }

    public CustomProgressView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        inti();
    }

    public CustomProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inti();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CustomProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        inti();
    }

    private Paint paintOne, paintTwo, paintTree;
    int width, height, radius;
    ValueAnimator animator;

    private void inti() {
        paintOne = new Paint();
        paintTwo = new Paint();
        paintTree = new Paint();
        paintOne.setColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        paintTwo.setColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        paintTree.setColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        paintTwo.setAntiAlias(true);
        paintTree.setAntiAlias(true);
        paintOne.setAntiAlias(true);


    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int desiredWidth = 150;
        int desiredHeight = 50;

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);


        //Measure Width
        if (widthMode == MeasureSpec.EXACTLY) {
            //Must be this size
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            //Can't be bigger than...
            width = Math.min(desiredWidth, widthSize);
        } else {
            //Be whatever you want
            width = desiredWidth;
        }

        //Measure Height
        if (heightMode == MeasureSpec.EXACTLY) {
            //Must be this size
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            //Can't be bigger than...
            height = Math.min(desiredHeight, heightSize);
        } else {
            //Be whatever you want
            height = desiredHeight;
        }

        radius = Math.min(width, height) / 4;
        if (radius * 9 > width) {
            radius = (int) (radius / 1.1f);
        }
        //MUST CALL THIS
        setMeasuredDimension(width, height);
        setAnimation();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        Log.d("Sergey", "width " + w + " height " + h);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(width / 2, height / 2, radius, paintOne);
        canvas.drawCircle(width / 2 + 3 * radius, height / 2, radius, paintTwo);
        canvas.drawCircle(width / 2 - 3 * radius, height / 2, radius, paintTree);
    }

    public void startAnimation() {
        animator.start();
    }


    private void setAnimation() {
        animator = ValueAnimator.ofInt(radius, 0);
        animator.setDuration(1000);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.addUpdateListener(animation -> {
            int newRadius = (int) animation.getAnimatedValue();
            if (radius != newRadius) {
                radius = newRadius;
                invalidate();
            }
        });
    }


}
