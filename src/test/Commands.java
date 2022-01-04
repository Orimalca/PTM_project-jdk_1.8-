package test;

import java.io.*;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class Commands {

	public interface DefaultIO{

//		Abstract Methods:
		public String readText();
		public void write(String text);
		public float readVal();
		public void write(float val);

//		Default Methods:

		// operates readVal() method and move to the next line(skips next content until gets to the next line);
		default float readValLine() {
			float val = readVal();
			readText(); // skip line
			return val;
		}

		// operates write() method and move to the next line(and adds "\n" at the end)
		default void writeLine(String text) { write(text); write("\n"); }
		default void writeLine(float val) { write(val); write("\n"); }

		/**
		 * Read inputs(using readText() method) and sends to desired out, also returns the amount of lines it read.
		 * @param out Writer
		 * @param exitStr String
		 * @return Amount of lines read
		 * @throws IOException
		 */
		default long ReadInputsAndSend(PrintWriter out, String exitStr) throws IOException {
			long count = 0;
			String line;
			while(!(line = readText()).equals(exitStr)) {
				count++;
				out.println(line);
			}
			return count;
		}

	}


	DefaultIO dio;
	public Commands(DefaultIO dio) {
		this.dio=dio;
	}


	/**
	 * Returns exit status.
	 * @return boolean
	 */
	public boolean Exit() { return sharedState.getExit(); }
	public void PrintMenu() {
		dio.write("Welcome to the Anomaly Detection Server.\n" +
				"Please choose an option:\n");
		sharedState.commands.forEach((cmdNumber, cmd) -> dio.write(cmdNumber + ". " + cmd + "\n"));

	}


	private class SharedState{

//		CTOR:
		public SharedState() {
			this.correlationThreshold = 0.9f;
			this.commands = new HashMap<Integer, Command>();
			this.exit = false;
			TP = 0; FP = 0;
		}

//		Data Members:
		private String train_fileName, test_fileName, anomalies_fileName;
		private float correlationThreshold;
		private TimeSeries train_TS, test_TS;
		private List<AnomalyReport> anomalyReports;
		private final HashMap<Integer, Command> commands;
		private long train_linesAmount, test_linesAmount, anomalies_linesAmount_P, anomalies_linesAmount_N;
		private long TP, FP;
		private boolean exit;


//		Getters:
		public float getCorrelationThreshold() { return correlationThreshold; }
		public TimeSeries getTrain_TS() { return train_TS; }
		public TimeSeries getTest_TS() { return test_TS; }
		public String getTrain_fileName() { return train_fileName; }
		public String getTest_fileName() { return test_fileName; }
		public List<AnomalyReport> getAnomalyReports() { return anomalyReports; }
		public String getAnomalies_fileName() { return anomalies_fileName; }
		public long getTrain_linesAmount() { return train_linesAmount; }
		public long getTest_linesAmount() { return test_linesAmount; }
		public long getAnomalies_linesAmount_P() { return anomalies_linesAmount_P; }
		public long getAnomalies_linesAmount_N() { return anomalies_linesAmount_N; }
		public boolean getExit() { return exit; }
		public long getTP() { return TP; }
		public long getFP() { return FP; }


//		Setters:
		public void setCorrelationThreshold(float correlationThreshold) { this.correlationThreshold = correlationThreshold; }
		public void setTrain_TS(TimeSeries train_TS) { this.train_TS = train_TS; }
		public void setTest_TS(TimeSeries test_TS) { this.test_TS = test_TS; }
		public void setTrain_fileName(String train_fileName) { this.train_fileName = train_fileName; }
		public void setTest_fileName(String test_fileName) { this.test_fileName = test_fileName; }
		public void setAnomalyReports(List<AnomalyReport> anomalyReports) { this.anomalyReports = anomalyReports; }
		public void setAnomalies_fileName(String anomalies_fileName) { this.anomalies_fileName = anomalies_fileName; }
		public void setTrain_linesAmount(long train_linesAmount) { this.train_linesAmount = train_linesAmount; }
		public void setTest_linesAmount(long test_lineAmount) { this.test_linesAmount = test_lineAmount; }
		public void setAnomalies_linesAmount_P(long anomalies_linesAmount_P) { this.anomalies_linesAmount_P = anomalies_linesAmount_P; }
		public void setAnomalies_linesAmount_N(long anomalies_linesAmount_N) { this.anomalies_linesAmount_N = anomalies_linesAmount_N; }
		public void setExit(boolean exit) { this.exit = exit; }
		public void setTP(long TP) { this.TP = TP; }
		public void setFP(long FP) { this.FP = FP; }


		/**
		 * Gets a desired name for the csv file that is generated(sent from the client through the IO).
		 * @param fileName String
		 * @return Amount of lines the file has.
		 */
		public long GenerateCSVfile(String fileName) {
			try(PrintWriter pw = new PrintWriter(new FileWriter(fileName))) { // try-with-resource will close pw automatically before return
				return dio.ReadInputsAndSend(pw, "done");
// 				ReadInputsAndSend also COUNTED the first line of contents(A,B,C,...) so need to -1 in case we want to count value lines only.
			} catch (IOException e) { e.printStackTrace(); }

			return 0; // will never get here
		}

		/**
		 * Gets a key of command inside the map and returns the command.
		 * @param key Integer
		 * @return Command
		 */
		public Command GetCommand(Integer key) { return commands.get(key); }

		/**
		 * Gets a key and command and puts it inside the HashMap
		 * @param key Integer
		 * @param cmd Command
		 */
		public void PutCommand(Integer key, Command cmd) { commands.put(key, cmd); }

		/**
		 * Gets a key of command inside the map and execute the command.
		 * @param key Integer
		 */
		public void ExecuteCommand(Integer key) { commands.get(key).execute(); }


	}

	private  SharedState sharedState=new SharedState();


	public abstract class Command{

//		Data Members:
		protected String description;

//		CTOR:
		public Command(String description) { this.description=description; }

//		Abstract Methods:
		public abstract void execute();

		@Override
		public final String toString() {
			return this.description;
		}

	}

	public class Upload_Command extends Command {

//		CTOR:
		public Upload_Command() {
			super("upload a time series csv file");
			sharedState.PutCommand(1, this);
		}

//		execute() override:
		@Override
		public void execute() {
//			Train File:
			dio.write("Please upload your local train CSV file.\n");
			sharedState.setTrain_fileName("anomalyTrain.csv");
//			GenerateCSVfile also COUNTED the first line of contents(A,B,C,...) so need to -1 in case we want to count value lines only.
			sharedState.setTrain_linesAmount(sharedState.GenerateCSVfile(sharedState.getTrain_fileName()) - 1);
			dio.write("Upload complete.\n");

//			Test File:
			dio.write("Please upload your local test CSV file.\n");
			sharedState.setTest_fileName("anomalyTest.csv");
//			GenerateCSVfile also COUNTED the first line of contents(A,B,C,...) so need to -1 in case we want to count value lines only.
			sharedState.setTest_linesAmount(sharedState.GenerateCSVfile(sharedState.getTest_fileName()) - 1);
			dio.write("Upload complete.\n");
		}
	}


	public class Threshold_Command extends Command {

//		CTOR:
		public Threshold_Command() {
			super("algorithm settings");
			sharedState.PutCommand(2, this);
		}

//		execute() override:
		@Override
		public void execute() {
			dio.write("The current correlation threshold is " + sharedState.getCorrelationThreshold() + "\n" +
					"Type a new threshold\n");

			float threshold = dio.readValLine();
			while(threshold <= 0 || threshold >= 1) {
				dio.write("please choose a value between 0 and 1.\n");
				threshold = dio.readValLine();
			}

			sharedState.setCorrelationThreshold(threshold);
		}
	}


	public class Detect_Command extends Command {

//		CTOR:
		public Detect_Command() {
			super("detect anomalies");
			sharedState.PutCommand(3, this);
		}

//		execute() override:
		@Override
		public void execute() {
			sharedState.setTrain_TS(new TimeSeries(sharedState.getTrain_fileName()));
			sharedState.setTest_TS(new TimeSeries(sharedState.getTest_fileName()));

			SimpleAnomalyDetector detector = new SimpleAnomalyDetector();
			detector.setCorrelationThreshold(sharedState.getCorrelationThreshold());

			detector.learnNormal(sharedState.getTrain_TS());
			sharedState.setAnomalyReports(detector.detect(sharedState.getTest_TS()));

			dio.writeLine("anomaly detection complete.");
		}
	}

	public class DisplayReports_Command extends Command {

//		CTOR:
		public DisplayReports_Command() {
			super("display results");
			sharedState.PutCommand(4, this);
		}


//		execute() override:
		@Override
		public void execute() {	// tab + space is required for actual length as inside the expectedOutput.txt file.....
			sharedState.getAnomalyReports().forEach( report -> dio.writeLine(report.timeStep + "\t " + report.description));
			dio.writeLine("Done.");
		}

	}

	public class UploadAnomaliesAndAnalyze_Command extends Command {

//		CTOR:
		public UploadAnomaliesAndAnalyze_Command() {
			super("upload anomalies and analyze results");
			sharedState.PutCommand(5, this);
		}

		private class IndexHolder { public int index = 0; }
		
		private class AnomalyRange {
			private final Map.Entry<Long, Long> range;
			private final String description;

//			CTORS:
			public AnomalyRange(Long START_timeStep, Long END_timeStep, String description) {
				this.description = description;
				this.range = new AbstractMap.SimpleEntry<Long, Long>(START_timeStep, END_timeStep);
			}
			public AnomalyRange(Long START_timeStep, Long END_timeStep) { this(START_timeStep, END_timeStep, null); }
			public AnomalyRange(AnomalyReport anomalyReport) {
				this(anomalyReport.timeStep, anomalyReport.timeStep, anomalyReport.description);
			}
			private AnomalyRange(String anomalyRange_line) {
				this(
					Long.parseLong(anomalyRange_line.split(",")[0]) ,
					Long.parseLong(anomalyRange_line.split(",")[1])
				);
			}

//			Getters:
			public String getDescription() { return description; }
			public Long getStart() { return range.getKey(); }
			public Long getEnd() { return range.getValue(); }
			public Long getAnomaliesAmount() { return getEnd() - getStart() + 1; }

//			Setters:
			public void setEnd(Long newEND_timeStep) { this.range.setValue(newEND_timeStep); }

		}


		@Override
		public void execute() {
			try {
				dio.writeLine("Please upload your local anomalies file.");
				sharedState.setAnomalies_fileName("clientAnomaliesRanges.csv");
//				GenerateCSVfile also COUNTED the first line of contents(A,B,C,...) so need to -1 in case we want to count value lines only.
//				but we don't have line of contents so no need to -1 (in face we might get an exception if we will -1).
				sharedState.setAnomalies_linesAmount_P(sharedState.GenerateCSVfile(sharedState.getAnomalies_fileName()));
				sharedState.setAnomalies_linesAmount_N(sharedState.getTest_linesAmount());	// N = n
				dio.write("Upload complete.\n"
//						+ "Analyzing...\n"		For some reason then are not printing it although they asked to....
				);


				List<AnomalyRange> clientAnomaliesRanges;
				try( // the "try()" will make sure the stream will close after the try block ("{}")
					 Stream<AnomalyRange> clientAnomaliesRanges_stream =
					 Files.lines(Paths.get(sharedState.getAnomalies_fileName()))
					 .parallel()
					 .map(anomaly_range_line -> {
						 AnomalyRange anomaly_range = new AnomalyRange(anomaly_range_line);

						 sharedState.setAnomalies_linesAmount_N(
								 sharedState.getAnomalies_linesAmount_N() - anomaly_range.getAnomaliesAmount()
						 );

						 return anomaly_range;
					 })
				) { clientAnomaliesRanges = clientAnomaliesRanges_stream.collect(Collectors.toList()); }

//				Another Possibility:
//				.collect(Collectors.toCollection(ArrayList<AnomalyRange>::new));		If we wanted an ArrayList we would do this.

//				The Next Line Will Produce An ERROR!!
//				.collect(Collectors.toCollection(List<AnomalyRange>::new));
//				REASON:
//				because List is abstract, it's not possible to create an instance of List
//				(we can create an instance of LinkedList and hold the List part of the LinkedList with a List variable, but we cant create
//				an instance of List).


				IndexHolder indexHolder = new IndexHolder();
				List<AnomalyRange> serverAnomaliesRanges = new ArrayList<AnomalyRange>(1);
				serverAnomaliesRanges.add(new AnomalyRange(sharedState.getAnomalyReports().get(0)) );
				for (int i = 1; i < sharedState.getAnomalyReports().size(); i++) {
					if (serverAnomaliesRanges.get(indexHolder.index).getEnd().equals(sharedState.getAnomalyReports().get(i).timeStep - 1)
						&& serverAnomaliesRanges.get(indexHolder.index).getDescription().equals(sharedState.getAnomalyReports().get(i).description)
					) serverAnomaliesRanges.get(indexHolder.index).setEnd(sharedState.getAnomalyReports().get(i).timeStep);

					else {
						serverAnomaliesRanges.add(new AnomalyRange(sharedState.getAnomalyReports().get(i)));
						indexHolder.index++;
					}
				}


//				Count FP:
				sharedState.setFP(
					serverAnomaliesRanges
					.parallelStream()
					.filter(anomalyRange_server ->
							clientAnomaliesRanges
							.parallelStream()
							.allMatch(  anomalyRange_client ->
										anomalyRange_server.getEnd() < anomalyRange_client.getStart()
										|| anomalyRange_client.getEnd() < anomalyRange_server.getStart()
							)
					)
					.count()
				);

//				Count TP:
				sharedState.setTP(
					clientAnomaliesRanges
					.parallelStream()
					.filter( anomalyRange_client ->
							 serverAnomaliesRanges
							.parallelStream()
							.anyMatch(  anomalyRange_server -> !
										(	// NOTICE THE "!" BEFORE
										anomalyRange_server.getEnd() < anomalyRange_client.getStart()
										|| anomalyRange_client.getEnd() < anomalyRange_server.getStart()
										)
							)
					)
					.count()
				);


				float TP_rate = (float)sharedState.getTP() / (float)sharedState.getAnomalies_linesAmount_P();
				float FP_rate = (float)sharedState.getFP() / (float)sharedState.getAnomalies_linesAmount_N();

//				Round Rates(exactly 3 decimals):
				DecimalFormat df_down = new DecimalFormat("0.000"); df_down.setRoundingMode(RoundingMode.DOWN);
				TP_rate = Float.parseFloat(df_down.format(TP_rate));
				FP_rate = Float.parseFloat(df_down.format(FP_rate));

//				Print Rates:
				dio.write("True Positive Rate: "); dio.writeLine(TP_rate);
				dio.write("False Positive Rate: "); dio.writeLine(FP_rate);

			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}


	public class Exit_Command extends Command {
		public Exit_Command() {
			super("exit");
			sharedState.PutCommand(6, this);
		}

		@Override
		public void execute() {
			sharedState.setExit(true);
		}

	}






}
