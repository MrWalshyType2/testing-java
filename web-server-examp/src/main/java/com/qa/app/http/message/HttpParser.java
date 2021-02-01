package com.qa.app.http.message;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qa.app.http.HttpHeaders;
import com.qa.app.http.HttpMethod;
import com.qa.app.http.HttpStatusCode;
import com.qa.app.http.HttpVersion;
import com.qa.app.http.exception.HttpParsingException;
import com.qa.app.http.message.HttpRequest.HttpRequestBuilder;

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
		HttpRequestBuilder httpRequestBuilder = HttpRequestBuilder.newBuilder();
		
		try {
			String requestLine = parseRequestLine(bufferedReader);
			HashMap<String, String> headers = parseHeaders(bufferedReader);
			String body = null;
			
			if (headers.containsKey(
							HttpHeaders.STANDARD_REQUEST_CONTENT_LENGTH.HEADER_NAME) ||
				headers.containsKey(
							HttpHeaders.STANDARD_REQUEST_TRANSFER_ENCODING.HEADER_NAME)) {
					body = parseBody(reader);
			}
			
			httpRequestBuilder.method(getMethod(requestLine))
							  .requestTarget(getRequestTarget(requestLine))
							  .httpVersion(getHttpVersion(requestLine))
							  .startLine(requestLine)
							  .headers(headers)
							  .body(body);				
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return httpRequestBuilder.build();
	}
	
	/**
	 * <p>Takes a BufferedReader returning the request line. The BufferedReader
	 * passed in will be used from its current location. </p>
	 * 
	 * <p><code>`validateRequestLine(String requestLine)`</code> is used to do some whitespace checks
	 * on the request-line ensuring it complies with RFC 7230 Section 3.1.1: </p>
	 * <ul><li>request-line   = method SP request-target SP HTTP-version CRLF</li></ul>
	 * <code>
	 * 	SP = SINGLE-SPACE // 32
	 *  CR = CARRIAGE-RETURN // 13
	 *  LF = LINE-FEED // 18
	 * </code>
	 * 
	 * <p>Use the following methods to check for RFC compliance on the output from this method: </p>
	 * <ul>
	 * 	<li><code>getMethod(String requestLine)</code></li>
	 *  <li><code>getRequestTarget(String requestLine)</code></li>
	 *  <li><code>getHttpVersion(String requestLine)</code></li>
	 * </ul>
	 * 
	 * @param reader
	 * @return String - representing the request line to be further validated
	 * @throws IOException
	 * @throws HttpParsingException
	 */
	private String parseRequestLine(BufferedReader reader) throws IOException, HttpParsingException {
		// Quick check for invalid request-line (specifically whitespace at the start)
		String requestLine = getRequestLine(reader);
		validateRequestLine(requestLine);
		LOGGER.debug("Parsed request-line: " + requestLine);
		return requestLine;
	}
	
	private String getRequestLine(BufferedReader reader) throws HttpParsingException, IOException {
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
		return reqLineBuffer.toString();
	}

	private void validateRequestLine(String requestLine) throws HttpParsingException {
		if (requestLine == null 		||
			requestLine.isEmpty()		||
			requestLine.charAt(0) == SP ||
			requestLine.split(" ").length > 3 ||
			requestLine.getBytes().length > 8000
			) {
			throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
		}
	}

	private String getHttpVersion(String requestLine) throws HttpParsingException {
		// [2] = http version
		List<String> requestLineList = Arrays.asList(requestLine.split(" "));
		String version = requestLineList.get(2);
		
		if (!version.equals(HttpVersion.HTTP1DOT1.DESCRIPTION)) {
			throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
		}
		
		return version;
	}

	private String getRequestTarget(String requestLine) throws HttpParsingException {
		String target = null;
		try {
			// [1] = request target
			List<String> requestLineList = Arrays.asList(requestLine.split(" "));
			target = requestLineList.get(1);
			
			// RFC 7230, Section 3.1.1 'A server that receives a request-target longer
			// than any URI it wishes to parse MUST response with a 414'. 
			if (target.getBytes().length > 6000) {
				throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_414_URI_TOO_LONG);
			}
			
			if (target.equals("/")) {
				target = "/Index";
			}
			// Throws IO if file doesn't exist
			FileInputStream in = new FileInputStream("src/main/resources/views" + target + ".html");
			in.close();
		} catch (IOException e) {
			throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
		}
		
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

	
	private HashMap<String, String> parseHeaders(BufferedReader bufferedReader) throws IOException, HttpParsingException {
//		InputStream data = new BufferedInputStream(reader.);
//		BufferedReader bufferedReader = new BufferedReader(reader);
		StringBuilder dataBuffer = new StringBuilder();
		HashMap<String, String> headers = new HashMap<String, String>();
		
		int _byte;
				
		while ((_byte = bufferedReader.read()) >= 0) {
			// CRLF check
			if (_byte == CR) {
				_byte = bufferedReader.read();
				
				if (_byte == LF) {
					bufferedReader.mark(2);
					if (bufferedReader.read() == CR && bufferedReader.read() == LF) {
						//bufferedReader.skip(dataBuffer.length());
						return headers;
					}
					bufferedReader.reset();
					//bufferedReader.skip(dataBuffer.length());
					
					Entry<String, String> header = parseHeader(dataBuffer.toString());
					dataBuffer.delete(0, dataBuffer.length());
					
					LOGGER.debug("Parsed header: " + header.getKey().trim() + ":" + header.getValue());
					
					headers.put(header.getKey().trim(), header.getValue());
				}
			}

			dataBuffer.append((char) _byte);
		} // EO-While
		throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
	}

	private Entry<String, String> parseHeader(String string) throws HttpParsingException {		
		// TODO: Some headers can have multiple values, allow for this
		string = string.replaceFirst(":", ":SPLIT:");
		
		// A sender MUST NOT send whitespace between the start-line and the
		// first header field. RFC 7230, Section 3. Either reject message or skip header.
		// The header value can have optional leading or trailing whitespace
		if (string.charAt(0) == SP) throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
		
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

	private String parseBody(InputStreamReader reader) throws IOException {
		BufferedReader bufferedReader = new BufferedReader(reader);
		StringBuilder dataBuffer = new StringBuilder();
		
		int _byte;
				
		while ((_byte = bufferedReader.read()) >= 0) {
			dataBuffer.append((char) _byte);
		}
		return dataBuffer.toString();
	}

}
