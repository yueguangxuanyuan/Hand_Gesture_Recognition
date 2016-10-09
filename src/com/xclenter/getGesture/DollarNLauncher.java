package com.xclenter.getGesture;

import java.io.File;
import java.io.FilenameFilter;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.Vector;

import org.opencv.core.Point;

import com.dollarN.NBestList;
import com.dollarN.NDollarParameters;
import com.dollarN.NDollarRecognizer;
import com.dollarN.PointR;
import com.dollarN.Utils;

public class DollarNLauncher {

	static NDollarRecognizer _rec = null;

	Vector<Vector<PointR>> strokes = new Vector<Vector<PointR>>();

	public DollarNLauncher() {
		String samplesDir = NDollarParameters.getInstance().SamplesDirectory;

		_rec = new NDollarRecognizer();

		// create the set of filenames to read in
		File currentDir = new File(samplesDir);
		File[] allXMLFiles = currentDir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.toLowerCase().endsWith(".xml");
			}
		});

		// read them
		for (int i = 0; i < allXMLFiles.length; ++i) {
			_rec.LoadGesture(allXMLFiles[i]);
		}
	}
	
	public void addStroke(Point[] pointArray){
		Vector<PointR> points = new Vector<>();
		for(Point point : pointArray){
			points.add(new PointR(point.x,point.y));
		}
		if(points.size() > 1){
			strokes.add(new Vector<PointR>(points));
		}
	}
	
	public void recongnization(Point[] pointArray){
		try{
			strokes.clear();
			addStroke(pointArray);
			Vector<PointR> allPoints = new Vector<PointR>();
			Enumeration<Vector<PointR>> en = strokes.elements();
			while (en.hasMoreElements()) {
				Vector<PointR> pts = en.nextElement();
				allPoints.addAll(pts);
			}
			NBestList result = _rec.Recognize(allPoints, strokes.size());
			String resultTxt;
			if (result.getScore() == -1) {
				resultTxt = MessageFormat.format(
						"No Match!\n[{0} out of {1} comparisons made]",
						result.getActualComparisons(),
						result.getTotalComparisons());
				System.out.println("No Match!");
			} else {
				resultTxt = MessageFormat
						.format("{0}: {1} ({2}px, {3}{4})  [{5,number,integer} out of {6,number,integer} comparisons made]",
								result.getName(),
								Utils.round(result.getScore(), 2),
								Utils.round(result.getDistance(), 2),
								Utils.round(result.getAngle(), 2),
								(char) 176, result.getActualComparisons(),
								result.getTotalComparisons());
				System.out.println("Result: " + result.getName() + " ("
						+ Utils.round(result.getScore(), 2) + ")");
			}
			System.out.println(resultTxt);
		}finally{
			strokes.clear();
		}
	
	}
	
	public void save(String name) {
		try {
			if (strokes == null || strokes.size() == 0) {
				System.out.println("Cannot save - no gesture!");
				return;
			}
			Vector<Integer> numPtsInStroke = new Vector<Integer>();
			Enumeration<Vector<PointR>> en = strokes.elements();
			while (en.hasMoreElements()) {
				Vector<PointR> pts = en.nextElement();
				numPtsInStroke.add(pts.size());
			}

			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
			if (_rec.SaveGesture(
					NDollarParameters.getInstance().SamplesDirectory
							+ "\\"
							+ name
							+ "_"
							+ dateFormat.format(GregorianCalendar.getInstance()
									.getTime()) + ".xml", strokes,
					numPtsInStroke)) {
				System.out.println("Gesture saved.");
			} else {
				System.out.println("Gesture save failed.");
			}
		} finally {
			strokes.clear();
		}

	}
}
