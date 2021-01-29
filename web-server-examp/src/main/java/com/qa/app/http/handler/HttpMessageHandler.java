package com.qa.app.http.handler;

import java.io.InputStream;
import java.io.OutputStream;

import com.qa.app.http.exception.HttpParsingException;
import com.qa.app.http.message.HttpMessage;
import com.qa.app.http.message.HttpRequest;
import com.qa.app.http.message.HttpResponse;

public interface HttpMessageHandler<T extends HttpRequest, O extends HttpResponse> {
	
	public O handle(InputStream in) throws HttpParsingException;
	public O handle(T in) throws HttpParsingException;
}