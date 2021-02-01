package com.qa.app.http.handler;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

import com.qa.app.http.HttpMethod;
import com.qa.app.http.HttpStatusCode;
import com.qa.app.http.exception.HttpParsingException;
import com.qa.app.http.filter.FilterChain;
import com.qa.app.http.message.HttpParser;
import com.qa.app.http.message.HttpRequest;
import com.qa.app.http.message.HttpResponse;
import com.qa.app.http.message.HttpResponse.HttpResponseBuilder;

public class HttpMessageHandlerImpl implements HttpMessageHandler<HttpRequest, HttpResponse> {
	
	private HttpParser parser;
	private FilterChain filterChain;
	
	public HttpMessageHandlerImpl(HttpParser parser) {
		this.parser = parser;
	}

	@Override
	public HttpResponse handle(InputStream in) throws HttpParsingException {
		HttpRequest request = parser.parseHttpRequest(in);
		return handle(request);
	}

	@Override
	public HttpResponse handle(HttpRequest in) throws HttpParsingException {
		HttpResponseBuilder response = HttpResponse.HttpResponseBuilder.newBuilder();
		HashMap<String, String> headers = new HashMap<String, String>();
		int bodyLength = 0;
		String bodyContents = null;
		
		if (in.getMethod() == HttpMethod.GET) {
			bodyContents = getViewContentsOrError(in.getRequestTarget());
			bodyLength = bodyContents.getBytes().length;
		}
		
		if (bodyLength > 0 && !bodyContents.isEmpty()) {
			headers.put("Content-Length", Integer.toString(bodyLength));
		}
		
		response.status(HttpStatusCode.SUCCESS_200_OK);
		response.headers(headers);
		response.body(bodyContents);
		return response.build();
	}

	private String getViewContentsOrError(String requestTarget) {
		FileInputStream in = null;
		
		try {
			List<String> requestArgs = Arrays.asList(requestTarget.split("/"));
			List<String> folders = null;
			
			requestTarget = parseRequestTarget(requestTarget);
			
			in = new FileInputStream("src/main/resources/views" + requestTarget + ".html");
			StringBuilder builder = new StringBuilder();
			int _byte;
			
			while ((_byte = in.read()) != -1) {
				builder.append((char) _byte);
			}
			in.close();
			
			return builder.toString();
			
		} catch (Exception e) {
			return "<html><head><title>Page Not Found</title></head><body><h1>404 - Page Not Found</h1></body></html>";
		}
	}

	private String parseRequestTarget(String requestTarget) {
		if (requestTarget.isEmpty() || 
				requestTarget.equalsIgnoreCase("/") ||
				requestTarget.equalsIgnoreCase("/index")) {
				return "/Index";
		}
		return requestTarget;
	}


}
