package com.example.magicleon.sudokuscanner;

import android.graphics.Bitmap;
import android.util.Log;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.utils.Converters;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by magicleon on 16/09/16.
 */
public class Transformer {
    public Transformer() {
    }

    public void loadOCV(){
        System.loadLibrary("opencv_java3");
    }

    public Bitmap addrizzone(Bitmap image, ArrayList<myHandle> sourcePoints, int screenWidth, int screenHeight){
        // sourcePoints are  expected  to be clockwise ordered
        // getting the size of the output image

        //determining point sets to get the transformation matrix
        List<Point> srcPts = new ArrayList<Point>();
        for (myHandle ball : sourcePoints) {

            srcPts.add(new Point((ball.getX()*image.getWidth() / screenWidth),ball.getY()*image.getHeight()/screenHeight));
        }

        double dst_width = Math.max(distance(srcPts.get(0),srcPts.get(1)),
                distance(srcPts.get(2),srcPts.get(3)));

        double dst_height = Math.max(distance(srcPts.get(0),srcPts.get(3)),
                distance(srcPts.get(1),srcPts.get(2)));

        if (dst_width<dst_height){
            Log.d("AA","destination width is lesser than height, scaling height");
            Log.d("AA","Before: " + dst_height);

            dst_height = dst_width * image.getHeight()/ image.getWidth();

            Log.d("AA","After: " + dst_width);
        }else{
            Log.d("AA","destination width is greater than height, scaling width");
            Log.d("AA","Before: " + dst_width);

            dst_width = dst_height * image.getWidth() / image.getHeight();

            Log.d("AA","After: " + dst_width);
        }

        List<Point> dstPoints= new ArrayList<Point>();
        dstPoints.add(new Point(0,0));
        dstPoints.add(new Point(dst_width,0));
        dstPoints.add(new Point(dst_width,dst_height));
        dstPoints.add(new Point(0,dst_height));

        Mat srcMat = Converters.vector_Point2f_to_Mat(srcPts);
        Mat dstMat = Converters.vector_Point2f_to_Mat(dstPoints);

        //getting the transformation matrix
        Mat perspectiveTransformation = Imgproc.getPerspectiveTransform(srcMat,dstMat);
        Core.normalize(perspectiveTransformation,perspectiveTransformation);


        //getting the input matrix from the given bitmap
        Mat inputMat = new Mat(image.getHeight(),image.getWidth(),CvType.CV_8UC3);

        Utils.bitmapToMat(image,inputMat);

        Imgproc.cvtColor(inputMat,inputMat,Imgproc.COLOR_RGB2GRAY);

        //getting the output matrix with the previously determined sizes
        Mat outputMat = new Mat((int) dst_height,(int) dst_width,CvType.CV_8UC1);

        //applying the transformation
        Imgproc.warpPerspective(inputMat,outputMat,perspectiveTransformation,new Size(dst_width,dst_height));

//        Imgproc.GaussianBlur(outputMat,outputMat,new Size(0,0),10);
//        Imgproc.adaptiveThreshold(outputMat,outputMat,255,Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C,Imgproc.THRESH_BINARY,15,40);
        //creating the output bitmap

        Bitmap outputBitmap = Bitmap.createBitmap((int)dst_width,(int)dst_height, Bitmap.Config.RGB_565);

        //Mat to B
        Imgproc.cvtColor(outputMat,outputMat,Imgproc.COLOR_GRAY2RGB);
        Utils.matToBitmap(outputMat,outputBitmap);

        return outputBitmap;
    }

    private double distance(Point p1, Point p2){
        return Math.sqrt((p1.x-p2.x)*(p1.x-p2.x) + (p1.y-p2.y)*(p1.y-p2.y));
    }
}
