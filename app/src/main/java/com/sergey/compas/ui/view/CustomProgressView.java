package com.sergey.compas.ui.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

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

    private Paint paint;
    int x,y, radius;

    private void inti(){
        paint = new Paint();
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
            radius = h/4;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.setColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        canvas.drawCircle(getPivotX(), getPivotY(), radius, paint);
    }
}
