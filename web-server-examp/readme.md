# Web Server Example

## Logback
### Specify config file as a sys prop
```
java -Dlogback.configurationFile=/src/main/resources/logback.xml com.qa.app.Runner
```

## RFC
- RFC 7230: (HTTP/1.1): Message Syntax and Routing
- RFC 7231: (HTTP/1.1): Semantics and Content
- RFC 7232: (HTTP/1.1): Conditional Requests
- RFC 7233: (HTTP/1.1): Range Requests
- RFC 7234: (HTTP/1.1): Caching
- RFC 7235: (HTTP/1.1): Authentication

## Example Request
Request messaging syntax and routing relies upon the RFC 7230 standard.

```
ET / HTTP/1.1
Host: localhost:8080
Connection: keep-alive
Upgrade-Insecure-Requests: 1
User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/88.0.4324.96 Safari/537.36 Edg/88.0.705.50
Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9
Sec-Fetch-Site: none
Sec-Fetch-Mode: navigate
Sec-Fetch-User: ?1
Sec-Fetch-Dest: document
Accept-Encoding: gzip, deflate, br
Accept-Language: en-GB,en;q=0.9,en-US;q=0.8
```

### RFC 7230 Message Request Format
```
HTTP-message   = start-line
                 *( header-field CRLF )
                 CRLF
                 [ message-body ]
```

The *start-line* of a HTTP message can be a **request-line** for indicating the message is a *request*, or a **status-line** which indicates the message is a *response*.

A **request-line** takes the following format:

```
request-line = method SP request-target SP HTTP-version CRLF
```
where *method* is a token, followed by a single space (SP), the request-target, another SP, the protocol version, ending with a CRLF.

The *method token* indicates the request method to be performed and is case-sensitive. Tokens types can be viewed in RFC 7231 Section 4.

### RFC 7231 Semantics and Content
#### Section 4
Section 4 states *All general-purpose servers MUST support the methods GET and HEAD. All other methods are OPTIONAL.*. Section 4 lists the following methods:
- GET
- HEAD
- POST
- PUT
- DELETE
- CONNECT
- OPTIONS
- TRACE

Section 4 further states in regard to methods *When a request method is received that is unrecognized or not implemented by an origin server, the origin server SHOULD respond with the 501 (Not Implemented) status code.  When a request method is received that is known by an origin server but not allowed for the target resource, the origin server SHOULD respond with the 405 (Method Not Allowed) status code.*

A set of allowed methods can be listed in an *Allow header field* from the target resource.

Section 4 further states *Recipients of an invalid request-line SHOULD respond with either a 400 (Bad Request) error or a 301 (Moved Permanently) redirect with the request-target properly encoded.*

Section 4 further states *A server that receives request-target longer than any URI it wishes to parse MUST respond with a 414 (URI Too Long) status code*.