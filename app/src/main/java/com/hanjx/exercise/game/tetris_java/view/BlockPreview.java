package com.hanjx.exercise.game.tetris_java.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.SizeUtils;
import com.hanjx.exercise.game.tetris.logic.Block;
import com.hanjx.exercise.game.tetris.logic.Offset;
import com.hanjx.exercise.game.tetris_java.BlockUtils;
import com.hanjx.exercise.game.tetris_java.Const;

public class BlockPreview extends View {
    private Block block;
    private int pointWidth = SizeUtils.dp2px(10);
    private Paint displayPaint = new Paint();
    private int displayedColor = 0xFF000000;

    public void setBlock(Block block) {
        this.block = block;
        requestLayout();
    }

    public BlockPreview(Context context) {
        super(context);
    }

    public BlockPreview(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public BlockPreview(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = BlockUtils.getOffsetsBottom(block.getSummaryOffset()).size() * pointWidth;
        setMeasuredDimension(width + getPaddingLeft() + getPaddingRight(),
                Const.BLOCK_POINT_COUNT * pointWidth + getPaddingTop() + getPaddingBottom());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int left = getPaddingLeft();
        int top = getPaddingTop();
        for (Offset offset : block.getSummaryOffset()) {
            drawPoint(canvas, left + offset.getX() * pointWidth, top + offset.getY() * pointWidth);
        }
    }

    private void drawPoint(Canvas canvas, int left, int top) {
        displayPaint.setColor(displayedColor);
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
