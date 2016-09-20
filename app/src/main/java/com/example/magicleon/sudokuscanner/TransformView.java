package com.example.magicleon.sudokuscanner;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by magicleon on 13/09/16.
 */
public class TransformView extends View {
    Bitmap myBM, scaled;
    ArrayList<myHandle> handles = new ArrayList<myHandle>();

    boolean scaledSet = false;
    boolean showingResult = false;

    int handleTouchedIndex = -1;

    Rect mMeasuredRect = new Rect();
    int screenWidth = getMeasuredWidth();
    int screenHeight = getMeasuredHeight();

    Paint polypaint = new Paint();

    public TransformView(Context context) {
        super(context);
        initHandles();
    }

    public TransformView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initHandles();
    }

    public TransformView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initHandles();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public TransformView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initHandles();
    }

    private void initHandles(){
        for (int i=0;i<4;i++){
            handles.add(new myHandle(i * screenWidth/4,i * screenHeight/4));
        }
        polypaint.setColor(Color.GREEN);
        polypaint.setStyle(Paint.Style.STROKE);
        polypaint.setStrokeWidth(3);
    }
    public void setBitmap(Bitmap bm){
        myBM = bm;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean handled = false;
//        Log.d("AA",event.toString());
        if(showingResult){
            return super.onTouchEvent(event);
        }
        switch (event.getActionMasked()){
            case MotionEvent.ACTION_DOWN:
                handleTouchedIndex = getTouchedHandle(event.getX(),event.getY());
                invalidate();
                handled = true;
                break;

            case MotionEvent.ACTION_MOVE:
                if(handleTouchedIndex >=0){
                    handles.get(handleTouchedIndex).setX(clampX(event.getX()));
                    handles.get(handleTouchedIndex).setY(clampY(event.getY()));
                    invalidate();
                }
                handled = true;
                break;
            case MotionEvent.ACTION_UP:
                sortClockwise(handles);
                Log.d("AA","Sorted");
                invalidate();
                handled = true;
                break;
        }

        return super.onTouchEvent(event) || handled ;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(scaledSet && !showingResult) {
            Path poly = new Path();

            canvas.drawBitmap(scaled, null, mMeasuredRect, null);


            poly.moveTo(handles.get(0).getX(),handles.get(0).getY());

            handles.get(0).draw(canvas,0);
            for (int i = 1;i<4; i++){
                handles.get(i).draw(canvas,i);
                poly.lineTo(handles.get(i).getX(),handles.get(i).getY());
                poly.moveTo(handles.get(i).getX(),handles.get(i).getY());
            }
            poly.lineTo(handles.get(0).getX(),handles.get(0).getY());
            poly.close();
            canvas.drawPath(poly,polypaint);

        }else {
            if (scaledSet && showingResult) {
                canvas.drawBitmap(scaled,0,0,null);
            }
        }

    }
    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        scaledSet = false;
        mMeasuredRect.set(0, 0, getMeasuredWidth(), getMeasuredHeight());
        screenWidth = MeasureSpec.getSize(widthMeasureSpec);
        screenHeight = MeasureSpec.getSize(heightMeasureSpec);
        scaled = getScaled(myBM);
        scaledSet = true;
    }


    private Bitmap getScaled(Bitmap bm){
        int nh = (int) ( bm.getHeight() * ((float)(screenWidth) / bm.getWidth()) );
        return Bitmap.createScaledBitmap(bm,screenWidth,nh,true);
    }

    private float clampX(float x){
        if (x<myHandle.RADIUS){
            return myHandle.RADIUS;
        }else if (x>screenWidth-myHandle.RADIUS){
            return screenWidth-myHandle.RADIUS;
        }

        return x;
    }
    private float clampY(float y){
        if (y<myHandle.RADIUS){
            return myHandle.RADIUS;
        }else if (y>screenHeight-myHandle.RADIUS){
            return screenHeight-myHandle.RADIUS;
        }

        return y;
    }
    private int getTouchedHandle(float x, float y){
        for (int i=0; i<4;i++){
            if(handles.get(i).nearMe(x,y)){
                return i;
            }
        }
        return -1;
    }

    private void sortClockwise(ArrayList<myHandle> balls){
        double minDistance = Double.POSITIVE_INFINITY;
        int minIndex = 0;
        double currentDistance = 0;

        for (int i=0; i<4;i++){
            currentDistance = (Math.pow(balls.get(i).getX(),2) + Math.pow(balls.get(i).getY(),2));
            if (currentDistance<minDistance){
                minDistance = currentDistance;
                minIndex = i;
            }
        }
        Collections.swap(balls,0,minIndex);

        minIndex = maxCoordinate(balls,balls.get(0).getX(),0);

        Collections.swap(balls,1,minIndex);

        minIndex = maxCoordinate(balls,balls.get(1).getY(),1);

        Collections.swap(balls,2,minIndex);

    }

    private int maxCoordinate(ArrayList<myHandle> balls, float threshold, int coordinate){
        int neededIndex = 0;
        float neededCoordinate = 0;
        float currentCoordinate = 0;
        //coordinate = 0 ---> considera i punti a destra della soglia e trova quello con l'ordinata maggiore
        //coordinate = 1 ---> considera i punti al di sotto della soglia e trova quello di ascissa maggiore
        switch (coordinate){
            case 0:
                neededCoordinate = Float.POSITIVE_INFINITY;
                for (int i=0; i<4; i++){
                    if (balls.get(i).getX()>threshold){
                        currentCoordinate = balls.get(i).getY();
                        if (currentCoordinate<neededCoordinate){
                            neededCoordinate = currentCoordinate;
                            neededIndex = i;
                        }
                    }
                }
                break;
            case 1:
                for (int i=0; i<4; i++){
                    if (balls.get(i).getY()>threshold){
                        currentCoordinate = balls.get(i).getX();
                        if (currentCoordinate>neededCoordinate){
                            neededCoordinate = currentCoordinate;
                            neededIndex = i;
                        }
                    }
                }
                break;
        }
        return  neededIndex;
    }

    public ArrayList<myHandle> getHandles() {
        return handles;
    }

    public void showResult(){
        Transformer transformer = new Transformer();
        transformer.loadOCV();

        Log.d("AA","Starting Transformation");
        Log.d("AA","screen size: " + screenWidth + " x " + screenHeight );
        scaled = getScaled(transformer.addrizzone(scaled,handles,screenWidth,screenHeight));

        Log.d("AA","Done, scaled sizes: " + scaled.getHeight() + " x " + scaled.getWidth());

        showingResult = true;

        invalidate();
    }
}
