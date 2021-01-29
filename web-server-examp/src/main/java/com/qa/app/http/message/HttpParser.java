package com.qa.app.http.message;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qa.app.http.HttpHeaders;
import com.qa.app.http.HttpMethod;
import com.qa.app.http.HttpStatusCode;
import com.qa.app.http.exception.HttpParsingException;

public class HttpParser {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(HttpParser.class);
	
	private static final int SP = 0x20; // hex for 32/space
	private static final int CR = 0x0D; // hex for 13/carriage-return - moves cursor to beginning of line
	private static final int LF = 0x0A; // hex for 18/line-feed - moves cursor down one line
	private static final int COLON = 0x3A;
	
	public HttpRequest parseHttpRequest(InputStream in) throws HttpParsingException {
		// reads bytes from a string and decodes to chars
		InputStreamReader reader = new InputStreamReader(in, StandardCharsets.US_ASCII);
		BufferedReader bufferedReader = new BufferedReader(reader);
		
		HttpRequest request = new HttpRequest();
		try {
			parseRequestLine(bufferedReader, request);
			parseHeaders(bufferedReader, request);
			
			HashMap<String, String> headers = request.getHeaders();
			if (headers.containsKey(HttpHeaders.STANDARD_REQUEST_CONTENT_LENGTH.HEADER_NAME) ||
				headers.containsKey(HttpHeaders.STANDARD_REQUEST_TRANSFER_ENCODING.HEADER_NAME)) {
				parseBody(reader, request);
			}		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return request;
	}
	
	private void parseRequestLine(BufferedReader reader, HttpRequest request) throws IOException, HttpParsingException {
		StringBuilder reqLineBuffer = new StringBuilder();
		int _byte;
		
		// loop through stream until first CRLF or error
		while ((_byte = reader.read()) != -1) {
			if (_byte == CR) {
				if ((_byte = reader.read()) == LF) {
					break;
				}
				throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
			}
			reqLineBuffer.append((char) _byte);
		}
		
		// Quick check for invalid request-line (specifically whitespace at the start)
		String requestLine = reqLineBuffer.toString();
		if (requestLine == null 		||
			requestLine.isEmpty()		||
			requestLine.charAt(0) == SP ||
			requestLine.split(" ").length > 3
			) {
			throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
		}

		// Set the method, target, version, startLine with programmatic validation
		request.setMethod(getMethod(requestLine));
		request.setRequestTarget(getRequestTarget(requestLine));
		request.setHttpVersion(getHttpVersion(requestLine));
		request.setStartLine(requestLine);
		LOGGER.debug("Parsed request-line: " + requestLine);
	}
	
	private String getHttpVersion(String requestLine) {
		// [2] = http version
		List<String> requestLineList = Arrays.asList(requestLine.split(" "));
		String version = requestLineList.get(2);
		
		return version;
	}

	private String getRequestTarget(String requestLine) {
		// [1] = request target
		List<String> requestLineList = Arrays.asList(requestLine.split(" "));
		String target = requestLineList.get(1);
		
		return target;
	}

	private HttpMethod getMethod(String requestLine) throws HttpParsingException {		
		// [0] = Method
		List<String> requestLineList = Arrays.asList(requestLine.split(" "));
		String method = requestLineList.get(0);
		
		if (method.length() > HttpMethod.MAX_LENGTH) throw new HttpParsingException(HttpStatusCode.SERVER_ERROR_501_NOT_IMPLEMENTED);
		
		for (HttpMethod httpMethod : HttpMethod.values()) {
			if (method.equals(httpMethod.name().toString())) {
				return httpMethod;
			} else if (method.equalsIgnoreCase(httpMethod.name().toString())) {
				throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
			}
		}
		throw new HttpParsingException(HttpStatusCode.SERVER_ERROR_501_NOT_IMPLEMENTED);
	}

	private void parseHeaders(BufferedReader bufferedReader, HttpRequest request) throws IOException, HttpParsingException {
//		InputStream data = new BufferedInputStream(reader.);
//		BufferedReader bufferedReader = new BufferedReader(reader);
		StringBuilder dataBuffer = new StringBuilder();
		
		int _byte;
		
		boolean headersParsed = false;
		
		while ((_byte = bufferedReader.read()) >= 0) {
			//if (_byte == SP) throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
			// CRLF check
			if (_byte == CR) {
				_byte = bufferedReader.read();
				
				if (_byte == LF) {
					bufferedReader.mark(2);
					if (bufferedReader.read() == CR && bufferedReader.read() == LF) {
						//bufferedReader.skip(dataBuffer.length());
						return;
					}
					bufferedReader.reset();
					//bufferedReader.skip(dataBuffer.length());
					
					Entry<String, String> header = parseHeader(dataBuffer.toString());
					dataBuffer.delete(0, dataBuffer.length());
					
					LOGGER.debug("Parsed header: " + header.getKey().trim() + ":" + header.getValue());
					
					request.addHeader(header.getKey().trim(), header.getValue());
				}
			}

			dataBuffer.append((char) _byte);
		}
	}

	private Entry<String, String> parseHeader(String string) throws HttpParsingException {		
		// TODO: Some headers can have multiple values, allow for this
		string = string.replaceFirst(":", ":SPLIT:");
		
		String[] keyValue = string.split(":SPLIT:");
		
		if (keyValue[0].isEmpty() || keyValue[1].isEmpty()) throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
		if (keyValue[0].contains(" ")) throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
		
		HashMap<String, String> returnable = new HashMap<String, String>();
		returnable.put(keyValue[0], keyValue[1]);
		
		return returnable.entrySet()
						 .stream()
						 .findFirst()
						 .get();
	}

	private void parseBody(InputStreamReader reader, HttpRequest request) throws IOException {
		BufferedReader bufferedReader = new BufferedReader(reader);
		StringBuilder dataBuffer = new StringBuilder();
		
		int _byte;
				
		while ((_byte = bufferedReader.read()) >= 0) {
			dataBuffer.append((char) _byte);
		}
		request.setBody(dataBuffer.toString());
	}

}
