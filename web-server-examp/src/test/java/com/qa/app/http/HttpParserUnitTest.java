package com.qa.app.http;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class HttpParserUnitTest {

	private HttpParser httpParser;
	
	@BeforeAll
	public void init() {
		httpParser = new HttpParser();
	}
	
	@Test
	void parseHttpRequestValidGetMethodTest() {
		HttpRequest request = null;
		try {
			request = httpParser.parseHttpRequest(generateValidGetTestCase());
		} catch (HttpParsingException e) {
			fail(e);
		}
		
		assertEquals(request.getMethod(), HttpMethod.GET);
	}
	
	@Test
	void parseHttpRequestBadGetMethodTest() {
		try {
			HttpRequest request = httpParser.parseHttpRequest(generateBadGetTestCase());
			fail();
		} catch (HttpParsingException e) {
			assertEquals(e.getErrorCode(), HttpStatusCode.SERVER_ERROR_501_NOT_IMPLEMENTED);
		}	
	}
	
	@Test
	void parseHttpRequestBadGetMethodWithTooLongMethodTest() {
		try {
			HttpRequest request = httpParser.parseHttpRequest(generateBadGetTestCaseWithTooLongMethod());
			fail();
		} catch (HttpParsingException e) {
			assertEquals(e.getErrorCode(), HttpStatusCode.SERVER_ERROR_501_NOT_IMPLEMENTED);
		}	
	}
	
	@Test
	void parseHttpRequestBadGetMethodRequestLineWithInvalidNumOfItemsTest() {
		try {
			HttpRequest request = httpParser.parseHttpRequest(generateBadGetTestCaseRequestLineWithInvalidNumOfItems());
			fail();
		} catch (HttpParsingException e) {
			assertEquals(e.getErrorCode(), HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
		}	
	}
	
	@Test
	void parseHttpRequestBadGetMethodWithEmptyRequestLine() {
		try {
			HttpRequest request = httpParser.parseHttpRequest(generateBadGetTestCaseWithEmptyRequestLine());
			fail();
		} catch (HttpParsingException e) {
			assertEquals(e.getErrorCode(), HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
		}	
	}
	
	@Test
	void parseHttpRequestBadGetMethodWithRequestLineOnlyCR() {
		try {
			HttpRequest request = httpParser.parseHttpRequest(generateBadGetTestCaseWithRequestLineOnlyCR());
			fail();
		} catch (HttpParsingException e) {
			assertEquals(e.getErrorCode(), HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
		}	
	}
	
	@Test
	void parseHttpRequestGetWithMalformedHeaders() {
		try {
			HttpRequest request = httpParser.parseHttpRequest(generateValidGetTestCaseWithMalformedHeaders());
			fail();
		} catch (HttpParsingException e) {
			assertEquals(e.getErrorCode(), HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
		}	
	}
	
	private InputStream generateValidGetTestCase() {
		String rawData = "GET /api/notes HTTP/1.1\r\n" + 
				"Host: localhost:8080\r\n" + 
				"Connection: keep-alive\r\n" + 
				"Upgrade-Insecure-Requests: 1\r\n" + 
				"User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/88.0.4324.96 Safari/537.36 Edg/88.0.705.50\r\n" + 
				"Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9\r\n" + 
				"Sec-Fetch-Site: none\r\n" + 
				"Sec-Fetch-Mode: navigate\r\n" + 
				"Sec-Fetch-User: ?1\r\n" + 
				"Sec-Fetch-Dest: document\r\n" + 
				"Accept-Encoding: gzip, deflate, br\r\n" + 
				"Accept-Language: en-GB,en;q=0.9,en-US;q=0.8\r\n\r\n";
		
		InputStream in = new ByteArrayInputStream(rawData.getBytes(
												  StandardCharsets.US_ASCII));
		return in;
	}
	
	private InputStream generateBadGetTestCase() {
		String rawData = "gEt / HTP/1.1\r\n" + 
				"Host: localhost:8080\r\n" + 
				"Connection: keep-alive\r\n" + 
				"Upgrade-Insecure-Requests: 1\r\n" + 
				"User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/88.0.4324.96 Safari/537.36 Edg/88.0.705.50\r\n" + 
				"Sec-Fetch-Mode: navigate\r\n" + 
				"Sec-Fetch-User: ?1\r\n" + 
				"Sec-Fetch-Dest: document\r\n" + 
				"Accept-Encoding: gzip, deflate, br\r\n" + 
				"Accept-Language: en-GB,en;q=0.9,en-US;q=0.8\r\n\r\n";
		
		InputStream in = new ByteArrayInputStream(rawData.getBytes(
												  StandardCharsets.US_ASCII));
		return in;
	}
	
	private InputStream generateBadGetTestCaseWithTooLongMethod() {
		String rawData = "GETTTTT / HTP/1.1\r\n" + 
				"Host: localhost:8080\r\n" + 
				"Connection: keep-alive\r\n" + 
				"Upgrade-Insecure-Requests: 1\r\n" + 
				"User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/88.0.4324.96 Safari/537.36 Edg/88.0.705.50\r\n" + 
				"Sec-Fetch-Mode: navigate\r\n" + 
				"Sec-Fetch-User: ?1\r\n" + 
				"Sec-Fetch-Dest: document\r\n" + 
				"Accept-Encoding: gzip, deflate, br\r\n" + 
				"Accept-Language: en-GB,en;q=0.9,en-US;q=0.8\r\n\r\n";
		
		InputStream in = new ByteArrayInputStream(rawData.getBytes(
												  StandardCharsets.US_ASCII));
		return in;
	}
	
	private InputStream generateBadGetTestCaseRequestLineWithInvalidNumOfItems() {
		String rawData = "GET / BlahBlah / HTP/1.1\r\n" + 
				"Host: localhost:8080\r\n" + 
				"Connection: keep-alive\r\n" + 
				"Upgrade-Insecure-Requests: 1\r\n" + 
				"User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/88.0.4324.96 Safari/537.36 Edg/88.0.705.50\r\n" + 
				"Sec-Fetch-Mode: navigate\r\n" + 
				"Sec-Fetch-User: ?1\r\n" + 
				"Sec-Fetch-Dest: document\r\n" + 
				"Accept-Encoding: gzip, deflate, br\r\n" + 
				"Accept-Language: en-GB,en;q=0.9,en-US;q=0.8\r\n\r\n";
		
		InputStream in = new ByteArrayInputStream(rawData.getBytes(
												  StandardCharsets.US_ASCII));
		return in;
	}
	
	private InputStream generateBadGetTestCaseWithEmptyRequestLine() {
		String rawData = "\r\n" + 
				"Host: localhost:8080\r\n" + 
				"Connection: keep-alive\r\n" + 
				"Upgrade-Insecure-Requests: 1\r\n" + 
				"User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/88.0.4324.96 Safari/537.36 Edg/88.0.705.50\r\n" + 
				"Sec-Fetch-Mode: navigate\r\n" + 
				"Sec-Fetch-User: ?1\r\n" + 
				"Sec-Fetch-Dest: document\r\n" + 
				"Accept-Encoding: gzip, deflate, br\r\n" + 
				"Accept-Language: en-GB,en;q=0.9,en-US;q=0.8\r\n\r\n";
		
		InputStream in = new ByteArrayInputStream(rawData.getBytes(
												  StandardCharsets.US_ASCII));
		return in;
	}
	
	private InputStream generateBadGetTestCaseWithRequestLineOnlyCR() {
		String rawData = "GET / HTTP/1.1\r" + 
				"Host: localhost:8080\r\n" + 
				"Connection: keep-alive\r\n" + 
				"Upgrade-Insecure-Requests: 1\r\n" + 
				"User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/88.0.4324.96 Safari/537.36 Edg/88.0.705.50\r\n" + 
				"Sec-Fetch-Mode: navigate\r\n" + 
				"Sec-Fetch-User: ?1\r\n" + 
				"Sec-Fetch-Dest: document\r\n" + 
				"Accept-Encoding: gzip, deflate, br\r\n" + 
				"Accept-Language: en-GB,en;q=0.9,en-US;q=0.8\r\n\r\n";
		
		InputStream in = new ByteArrayInputStream(rawData.getBytes(
												  StandardCharsets.US_ASCII));
		return in;
	}
	
	private InputStream generateValidGetTestCaseWithMalformedHeaders() {
		String rawData = "GET /api/notes HTTP/1.1\r\n" + 
				" Host: localhost:8080\r\n" + 
				"Connection: keep-alive\r\n" + 
				"Upgrade-Insecure-Requests: 1\r\n" + 
				"User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/88.0.4324.96 Safari/537.36 Edg/88.0.705.50\r\n" + 
				"Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9\r\n" + 
				"Sec-Fetch-Site: none\r\n" + 
				"Sec-Fetch-Mode: navigate\r\n" + 
				"Sec-Fetch-User: ?1\r\n" + 
				"Sec-Fetch-Dest: document\r\n" + 
				"Accept-Encoding: gzip, deflate, br\r\n" + 
				"Accept-Language: en-GB,en;q=0.9,en-US;q=0.8\r\n\r\n";
		
		InputStream in = new ByteArrayInputStream(rawData.getBytes(
												  StandardCharsets.US_ASCII));
		return in;
	}
}
