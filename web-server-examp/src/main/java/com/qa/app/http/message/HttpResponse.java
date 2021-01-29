package com.qa.app.http.message;

import java.util.HashMap;

import com.qa.app.http.HttpStatusCode;
import com.qa.app.http.exception.HttpParsingException;

public class HttpResponse extends HttpMessage {

	private HttpStatusCode status;
	private HashMap<String, String> headers;
	private String body;
	
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
	
	public HashMap<String, String> getHeaders() {
		return headers;
	}
	
	public void setHeaders(HashMap<String, String> headers) {
		this.headers = headers;
	}
	
	public String getBody() {
		return body;
	}
	
	public void setBody(String body) {
		this.body = body;
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
}
