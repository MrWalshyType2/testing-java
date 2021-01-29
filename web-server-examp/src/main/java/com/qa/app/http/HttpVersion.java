package com.qa.app.http;

public enum HttpVersion {

	HTTP1DOT1("HTTP/1.1");
	
	public final String DESCRIPTION;
	
	private HttpVersion(String DESCRIPTION) {
		this.DESCRIPTION = DESCRIPTION;
	}
}
