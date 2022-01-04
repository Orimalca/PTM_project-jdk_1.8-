package test;

import java.io.*;
import java.util.*;

public class TimeSeries {
	private HashMap<String, Vector<Float>> map;
	private String[] columnNames;		// String array of column names.


	public TimeSeries(String csvFileName) {

		try {
			BufferedReader br = new BufferedReader(new FileReader(csvFileName));

			String line = br.readLine();	// Read First Line.

			// save first content
			columnNames = line.split(",");
			//

			map = new HashMap<String, Vector<Float>>();
			for (int i = 0; i < columnNames.length; i++) map.put(columnNames[i], new Vector<Float>());

			String[] rowValues;
			while ((line = br.readLine()) != null) {
				rowValues = line.split(",");			// row values separated

				for(int i = 0; i < columnNames.length; i++)
					(map.get(columnNames[i])).add( Float.parseFloat(rowValues[i]) );
			}

		} catch (IOException e) {
//			e.printStackTrace();
		}

	}

	/**
	 * Returns the String inside columnNames at the specific given index.
	 * @param index index of column inside columnNames.
	 * @return String - name of Column
	 */
	public String NameOfCol(int index) { return columnNames[index]; }

	/**
	 * Returns amount of columns based on size of columnNames length.
	 * @return int - amount of Columns
	 */
	public int ColumnsAmount() { return columnNames.length; }

	/**
	 * Returns amount of rows based on first Vector<Float> length.
	 * @return int - amount of rows
	 */
	public int RowsAmount() { return (map.get(columnNames[0])).size(); }
	public Vector<Float> GetColByName(String name) { return map.get(name); }

	/**
	 * Returns the Float value at the specific (given) index of the desired (given) Column.
	 * @param colName name of desired column
	 * @param index index of the desired Float
	 * @return Float - Value of Float from column
	 */
	public Float FloatFromCol(String colName , int index) { return (map.get(colName)).elementAt(index); }

	/**
	 * Returns the TimeStep(the row) at (given) index.
	 * @param index The index of desired row
	 * @return Vector<Float> - row values inside a Vector<Float> variable
	 */
	public Vector<Float> GetTimeStep(int index) {
		Vector<Float> timeStep = new Vector<Float>();

		for(int j = 0; j < columnNames.length; j++) { timeStep.add( FloatFromCol(columnNames[j], index) ); }
		return timeStep;
	}

	/**
	 * Gets name of specific column and Returns a copy of column (copy of Vector<Float>) as primitive array of floats(float[]), with same values as column(Vector<Float>).
	 * @param colName name of the desired column
	 * @return float[] - primitive array of floats
	 */
	public float[] ColToPrimitiveArray(String colName) {
		float[] arr = new float[RowsAmount()];

		for(int i = 0; i < RowsAmount(); i++)
			arr[i] = FloatFromCol(colName, i);

		return arr;
	}

	/**
	 * Creates Point[] array from 2 columns.
	 * @param colName1 name of first column
	 * @param colName2 name of second column
	 * @return Point[] - array of points based on the two columns.
	 */
	public Point[] PointsArray(String colName1, String colName2) {
		Point[] points = new Point[RowsAmount()];

		for (int k = 0; k < RowsAmount(); k++) {
			points[k] = new Point(
					( (float)FloatFromCol(colName1, k) ),
					( (float)FloatFromCol(colName2, k) )
			);
		}

		return points;
	}

}
