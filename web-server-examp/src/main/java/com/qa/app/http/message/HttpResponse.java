package com.qa.app.http.message;

import java.util.HashMap;

import com.qa.app.http.HttpStatusCode;
import com.qa.app.http.exception.HttpParsingException;

public class HttpResponse extends HttpMessage {

	private HttpStatusCode status;
	
	public HttpResponse() {
		
	}
	
	public HttpResponse(HttpResponseBuilder builder) {
		this.startLine = builder.startLine;
		this.headers = builder.headers;
		this.body = builder.body;
		this.status = builder.status;
	}
	
	public HttpResponse(String startLine, HashMap<String, String> headers, String body, HttpStatusCode status) {
		this.startLine = startLine;
		this.headers = headers;
		this.body = body;
		this.status = status;
	}

	public HttpStatusCode getStatus() {
		return status;
	}
	
	public void setStatus(HttpStatusCode status) {
		this.status = status;
	}
	
	public void setStatus(int status) throws HttpParsingException {
		for (HttpStatusCode code : HttpStatusCode.values()) {
			if (code.STATUS_CODE == status) {
				this.status = code;
			}
		}
		throw new HttpParsingException(HttpStatusCode.SERVER_ERROR_501_NOT_IMPLEMENTED);
	}
	
	public String build() {
		final String CRLF = "\r\n";
		
		StringBuilder response = new StringBuilder();
		
		// Status: HTTP_VERSION RESPONSE_CODE RESPONSE_MESSAGE
		response.append("HTTP/1.1 200 OK");
		response.append(CRLF);
		
		// Headers
		response.append(headers);
		response.append(CRLF);
		response.append(CRLF);
		
		if (!body.isEmpty()) {
			response.append(body);
			response.append(CRLF);
			response.append(CRLF);
		}
		return response.toString();
	}
	
	public static class HttpResponseBuilder {
		
		private String startLine;
		private HashMap<String, String> headers;
		private String body;
		private HttpStatusCode status;
		
		public static HttpResponseBuilder newBuilder() {
			return new HttpResponseBuilder();
		}
		
		private HttpResponseBuilder() {
			
		}
		
		public HttpResponseBuilder startLine(String startLine) {
			this.startLine = startLine;
			return this;
		}
		
		public HttpResponseBuilder headers(HashMap<String, String> headers) {
			this.headers = headers;
			return this;
		}
		
		public HttpResponseBuilder body(String body) {
			this.body = body;
			return this;
		}
		
		public HttpResponseBuilder status(HttpStatusCode status) {
			this.status = status;
			return this;
		}
		
		public HttpResponse build() {
			return new HttpResponse(this);
		}
	}
}
