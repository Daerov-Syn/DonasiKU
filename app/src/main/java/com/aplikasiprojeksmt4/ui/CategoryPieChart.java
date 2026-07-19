package com.aplikasiprojeksmt4.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import com.aplikasiprojeksmt4.R;

public class CategoryPieChart extends View {

    private Paint purplePaint;
    private Paint orangePaint;
    private Paint whitePaint;
    private RectF rectF;
    private float barangPercentage = 66f;
    private float uangPercentage = 34f;

    public CategoryPieChart(Context context) {
        super(context);
        init();
    }

    public CategoryPieChart(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        purplePaint = new Paint();
        purplePaint.setAntiAlias(true);
        purplePaint.setStyle(Paint.Style.FILL);
        purplePaint.setColor(ContextCompat.getColor(getContext(), R.color.primary_purple));

        orangePaint = new Paint();
        orangePaint.setAntiAlias(true);
        orangePaint.setStyle(Paint.Style.FILL);
        orangePaint.setColor(0xFFFF9800); // Orange color

        whitePaint = new Paint();
        whitePaint.setAntiAlias(true);
        whitePaint.setStyle(Paint.Style.FILL);
        whitePaint.setColor(0xFFFFFFFF); // White for donut hole

        rectF = new RectF();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float width = getWidth();
        float height = getHeight();
        float size = Math.min(width, height);
        float padding = 4f;

        rectF.set(padding, padding, size - padding, size - padding);

        // Draw Orange part (Donasi Uang) - starting from -90 degrees (top)
        float uangAngle = (uangPercentage / 100f) * 360f;
        canvas.drawArc(rectF, -90, uangAngle, true, orangePaint);

        // Draw Purple part (Donasi Barang)
        float barangAngle = (barangPercentage / 100f) * 360f;
        canvas.drawArc(rectF, -90 + uangAngle, barangAngle, true, purplePaint);
        
        // Donut hole
        canvas.drawCircle(width / 2f, height / 2f, size / 3.5f, whitePaint);
    }
}
