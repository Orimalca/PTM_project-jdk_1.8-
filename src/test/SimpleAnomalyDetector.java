package test;

import java.util.LinkedList;
import java.util.List;

public class SimpleAnomalyDetector implements TimeSeriesAnomalyDetector {
	List<CorrelatedFeatures> cfList;
	public float correlationThreshold;

	//	CTOR:
	public SimpleAnomalyDetector() {
		this.cfList = new LinkedList<CorrelatedFeatures>();
		this.correlationThreshold = (float)0.9;
	}

	//	Getters:
	public List<CorrelatedFeatures> getCfList() { return cfList; }
	public float getCorrelationThreshold() { return correlationThreshold; }

	//	Setters:
	public void setCfList(List<CorrelatedFeatures> cfList) { this.cfList = cfList; }
	public void setCorrelationThreshold(float correlationThreshold) { this.correlationThreshold = correlationThreshold; }



	@Override
	public void learnNormal(TimeSeries ts) {
		float m, p, maxDev = 0;
		int c;
		float[] column1, column2;
		Point[] points;

		for(int i = 0; i < ts.ColumnsAmount(); i ++) {
			m = 0;
			c = -1;

			for (int j = i + 1; j < ts.ColumnsAmount(); j++) {
				p = Math.abs( StatLib.pearson(
								(ts.ColToPrimitiveArray( ts.NameOfCol(i) )) ,	// first float[] arr
								(ts.ColToPrimitiveArray( ts.NameOfCol(j) ))		// second float[] arr
						)
				);

				if(p > m && p > correlationThreshold) { m = p; c = j; }
			}

			if(c != -1) {

				points = ts.PointsArray(ts.NameOfCol(i), ts.NameOfCol(c));

				Line line = StatLib.linear_reg(points);
				for (Point point : points) {
					if( StatLib.dev(point, line) > maxDev ) maxDev = StatLib.dev(point, line);
				}

				maxDev = maxDev*(1.1f);		// prevent close cases (3.00001 > 3)
				cfList.add( new CorrelatedFeatures(ts.NameOfCol(i), ts.NameOfCol(c), m, line, maxDev) );
			}

		}
	}


	@Override
	public List<AnomalyReport> detect(TimeSeries ts) {
		List<AnomalyReport> list = new LinkedList<AnomalyReport>();
		Point[] points;

		for (CorrelatedFeatures cf : cfList) {
			points = ts.PointsArray(cf.feature1, cf.feature2);

			for(int i = 0; i < points.length; i++)
				if( StatLib.dev(points[i], cf.lin_reg) > cf.threshold ) list.add(new AnomalyReport((cf.feature1+"-"+cf.feature2), (i+1)));	// i+1 represent the exact row.

		}

		return list;
	}



	public List<CorrelatedFeatures> getNormalModel() { return this.cfList; }


}


