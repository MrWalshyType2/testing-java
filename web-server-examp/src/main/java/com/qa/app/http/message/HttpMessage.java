package com.qa.app.http.message;

import java.util.HashMap;

public abstract class HttpMessage {

	protected HashMap<String, String> headers;
	protected String body;
	
	public HashMap<String, String> getHeaders() {
		return headers;
	}
	
	public void setHeaders(HashMap<String, String> headers) {
		this.headers = headers;
	}
	
	public void addHeader(String key, String value) {
		headers.put(key, value);
	}
	
	public void removeHeader(String key) {
		headers.remove(key);
	}
	
	public String getBody() {
		return body;
	}
	
	public void setBody(String body) {
		this.body = body;
	}
	
}
