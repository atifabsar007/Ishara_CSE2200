package com.kuet.ishara.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/** Small custom View: animated-look confidence bars, accessible text description. */
public class GestureStatusView extends View {
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private float confidence;
    public GestureStatusView(Context context, AttributeSet attrs) { super(context, attrs); }
    public void setConfidence(float value) {
        confidence = Math.max(0f, Math.min(1f, value));
        setContentDescription("Gesture confidence " + Math.round(confidence * 100) + " percent");
        invalidate();
    }
    @Override protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int bars = 10; float gap = 5f * getResources().getDisplayMetrics().density;
        float width = (getWidth() - gap * (bars-1)) / bars;
        float filled = confidence * bars;
        for (int i = 0; i < bars; i++) {
            paint.setColor(i < filled ? 0xFF006D63 : 0xFFD4E8E3);
            canvas.drawRoundRect(i * (width+gap), 4f, i*(width+gap)+width, getHeight()-4f, 8f, 8f, paint);
        }
    }
}
