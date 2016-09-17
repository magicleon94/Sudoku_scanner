package com.example.magicleon.sudokuscanner;

import android.graphics.Bitmap;
import android.util.Log;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
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

    public Bitmap addrizzone(Bitmap image, ArrayList<myHandle> sourcePoints){
        // sourcePoints are  expected  to be clockwise ordered
        // getting the size of the output image
        double dst_width = Math.max(sourcePoints.get(0).distanceFrom(sourcePoints.get(1)),sourcePoints.get(3).distanceFrom(sourcePoints.get(2)));
        double dst_height = Math.max(sourcePoints.get(0).distanceFrom(sourcePoints.get(3)),sourcePoints.get(1).distanceFrom(sourcePoints.get(2)));

        //determining point sets to get the transformation matrix
        List<org.opencv.core.Point> srcPts = new ArrayList<org.opencv.core.Point>();
        for (myHandle ball : sourcePoints) {
            srcPts.add(new org.opencv.core.Point((ball.getX()),ball.getY()));
        }

        List<org.opencv.core.Point> dstPoints= new ArrayList<org.opencv.core.Point>();
        dstPoints.add(new org.opencv.core.Point(0,0));
        dstPoints.add(new org.opencv.core.Point(dst_width-1,0));
        dstPoints.add(new org.opencv.core.Point(dst_width-1,dst_height-1));
        dstPoints.add(new org.opencv.core.Point(0,dst_height));

        Mat srcMat = Converters.vector_Point2f_to_Mat(srcPts);
        Mat dstMat = Converters.vector_Point2f_to_Mat(dstPoints);

        //getting the transformation matrix
        Mat perspectiveTransformation = Imgproc.getPerspectiveTransform(srcMat,dstMat);

        //getting the input matrix from the given bitmap
        Mat inputMat = new Mat(image.getHeight(),image.getWidth(),CvType.CV_8UC1);

        Utils.bitmapToMat(image,inputMat);

        Imgproc.cvtColor(inputMat,inputMat,Imgproc.COLOR_RGB2GRAY);

        //getting the output matrix with the previously determined sizes
        Mat outputMat = new Mat((int) dst_height,(int) dst_width,CvType.CV_8UC1);

        //applying the transformation
        Imgproc.warpPerspective(inputMat,outputMat,perspectiveTransformation,new Size(dst_width,dst_height));

        //creating the output bitmap
        Bitmap outputBitmap = Bitmap.createBitmap((int)dst_width,(int)dst_height, Bitmap.Config.RGB_565);

        //Mat to Bitmap
        Imgproc.cvtColor(outputMat,outputMat,Imgproc.COLOR_GRAY2RGB);
        Utils.matToBitmap(outputMat,outputBitmap);

        return outputBitmap;
    }


}
