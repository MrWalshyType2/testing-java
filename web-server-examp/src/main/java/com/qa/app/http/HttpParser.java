package com.qa.app.http;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpParser {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(HttpParser.class);
	
	private static final int SP = 0x20; // hex for 32/space
	private static final int CR = 0x0D; // hex for 13/carriage-return - moves cursor to beginning of line
	private static final int LF = 0x0A; // hex for 18/line-feed - moves cursor down one line
	private static final int COLON = 0x3A;
	
	public HttpRequest parseHttpRequest(InputStream in) throws HttpParsingException {
		// reads bytes from a string and decodes to chars
		InputStreamReader reader = new InputStreamReader(in, StandardCharsets.US_ASCII);

		HttpRequest request = new HttpRequest();
		try {
			parseRequestLine(reader, request);
			parseHeaders(reader, request);
			
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
	
	private void parseRequestLine(InputStreamReader reader, HttpRequest request) throws IOException, HttpParsingException {
		StringBuilder dataBuffer = new StringBuilder();
		int _byte;
		
		boolean methodParsed = false;
		boolean requestTargetParsed = false;
		
		while ((_byte = reader.read()) >= 0) {
			// if (this and the next byte == CRLF)
			if (_byte == CR) {
				_byte = reader.read();
				
				if (_byte == LF) {
					LOGGER.debug("Request-line VERSION to Process : {}", dataBuffer.toString());
					request.setHttpVersion(dataBuffer.toString());
					
					if (!methodParsed || !requestTargetParsed) {
						throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
					}
					
					return;
				}
			}
			
			if (_byte == SP) {
				// Process previous data
				if (!methodParsed) {
					LOGGER.debug("Request-line METHOD to Process : {}", dataBuffer.toString());
					request.setMethod(dataBuffer.toString());
					methodParsed = true;
				} else if (!requestTargetParsed) {
					LOGGER.debug("Request-line REQUEST TARGET to Process : {}", dataBuffer.toString());
					request.setRequestTarget(dataBuffer.toString());
					requestTargetParsed = true;
				} else {
					throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
				}
				
				dataBuffer.delete(0, dataBuffer.length());
			} else {
				dataBuffer.append((char) _byte);
				
				// if the method has not been parsed
				if (!methodParsed) {
					if (dataBuffer.length() > HttpMethod.MAX_LENGTH) {
						throw new HttpParsingException(HttpStatusCode.SERVER_ERROR_501_NOT_IMPLEMENTED);
					}
				}
			}
		}
	}
	
	private void parseHeaders(InputStreamReader reader, HttpRequest request) throws IOException, HttpParsingException {
//		InputStream data = new BufferedInputStream(reader.);
		BufferedReader bufferedReader = new BufferedReader(reader);
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
						bufferedReader.skip(dataBuffer.length());
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
	}

}
