package com.example.magicleon.sudokuscanner;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by magicleon on 23/09/16.
 */

public class SudokuView extends View {
    Paint mPaint;
    int size;
    ArrayList<Integer> numbers = null;

    public SudokuView(Context context) {
        super(context);
        mPaint = new Paint();
    }
    public SudokuView(Context context,ArrayList<Integer> numbers) {
        super(context);
        mPaint = new Paint();
        this.numbers = numbers;
    }

    public SudokuView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint();
    }

    public SudokuView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint = new Paint();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public SudokuView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mPaint = new Paint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawGrid(canvas);
        if(numbers!=null){
            drawNumbers(canvas,numbers);
        }

    }
    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int screenWidth = MeasureSpec.getSize(widthMeasureSpec);
        int screenHeight = MeasureSpec.getSize(heightMeasureSpec);
        size = Math.min(screenWidth,screenHeight);
    }
    private void drawGrid(Canvas canvas){
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(10);
        canvas.drawRect(0,0,size-1,size-1,mPaint);
        for (int i=1; i<=9;i++){
            mPaint.setStrokeWidth(i%3==0 ? 5 : 3);
            canvas.drawLine(size/9*i,0,size/9*i,size,mPaint);
            canvas.drawLine(0,size/9*i,size,size/9*i,mPaint);
        }
    }

    private void drawNumbers(Canvas canvas, ArrayList<Integer> numbers){
        int step = size/9;
        int x,y;
        int margin = size/32;

        mPaint.setTextSize(100);
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        int number;
        for(int i=0;i<9;i++){
            y = (i+1)*step-size/32;
            for(int j=0; j<9; j++) {
                x = j * step + margin;

                number = numbers.get(i*9+j);
                if (number > 0) {
                    canvas.drawText(String.valueOf(number), x, y, mPaint);
                }

            }
        }
    }
}
