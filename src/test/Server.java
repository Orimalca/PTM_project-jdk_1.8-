package test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class Server {

	public interface ClientHandler{
		public void HandleClient(InputStream socketInputStream, OutputStream socketOutputStream);
		default void Bye(OutputStream socketOutputStream) throws IOException { socketOutputStream.write("bye".getBytes()); }
	}

//	private volatile int clientCount; // amount of client handling
//	private final int clientLimit; // clientLimit is constant, limit amount of threads
	volatile boolean stop;
	public Server() { stop=false; /*clientCount = 0; clientLimit = 10;*/ }

	
	private void startServer(int port, ClientHandler ch) {
		try {
//			Create ServerSocket:
			ServerSocket theServer = new ServerSocket(port);
//			Set max-time the server will wait for a client to connect:
			theServer.setSoTimeout(1000); // 1 seconds(because setSoTimeout gets milliseconds)
//			the ServerSocket listens for requests(that satisfies the chosen protocol) written to the chosen port(on the server framework):
			while(!stop) {
				try (
					Socket aClient = theServer.accept();
					InputStream fromClient = aClient.getInputStream();
					OutputStream toClient = aClient.getOutputStream();
//					aClient Socket listening to the same port the ServerSocket and writing to the client port that his socket took over.
//					aClient conducting the connection with the client in a thread so main ServerSocket can still listen for other requests.

//					try-with-resources automatically close all connections with the client.
//					closes aClient, fromClient and toClient because Socket, InputStream and OutputStream implements Autocloseable(interface)
				 ) {
					ch.HandleClient(fromClient, toClient);
					ch.Bye(toClient);
				} catch (SocketTimeoutException e) { // will catch SocketTimeoutException before closeException
//				Don't exit the program, just close the server.
				stop();
//				will go and close theServer ServerSocket after that
				}
			}
		theServer.close();
//		if the timeout elapses before the method returns, the program will throw a SocketTimeoutException.
//		SocketTimeoutException catch need to be before IOException because SocketTimeoutException is type of(inherit) IOException so the thrown
//		SocketTimeoutException will get inside the IOException block if it's first(if the IOException block is before the SocketTimeoutException)
//		(reminder: SocketTimeoutException has a IOException part because it inherits IOException)
/*
		} catch (SocketTimeoutException e) {
//			Don't exit the program, just close the server.
			stop(); */
		} catch (IOException e) {
			e.printStackTrace();
		}

	}


	/*
	/**
	 * Gets a Socket that represents a client and ClientHandler that will handle the client(the way it handles clients)
	 * @param aClient Socket - represents the client
	 * @param ch ClientHandler - handles the client
	 */
	/*
	public void clientThread(Socket aClient, ClientHandler ch) {	// ----!! NEED TO TAKE CARE TO CLIENT LIMIT(CLIENT THREADS LIMIT) !!----
		new Thread(() -> {
			try {
			if(clientCount == clientLimit) then wait until other clients end and then start(maybe add a while-loop to handle it)
				clientCount++;
				ch.HandleClient(aClient);
//				it's not the responsibility of the ClientHandler to close the Socket
//				because we might want to run another(and other) type of ClientHandler to continue the interaction with the client
				clientCount--;
				aClient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}).start();
	}
 */



	// runs the server in its own thread
	public void start(int port, ClientHandler ch) { new Thread(()->startServer(port,ch)).start(); }
	
	public void stop() { stop=true; }
}
