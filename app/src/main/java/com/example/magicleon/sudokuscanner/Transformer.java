package com.example.magicleon.sudokuscanner;

import android.graphics.Bitmap;
import android.util.Log;

import com.googlecode.tesseract.android.TessBaseAPI;

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

    public ArrayList<Integer>  addrizzone(Bitmap image, ArrayList<myHandle> sourcePoints, int screenWidth, int screenHeight){
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

        List<Point> dstPoints= new ArrayList<Point>();
        dstPoints.add(new Point(0,0));
        dstPoints.add(new Point(dst_width,0));
        dstPoints.add(new Point(dst_width,dst_height));
        dstPoints.add(new Point(0,dst_height));

        Mat srcMat = Converters.vector_Point2f_to_Mat(srcPts);
        Mat dstMat = Converters.vector_Point2f_to_Mat(dstPoints);

        //getting the transformation matrix
        Mat perspectiveTransformation = Imgproc.getPerspectiveTransform(srcMat,dstMat);
//        Core.normalize(perspectiveTransformation,perspectiveTransformation);


        //getting the input matrix from the given bitmap
        Mat inputMat = new Mat(image.getHeight(),image.getWidth(),CvType.CV_8UC3);

        Utils.bitmapToMat(image,inputMat);

        Imgproc.cvtColor(inputMat,inputMat,Imgproc.COLOR_RGB2GRAY);

        //getting the output matrix with the previously determined sizes
        Mat outputMat = new Mat((int) dst_height,(int) dst_width,CvType.CV_8UC1);

        //applying the transformation
        Imgproc.warpPerspective(inputMat,outputMat,perspectiveTransformation,new Size(dst_width,dst_height));

//        Imgproc.threshold(outputMat,outputMat,150,255,Imgproc.THRESH_OTSU);
        //creating the output bitmap

        Bitmap outputBitmap = Bitmap.createBitmap((int)dst_width,(int)dst_height, Bitmap.Config.ARGB_8888);

        //Mat to B
        Imgproc.cvtColor(outputMat,outputMat,Imgproc.COLOR_GRAY2RGB);
        Utils.matToBitmap(outputMat,outputBitmap);

        return getTable(outputBitmap);
    }

    private double distance(Point p1, Point p2){
        return Math.sqrt((p1.x-p2.x)*(p1.x-p2.x) + (p1.y-p2.y)*(p1.y-p2.y));
    }

    ArrayList<Integer>  getTable(Bitmap result){
//        int sudoku[] = new int[81];
        ArrayList<Integer> sudoku = new ArrayList<Integer>();
        int segW = result.getWidth() / 9;
        int segH = result.getHeight() / 9;

        int xyMargin = 10;
        int sizeMargin = 22;

        int x,y;

        TessBaseAPI tessBaseAPI = new TessBaseAPI();
        tessBaseAPI.init("/data/user/0/com.example.magicleon.sudokuscanner","ita");
        tessBaseAPI.setVariable(TessBaseAPI.VAR_CHAR_WHITELIST,"1234567890");
        tessBaseAPI.setVariable(TessBaseAPI.VAR_CHAR_BLACKLIST,"!@#$%^&*   ()_+=-[]}{" +";:'\"\\|~`,./<>?");
        tessBaseAPI.setImage(result);

        for(int i = 0; i<9;i++){
            y = i*segH + xyMargin;

            for (int j = 0; j<9; j++) {
                x = j * segW + xyMargin;
                tessBaseAPI.setRectangle(x,y,segW-sizeMargin,segH-sizeMargin);
                String number = tessBaseAPI.getUTF8Text();

                try{
                    int  value = Integer.parseInt(number);
                    sudoku.add(i*9+j, value>0 && value<=9 ? value : 0 );
                }catch (NumberFormatException e){
                    sudoku.add(i*9+j, 0 );
                }
            }

        }
        tessBaseAPI.end();
        return  sudoku;
    }
}
