package com.xclenter.getGesture;

import java.io.File;
import java.util.Arrays;

import org.opencv.core.Point;

public class Test {

	public static void main(String[] args) {
		addTraningData();
		File[] files = new File("imgs").listFiles();
		for(File file : files){
			System.out.println(file.getName());
			GetGesture.doRecognize("imgs/"+file.getName());
		}
	}

	public static void addTraningData() {
		DollarNLauncher dollarN = new DollarNLauncher();
		Point[] gesturepoints = GetGesture.getGestureFromImg("imgs/rock.png");
//		System.out.println(Arrays.toString(gesturepoints));
		dollarN.addStroke(gesturepoints);
		dollarN.save("rock");

		Point[] gesturepoints_cloth = GetGesture.getGestureFromImg("imgs/cloth.png");
//		System.out.println(Arrays.toString(gesturepoints_cloth));
		dollarN.addStroke(gesturepoints_cloth);
		
//		System.out.println(Arrays.equals(gesturepoints_cloth, gesturepoints));
		dollarN.save("cloth");
	}
}
