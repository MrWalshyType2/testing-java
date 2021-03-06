package com.qa.app.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.ServerSocket;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qa.app.http.filter.FilterChain;
import com.qa.app.http.handler.HttpMessageHandlerImpl;
import com.qa.app.http.message.HttpParser;
import com.qa.app.utilities.Annotation;

public class ServerListenerThread extends Thread {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(ServerListenerThread.class);
	
	private int port;
	private String viewsLocation;
	private ServerSocket serverSocket;
	private FilterChain filterChain;

	public ServerListenerThread(int port, String viewsLocation) throws IOException {
		super();
		this.port = port;
		this.viewsLocation = viewsLocation;
		this.serverSocket = new ServerSocket(this.port);
		this.filterChain = loadFilters();
	}

	private FilterChain loadFilters() {
		// TODO Auto-generated method stub
//		Filter[] filters = Class
		try {
			Annotation.scan("src/main/java/com/qa/app");
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | NoSuchMethodException | SecurityException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void run() {
		try {
			while (serverSocket.isBound() && !serverSocket.isClosed()) {
				LOGGER.info("OPENING SOCKET ON PORT " + serverSocket.getLocalPort());
				
				Socket socket = serverSocket.accept(); // Stops and waits for a connection
				
				LOGGER.info("SOCKET ON PORT " + serverSocket.getLocalPort() + 
							" ACCEPTED CONNECTION: " + socket.getInetAddress());
				
				LOGGER.info("OPENING WORKER THREAD FOR CONNECTION " + socket.getInetAddress() +
							" ON PORT " + socket.getPort() + 
							" ON SOCKET WITH PORT " + socket.getLocalPort());
				HttpConnectionWorkerThread workerThread = 
						new HttpConnectionWorkerThread(socket,
													   filterChain,
													   new HttpMessageHandlerImpl(new HttpParser()));
				workerThread.start();
			}
			// serverSocket.close(); // TODO: HANDLE CLOSE
		} catch (IOException e) {
			//throw new ServerException("Error creating server", e);
			LOGGER.error("ERROR ENCOUNTERED OPENING SOCKET ON PORT: " + port);
		} finally {
			if (serverSocket != null) {
				try {
					serverSocket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
