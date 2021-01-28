package com.qa.app.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpConnectionWorkerThread extends Thread {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(HttpConnectionWorkerThread.class);
	
	private Socket socket;

	public HttpConnectionWorkerThread(Socket socket) {
		this.socket = socket;
	}
	
	@Override
	public void run() {
		InputStream in = null;
		OutputStream out = null;
		try {
			 in = socket.getInputStream();
			 out = socket.getOutputStream();
			
			final String CRLF = "\r\n"; // 13, 10
			
			// Setup HTML page
			String html = "<html><head><title>Simple HttpServer</title></head><body><h1>Page served</h1></body></html>";
			String response = 
					"HTTP/1.1 200 OK" + CRLF + // Status: HTTP_VERSION RESPONSE_CODE RESPONSE_MESSAGE
					"Content-Length: " + html.getBytes().length + CRLF + // HEADER
					CRLF +
					html + // BODY
					CRLF + CRLF;
			
			// Write to output stream
			out.write(response.getBytes());
		} catch (IOException e) {
			LOGGER.error("CONNECTION ERROR ENCOUNTERED", e);
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
