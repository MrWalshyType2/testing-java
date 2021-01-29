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
	private String body;
	
	private HashMap<String, String> headers;

	HttpRequest() {
		super();
		headers = new HashMap<String, String>();
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

	void setHeaders(HashMap<String, String> headers) {
		this.headers = headers;
	}
	
	void addHeader(String key, String value) {
		headers.put(key, value);
	}
	
	void removeHeader(String key) {
		headers.remove(key);
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public void setMethod(HttpMethod method) {
		this.method = method;
	}
}
