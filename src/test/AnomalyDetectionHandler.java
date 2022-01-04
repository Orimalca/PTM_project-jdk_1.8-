package test;


import test.Commands.DefaultIO;
import test.Server.ClientHandler;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class AnomalyDetectionHandler implements ClientHandler{

	@Override
	public void HandleClient(InputStream socketInputStream, OutputStream socketOutputStream) {
		SocketIO aClient_SocketIO = new SocketIO(socketInputStream, socketOutputStream);
		CLI aClient_CLI = new CLI(aClient_SocketIO);
		aClient_CLI.start();
	}

	public class SocketIO implements DefaultIO{

//		Data Members:
		Scanner in;
		PrintWriter out;

//		CTOR:
		public SocketIO (InputStream socketInputStream, OutputStream socketOutputStream) {
			in = new Scanner(socketInputStream);
			out = new PrintWriter(socketOutputStream);
		}

//		Read Methods Override:
		@Override
		public String readText() {
			return in.nextLine();
		}
		@Override
		public float readVal() { return in.nextFloat(); }

//		Write Methods Override:
//		IMPORTANT NOTE(RULE 1 IN NETWORK COMMUNICATION WITH SOCKETS):
//		WE MUST DO flush() SO THE CONTENT(THE STATE) WILL BE WRITTEN IMMEDIATELY AFTER print() AND NOT ONLY WHEN WE CLOSE THE SocketOutputStream.
		@Override
		public void write(String text) { out.print(text); out.flush(); }
		@Override
		public void write(float val) { out.print(val); out.flush(); }

//		Close(Resources) Method:
		public void close() {
			in.close();
			out.close();
		}

	}

//

}
