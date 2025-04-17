package com.example.androidproject;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;

public class DoughnutView extends View {
    final int COLOR_OPTION_DEFAULT = 0;
    final int COLOR_OPTION_PLIABILITY = 1;
    private Context context;
    private Paint progressPaint;
    private Paint backgroundPaint;
    private int progressColor;
    private int progress;
    private int colorOption;

    public DoughnutView(Context context) {
        super(context);
        this.context = context;
        init(null);
    }

    public DoughnutView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init(attrs);
    }

    public DoughnutView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        init(attrs);
    }

    private void init(AttributeSet attrs){
        if (attrs != null) {
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.DoughnutView);

            try {
                progressColor = array.getColor(R.styleable.DoughnutView_customColorValue, Color.BLUE);
                colorOption = array.getInteger(R.styleable.DoughnutView_customColorOption, COLOR_OPTION_DEFAULT);
            } finally {
                array.recycle();
            }
        }

        progressPaint = new Paint();
        progressPaint.setAntiAlias(true);
        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setColor(progressColor);
        progressPaint.setStrokeWidth(20);

        backgroundPaint = new Paint();
        backgroundPaint.setStyle(Paint.Style.STROKE);
        backgroundPaint.setColor(Color.LTGRAY);
        backgroundPaint.setStrokeWidth(20);
    }

    public void setProgress(int value) {
        progress = value;
        if (colorOption == COLOR_OPTION_PLIABILITY) {
            if (progress >= 70) progressColor = Color.parseColor("#009000");
            else if (progress >= 40) progressColor = Color.parseColor("#ff7f00");
            else progressColor = Color.RED;

            progressPaint.setColor(progressColor);
        }
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);

        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width = 0;
        int height = 0;

        if (widthMode == MeasureSpec.AT_MOST) {
            width = 130;
        } else if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        }

        if (heightMode == MeasureSpec.AT_MOST) {
            height = 130;
        } else if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        }

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        float radius = getWidth() / 2f - progressPaint.getStrokeWidth();
        float centerX = getWidth() / 2f;
        float centerY = getHeight() / 2f;

        // 배경 원 그리기
        canvas.drawCircle(centerX, centerY, radius, backgroundPaint);

        // 원호(Arc)를 그리기 위한 사각형 범위 설정
        RectF rect = new RectF(
                centerX - radius,
                centerY - radius,
                centerX + radius,
                centerY + radius
        );

        // 현재 진행도에 따른 각도 계산
        float sweepAngle = 360f * progress / 100f;

        // 진행도 원호 그리기 (-90도부터 시작해서 시계방향)
        canvas.drawArc(rect, -90f, sweepAngle, false, progressPaint);
    }
}
