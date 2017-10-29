package com.sergey.compas.ui.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.content.res.AppCompatResources;
import android.util.AttributeSet;
import android.view.View;

import com.sergey.compas.R;

/**
 * Created by sergey on 29.10.17.
 */

public class CustomCompasView extends View {
    public CustomCompasView(Context context) {
        super(context);
    }

    public CustomCompasView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomCompasView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CustomCompasView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private final static float MAX_CHANGE_STEP = 2.0f;
    private final static float CHANGE_STEP = 0.05f;
    private final static float MIN_DIFF_TO_REDRAW = 1.5F;
    private final static float DIRECTION_ARROW_DISTANCE_TO_EDGE = 0.41f;

    private Bitmap background;
    private Bitmap arrowCompass;
    private Bitmap arrowTarget;

    private boolean isShouldDrawTargetArrow;

    private int minWidthHeight;

    private float arrowCompassAngle;
    private float arrowCompassAngleTarget;

    private float directionArrowAngle;
    private float directionArrowAngleTarget;


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        resizeBitmap(h, w);
        minWidthHeight = Math.min(w, h);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        calculateAngles();
        drawBackground(canvas);
        drawCompassArrow(canvas);
        drawDirectionArrow(canvas);
        redraw();
    }

    public void setAzimuth(float angle) {
        this.arrowCompassAngleTarget = angle;

        redraw();
    }

    public void setBearing(float angle) {
        isShouldDrawTargetArrow = true;
        this.directionArrowAngleTarget = angle;
        redraw();
    }


    private void drawCompassArrow(Canvas canvas) {
        canvas.save(Canvas.ALL_SAVE_FLAG);
        canvas.rotate(arrowCompassAngle, minWidthHeight / 2, minWidthHeight / 2);
        canvas.translate(minWidthHeight / 2 - arrowCompass.getWidth() / 2, minWidthHeight / 2 - arrowCompass.getHeight() / 2);
        canvas.drawBitmap(arrowCompass, 0, 0, null);
        canvas.restore();
    }

    private void drawBackground(Canvas canvas) {
        canvas.drawBitmap(background, 0, 0, null);
    }

    private void calculateAngles() {
        arrowCompassAngle += calculateStepWithAnimation(arrowCompassAngle, arrowCompassAngleTarget);
        directionArrowAngle += calculateStepWithAnimation(directionArrowAngle, arrowCompassAngle + directionArrowAngleTarget);
    }

    private void drawDirectionArrow(Canvas canvas) {
        if (isShouldDrawTargetArrow) {
            float x = (float) (Math.cos(directionArrowAngle * Math.PI / 180 - Math.PI / 2) * minWidthHeight * DIRECTION_ARROW_DISTANCE_TO_EDGE + minWidthHeight / 2);
            float y = (float) (Math.sin(directionArrowAngle * Math.PI / 180 - Math.PI / 2) * minWidthHeight * DIRECTION_ARROW_DISTANCE_TO_EDGE + minWidthHeight / 2);
            canvas.save(Canvas.ALL_SAVE_FLAG);
            canvas.translate(x, y);
            canvas.rotate(directionArrowAngle, 0, 0);
            canvas.drawBitmap(arrowTarget, -1 * arrowTarget.getWidth() / 2, -1 * arrowTarget.getHeight() / 2, null);
            canvas.restore();
        }
    }

    private void redraw() {
        if (isShouldRedraw())
            invalidate();
    }

    private boolean isShouldRedraw() {
        boolean arrowCompassRedraw = Math.abs(arrowCompassAngle - arrowCompassAngleTarget) > MIN_DIFF_TO_REDRAW;
        boolean arrowDirectionRedraw = Math.abs(directionArrowAngle - directionArrowAngleTarget) > MIN_DIFF_TO_REDRAW;
        return arrowCompassRedraw || arrowDirectionRedraw;
    }

    private float calculateStepWithAnimation(float current, float target) {
        float clockwiseDistance = (target - current + 360) % 360;
        float minDistance = Math.min(clockwiseDistance, 360 - clockwiseDistance);
        float step = Math.min(MAX_CHANGE_STEP, minDistance * CHANGE_STEP);
        return clockwiseDistance < 180 ? step : -1 * step;
    }

    public Bitmap getBitmapFromVectorDrawable(Context context, int drawableId) {
        Drawable drawable = AppCompatResources.getDrawable(context, drawableId);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP && drawable != null) {
            drawable = (DrawableCompat.wrap(drawable)).mutate();
        }
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    private void resizeBitmap(int parentHeight, int parentWidth) {
        Resources resources = getResources();
        background = getResizedBitmap(BitmapFactory.decodeResource(resources, R.drawable.background), parentWidth, parentHeight);
        arrowCompass = getResizedBitmap(BitmapFactory.decodeResource(resources, R.drawable.arrow), parentWidth / 13, (int) (parentHeight / 1.1f));
        arrowTarget = getResizedBitmap(getBitmapFromVectorDrawable(getContext(), R.drawable.ic_arrows), parentWidth / 11, parentHeight / 11);
    }


    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }
}
