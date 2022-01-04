package test;

import java.util.ArrayList;

import test.Commands.Command;
import test.Commands.DefaultIO;

public class CLI {

	ArrayList<Command> commands;
	DefaultIO dio;
	Commands c;

	public CLI(DefaultIO dio) {
		this.dio = dio;
		c = new Commands(dio);
		commands = new ArrayList<Command>();

		// example: commands.add(c.new ExampleCommand());
		// implement
		commands.add(c.new Upload_Command());
		commands.add(c.new Threshold_Command());
		commands.add(c.new Detect_Command());
		commands.add(c.new DisplayReports_Command());
		commands.add(c.new UploadAnomaliesAndAnalyze_Command());
		commands.add(c.new Exit_Command());
	}

	public void start() {
		while(!c.Exit()) {
			c.PrintMenu();
			switch ((int)dio.readValLine()) {
				case 1: { commands.get(0).execute(); break; }
				case 2: { commands.get(1).execute(); break; }
				case 3: { commands.get(2).execute(); break; }
				case 4: { commands.get(3).execute(); break; }
				case 5: { commands.get(4).execute(); break; }
				case 6: { commands.get(5).execute(); break; }
			}
		}
	}




}



