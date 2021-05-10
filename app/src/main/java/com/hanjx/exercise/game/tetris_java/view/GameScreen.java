package com.hanjx.exercise.game.tetris_java.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.SizeUtils;
import com.hanjx.exercise.game.tetris_java.Const;

import java.util.List;

public class GameScreen extends View {
    private Paint displayPaint = new Paint();

    private int columnCount = Const.COLUMN_COUNT;
    private int rowCount = Const.ROW_COUNT;
    private int pointWidth = SizeUtils.dp2px(20);
    private int normalColor = 0x50000000;
    private int displayedColor = 0xFF000000;

    private List<Boolean> displayStatus;

    public GameScreen(Context context) {
        super(context);
    }

    public GameScreen(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public GameScreen(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setDisplayStatus(List<Boolean> displayStatus) {
        this.displayStatus = displayStatus;
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = columnCount * pointWidth + getPaddingLeft() + getPaddingRight();
        int height = rowCount * pointWidth + getPaddingTop() + getPaddingBottom();
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int left = getPaddingLeft();
        int top = getPaddingTop();
        for (int i = 0; i < displayStatus.size(); i++) {
            drawPoint(canvas, left + i % columnCount * pointWidth,
                    top + i / columnCount * pointWidth, displayStatus.get(i));
        }
    }

    private void drawPoint(Canvas canvas, int left, int top, boolean displayed) {
        displayPaint.setColor(displayed ? displayedColor : normalColor);
        displayPaint.setStyle(Paint.Style.FILL);
        displayPaint.setStrokeWidth(0);
        canvas.drawRect(left + pointWidth * 0.25f, top + pointWidth * 0.25f,
                left + pointWidth * 0.75f, top + pointWidth * 0.75f, displayPaint);
        displayPaint.setStyle(Paint.Style.STROKE);
        displayPaint.setStrokeWidth(pointWidth * 0.1f);
        canvas.drawRect(left + pointWidth * 0.1f, top + pointWidth * 0.1f,
                left + pointWidth * 0.9f, top + pointWidth * 0.9f, displayPaint);
    }
}
