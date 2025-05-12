package com.example.spendpal;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import java.util.List;

public class CustomPieChartView extends View {

    private List<Float> values;
    private List<Integer> colors;
    private List<String> titles;
    private Paint paint;
    private RectF rectF;

    public CustomPieChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        rectF = new RectF();
    }

    public void setData(List<Float> values, List<Integer> colors, List<String> titles) {
        this.values = values;
        this.colors = colors;
        this.titles = titles;
        invalidate(); // Redraw the chart
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (values == null || colors == null || titles == null || values.isEmpty()) return;

        float total = 0;
        for (float val : values) total += val;

        int width = getWidth();
        int height = getHeight();
        int minDimen = Math.min(width, height);
        float padding = 40;
        rectF.set(padding, padding, minDimen - padding, minDimen - padding);

        float startAngle = 0f;

        // Draw pie slices with titles and percentages
        for (int i = 0; i < values.size(); i++) {
            float sweepAngle = (values.get(i) / total) * 360f;
            paint.setColor(colors.get(i));
            canvas.drawArc(rectF, startAngle, sweepAngle, true, paint);

            // Only draw text if slice is big enough
            if (sweepAngle > 18) {
                float angle = startAngle + sweepAngle / 2;
                float x = (float) (width / 2 + Math.cos(Math.toRadians(angle)) * (minDimen / 4));
                float y = (float) (height / 2 + Math.sin(Math.toRadians(angle)) * (minDimen / 4));

                String percentage = String.format("%.1f%%", (values.get(i) / total) * 100);
                String displayText = titles.get(i) + " (" + percentage + ")";

                paint.setColor(Color.WHITE);
                paint.setTextSize(24);
                paint.setTextAlign(Paint.Align.CENTER);
                canvas.drawText(displayText, x, y, paint);
            }

            startAngle += sweepAngle;
        }

        // Draw legend below the pie chart
        float legendStartY = minDimen + 50;
        float legendStartX = padding;
        float legendSpacing = 60;

        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTextSize(32);

        for (int i = 0; i < titles.size(); i++) {
            // Color box
            paint.setColor(colors.get(i));
            canvas.drawRect(legendStartX, legendStartY + i * legendSpacing,
                    legendStartX + 40, legendStartY + i * legendSpacing + 40, paint);

            // Text
            paint.setColor(Color.BLACK);
            canvas.drawText(titles.get(i), legendStartX + 60, legendStartY + i * legendSpacing + 30, paint);
        }
    }
}
