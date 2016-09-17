package com.example.magicleon.sudokuscanner;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

/**
 * Created by magicleon on 11/09/16.
 */
public class TransformFragment extends Fragment {
    ImageView imageView ;
    Bitmap scaled;
    int x;
    int y;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scaled = getScaled(getActivity().getIntent().getStringExtra("photoPath"));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        View view = inflater.inflate(R.layout.transform_fragment_layout,container,false);
        imageView = (ImageView) view.findViewById(R.id.transfrormImageView);
        Bitmap myCopy = scaled;
        drawMyCircle(new Canvas(myCopy));
        imageView.setImageBitmap(myCopy);
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if  (motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    x = (int) motionEvent.getX();
                    y = (int) motionEvent.getY();
                }
                return true;
            }
        });
        return view;
    }



    private Bitmap getScaled(String path){
        System.loadLibrary("opencv_java3");

        Mat test = Imgcodecs.imread(path);
        Imgproc.cvtColor(test,test,Imgproc.COLOR_RGB2BGR);

        Bitmap bm = Bitmap.createBitmap(test.cols(),test.rows(),Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(test,bm);

        android.graphics.Point size = new android.graphics.Point();
        getActivity().getWindowManager().getDefaultDisplay().getSize(size);
        int nh = (int) ( bm.getHeight() * ((float)(size.x) / bm.getWidth()) );

        Bitmap result =  Bitmap.createScaledBitmap(bm,size.x,nh,true);

        return result;

    }

    private void drawMyCircle(Canvas canvas){
        Paint mPaint = new Paint();
        mPaint.setColor(Color.BLUE);
        mPaint.setStrokeWidth(40);
        mPaint.setStyle(Paint.Style.FILL);

        canvas.drawCircle(x,y,50,mPaint);
    }
}


