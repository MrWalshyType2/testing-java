package com.qa.app.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qa.app.http.HttpStatusCode;
import com.qa.app.http.exception.HttpParsingException;
import com.qa.app.http.handler.HttpMessageHandler;
import com.qa.app.http.message.HttpParser;
import com.qa.app.http.message.HttpRequest;
import com.qa.app.http.message.HttpResponse;

public class HttpConnectionWorkerThread extends Thread {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(HttpConnectionWorkerThread.class);
	
	private Socket socket;
	private HttpMessageHandler<HttpRequest, HttpResponse> handler;

	public HttpConnectionWorkerThread(Socket socket, 
									  HttpMessageHandler<HttpRequest, HttpResponse> handler) {
		this.socket = socket;
		this.handler = handler;
	}
	
	@Override
	public void run() {
		InputStream in = null;
		OutputStream out = null;
		try {
			 in = socket.getInputStream();
			 out = socket.getOutputStream();
			
			final String CRLF = "\r\n"; // 13, 10
			
			HttpResponse response = handler.handle(in);
			
			// Write to output stream
			out.write(response.build().getBytes());
		} catch (IOException e) {
			LOGGER.error("CONNECTION ERROR ENCOUNTERED", e);
			e.printStackTrace();
		} catch (HttpParsingException e) {
			LOGGER.error("HTTP PARSING ERROR ENCOUNTERED", e);
			e.printStackTrace();
		} finally {
			try {
				if (in != null) in.close();
				if (out != null) out.close();
				if (socket != null) socket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
		LOGGER.info("END OF PROCESSING ON PORT: " + socket.getLocalPort());
	}

}
