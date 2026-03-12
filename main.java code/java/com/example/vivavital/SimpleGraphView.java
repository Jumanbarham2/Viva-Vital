package com.example.vivavital;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SimpleGraphView extends View {
    private Paint paint = new Paint();
    private List<Float> values = new ArrayList<>();
    private float maxValue = 0;

    public SimpleGraphView(Context context) {
        super(context);
        init();
    }

    public SimpleGraphView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint.setAntiAlias(true);
        paint.setStrokeWidth(4f);
    }

    public void setData(List<Float> dataPoints) {
        this.values = dataPoints;
        this.maxValue = Collections.max(dataPoints);
        invalidate(); // Redraw view
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (values.isEmpty()) return;

        int width = getWidth();
        int height = getHeight();
        float padding = 50f;
        float graphHeight = height - 2*padding;
        float graphWidth = width - 2*padding;
        float xStep = graphWidth / (values.size() - 1);

        // Draw axes
        paint.setColor(Color.BLACK);
        canvas.drawLine(padding, height-padding, width-padding, height-padding, paint); // X-axis
        canvas.drawLine(padding, padding, padding, height-padding, paint); // Y-axis

        // Draw graph line
        paint.setColor(Color.BLUE);
        Path path = new Path();
        path.moveTo(padding, height-padding - (values.get(0)/maxValue*graphHeight));

        for (int i = 1; i < values.size(); i++) {
            float x = padding + (i * xStep);
            float y = height-padding - (values.get(i)/maxValue*graphHeight);
            path.lineTo(x, y);
        }
        canvas.drawPath(path, paint);

        // Draw data points
        paint.setColor(Color.RED);
        for (int i = 0; i < values.size(); i++) {
            float x = padding + (i * xStep);
            float y = height-padding - (values.get(i)/maxValue*graphHeight);
            canvas.drawCircle(x, y, 8f, paint);
        }
    }
}