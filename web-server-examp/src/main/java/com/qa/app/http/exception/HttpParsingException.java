package com.qa.app.http.exception;

import com.qa.app.http.HttpStatusCode;

public class HttpParsingException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6094285473912819280L;

	private final HttpStatusCode errorCode;

	public HttpParsingException(HttpStatusCode errorCode) {
		super(errorCode.MESSAGE);
		this.errorCode = errorCode;
	}

	public HttpStatusCode getErrorCode() {
		return errorCode;
	}
}
