package com.qa.app.http.message;

import java.util.HashMap;

import com.qa.app.http.HttpMethod;
import com.qa.app.http.HttpStatusCode;
import com.qa.app.http.exception.HttpParsingException;

public class HttpRequest extends HttpMessage {

	private HttpMethod method;
	
	/**
	 * <p>The absolute path used to identify a targeted resource on some origin.</p>
	 * 
	 * <code>GET /api/notes HTTP/1.1</code>
	 */
	private String requestTarget;
	private String httpVersion;
	
	HttpRequest() {
		super();
		headers = new HashMap<String, String>();
	}
	
	HttpRequest(HttpRequestBuilder httpRequestBuilder) {
		this.startLine = httpRequestBuilder.startLine;
		this.headers = httpRequestBuilder.headers;
		this.body = httpRequestBuilder.body;
		this.method = httpRequestBuilder.method;
		this.requestTarget = httpRequestBuilder.requestTarget;
		this.httpVersion = httpRequestBuilder.httpVersion;
		}

	HttpRequest(String startLine, HashMap<String, String> headers, String body, HttpMethod method,
			String requestTarget, String httpVersion) {
		this.startLine = startLine;
		this.headers = headers;
		this.body = body;
		this.method = method;
		this.requestTarget = requestTarget;
		this.httpVersion = httpVersion;
	}

	public HttpMethod getMethod() {
		return method;
	}

	void setMethod(String method) throws HttpParsingException {
		for (HttpMethod httpMethod : HttpMethod.values()) {
			if (method.equals(httpMethod.name())) {
				this.method = httpMethod;
				return;
			}
		}
		throw new HttpParsingException(HttpStatusCode.SERVER_ERROR_501_NOT_IMPLEMENTED);
	}
	
	public String getRequestTarget() {
		return requestTarget;
	}
	
	void setRequestTarget(String target) {
		this.requestTarget = target;
	}

	public String getHttpVersion() {
		return httpVersion;
	}

	void setHttpVersion(String httpVersion) {
		this.httpVersion = httpVersion;
	}
	
	public HashMap<String, String> getHeaders() {
		return headers;
	}

	public void setMethod(HttpMethod method) {
		this.method = method;
	}
	
	public static class HttpRequestBuilder {
		
		private String startLine;
		private HashMap<String, String> headers;
		private String body;
		private HttpMethod method;
		private String requestTarget;
		private String httpVersion;
		
		public static HttpRequestBuilder newBuilder() {
			return new HttpRequestBuilder();
		}
		
		private HttpRequestBuilder() {
			
		}
		
		public HttpRequestBuilder startLine(String startLine) {
			this.startLine = startLine;
			return this;
		}
		
		public HttpRequestBuilder headers(HashMap<String, String> headers) {
			this.headers = headers;
			return this;
		}
		
		public HttpRequestBuilder body(String body) {
			this.body = body;
			return this;
		}
		
		public HttpRequestBuilder method(HttpMethod method) {
			this.method = method;
			return this;
		}
		
		public HttpRequestBuilder requestTarget(String requestTarget) {
			this.requestTarget = requestTarget;
			return this;
		}
		
		public HttpRequestBuilder httpVersion(String httpVersion) {
			this.httpVersion = httpVersion;
			return this;
		}
		
		public HttpRequest build() {
			return new HttpRequest(this);
		}
	}
}
