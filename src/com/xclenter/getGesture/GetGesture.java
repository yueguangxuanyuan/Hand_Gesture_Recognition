package com.xclenter.getGesture;

import java.util.ArrayList;
import java.util.List;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

public class GetGesture {
	private static DollarNLauncher dollarN = new DollarNLauncher();
	private static final int kernelSize = 5;
	private static final double stdDev = 0;
	
	public static Point[] getGestureFromImg(String fileName){
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

		Mat capturedFrame = Highgui.imread(fileName,
				Highgui.CV_LOAD_IMAGE_COLOR);
		Mat grayFrame = new Mat();
		Mat blurFrame = new Mat();
		Mat thresholdFrame = new Mat();
		Imgproc.cvtColor(capturedFrame, grayFrame,
				Imgproc.COLOR_RGB2GRAY);
		Imgproc.GaussianBlur(grayFrame, blurFrame, new Size(
				kernelSize, kernelSize), stdDev);
		Imgproc.threshold(blurFrame, thresholdFrame, 127, 255,
				Imgproc.THRESH_BINARY + Imgproc.THRESH_OTSU);

		// finding contours
		Mat newImgT = thresholdFrame.clone();
		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
		Mat hierarchy = new Mat();
		Imgproc.findContours(newImgT, contours, hierarchy,
				Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);

		// extracting largest contour
		double maxArea = 1000;
		int contourIdx = -1;
		for (int i = 0; i < contours.size(); ++i) {
			double area = Imgproc.contourArea(contours.get(i));
			if (area > maxArea) {
				maxArea = area;
				contourIdx = i;
			}
		}
		MatOfPoint tempContours = new MatOfPoint(
				contours.get(contourIdx));
		Point[] borderPoints = tempContours.toArray();
		return borderPoints;
	}
	
	public static void doRecognize(String fileName){
		Point[] gesturepoints = getGestureFromImg(fileName);
		dollarN.recongnization(gesturepoints);
		
	}
	
}
