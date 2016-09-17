package com.example.magicleon.sudokuscanner;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

/**
 * Created by magicleon on 13/09/16.
 */
public class myHandle {
    float x;
    float y;
    public static int RADIUS = 60;
    public static int  STROKE_CIRCLE_WIDTH = 4;
    public static int  STROKE_CROSS_WIDTH = 3;
    public static int CROSS_LENGTH = 16;
    private static Paint mPaint;

    public myHandle() {
        mPaint = new Paint();
    }

    public myHandle(float x, float y){
        mPaint = new Paint();
        this.x = x;
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void draw(Canvas  canvas){
        mPaint.setColor(Color.RED);
        mPaint.setStrokeWidth(STROKE_CIRCLE_WIDTH);
        mPaint.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(x,y,RADIUS,mPaint);

        mPaint.setColor(Color.CYAN);
        mPaint.setStrokeWidth(STROKE_CROSS_WIDTH);

        canvas.drawLine(x-CROSS_LENGTH/2,y,x+CROSS_LENGTH/2,y,mPaint);
        canvas.drawLine(x,y-CROSS_LENGTH/2,x,y+CROSS_LENGTH/2,mPaint);
    }

    public void draw(Canvas  canvas,int index){
        this.draw(canvas);
        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setTextSize(50);
        canvas.drawText(Integer.toString(index),x,y,mPaint);
        canvas.drawText("x = " + Float.toString(x) + " y = " + Float.toString(y),x,y+70,mPaint);
    }

    public boolean nearMe(float x, float y){
        return (Math.sqrt(Math.pow(this.x-x,2) +Math.pow(this.y-y,2))) < RADIUS ;
    }

    public double distanceFrom(myHandle point){
//        Log.d("AA","Distance is " + (int)(Math.sqrt(Math.pow(this.x-point.x,2) +Math.pow(this.y-point.y,2))));
        return (Math.sqrt(Math.pow(this.x-point.x,2) +Math.pow(this.y-point.y,2)));
    }

}
