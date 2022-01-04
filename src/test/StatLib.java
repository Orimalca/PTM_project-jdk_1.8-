package test;

import java.lang.Math;


public class StatLib {

	// simple average
	public static float avg(float[] x){
		float avg = 0;

		for (float num : x) {
			avg += num;
		}

		return avg/x.length;
	}

	// returns the variance of X and Y
	public static float var(float[] x){
		float xAvg = 0;

		for (float num : x)
			xAvg += (num - avg(x))*(num - avg(x));

		xAvg /= x.length;

		return xAvg;
	}

	// returns the covariance of X and Y
	public static float cov(float[] x, float[] y){
		float sum = 0;

		for (int i = 0; i < x.length; i++)
			sum += (x[i] - avg(x))*(y[i] - avg(y));

		sum /= (x.length);

		return sum;
	}


	// returns the Pearson correlation coefficient of X and Y
	public static float pearson(float[] x, float[] y){
		return (cov(x,y))/((float)((Math.sqrt(var(x)))*(Math.sqrt(var(y)))));
		// we might lose some data because Math.sqrt(double a)
	}

	// performs a linear regression and returns the line equation
	public static Line linear_reg(Point[] points){
		float x[] = new float[points.length];
		float y[] = new float[points.length];
		float xAvg = 0, yAvg = 0;
		float a, b;

		for(int i = 0; i < points.length; i++) {
			xAvg += points[i].x;
			yAvg += points[i].y;

			x[i] = points[i].x;
			y[i] = points[i].y;
		}

		xAvg /= points.length;
		yAvg /= points.length;

		a = cov(x,y)/var(x);
		b = yAvg - a*xAvg;

		return new Line(a,b);
	}

	// returns the deviation between point p and the line equation of the points
	public static float dev(Point p,Point[] points){
		Line l = linear_reg(points);

		return Math.abs(p.y - l.f(p.x));
	}

	// returns the deviation between point p and the line
	public static float dev(Point p,Line l){
		return Math.abs(p.y - l.f(p.x));
	}

}
