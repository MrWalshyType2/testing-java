package com.qa.app.http;

public enum HttpHeaders {

	// Accept
	STANDARD_REQUEST_ACCEPT("Accept", "The media type/s provided in the request for content negotiation of an appropriate response."),
	// Accept-Charset
	STANDARD_REQUEST_ACCEPT_CHARSET("Accept-Charset", "The accepted response character-set."),
	// Accept-Language
	STANDARD_REQUEST_ACCEPT_LANGUAGE("Accept-Language", "Accepts a list of acceptable languages for the response.."),
	// Access-Control-Request-Method
	STANDARD_REQUEST_ACCESS_CONTROL_REQUEST_METHOD("Access-Control-Request-Method", "Used with 'Access-Control-Request-Headers' to initiate requests for CORS with 'Origin'."),
	// Access-Control-Request-Headers
	STANDARD_REQUEST_ACCESS_CONTROL_REQUEST_HEADERS("Access-Control-Request-Headers", "Used by the browser when issuing a preflight request letting the server know which HTTP headers the client might send. Answered by the server side header 'Access-Control-Allow-Headers'."),
	// Authorization credentials
	STANDARD_REQUEST_AUTHORIZATION("Authorization", "Authentication credentials for HTTP authentication."),
	// Host
	STANDARD_REQUEST_HOST("Host", "Server domain name and TCP port on which the server is listening. The port may be omitted if it is a standard port for the service requested."),
	// Connection
	STANDARD_REQUEST_CONNECTION("Connection", "Control options for the current connection."),
	// User-Agent
	STANDARD_REQUEST_USER_AGENT("User-Agent", "The user agent string of the user agent."),
	// Accept-Encoding
	STANDARD_REQUEST_ACCEPT_ENCODING("Accept-Encoding", "List of acceptable encodings");
	
	public final String HEADER_NAME;
	public final String DESCRIPTION;
	
	private HttpHeaders(String headerName, String description) {
		HEADER_NAME = headerName;
		DESCRIPTION = description;
	}
}
