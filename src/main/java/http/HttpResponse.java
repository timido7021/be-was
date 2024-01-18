package http;

import http.header.ResponseHeader;
import http.status.HttpStatus;

import java.util.Map;

public class HttpResponse {
    private ResponseHeader header;
    private byte[] body;

    private HttpResponse(ResponseHeader header, byte[] body) {
        this.header = header;
        this.body = body;
    }

    public static HttpResponse emptyBodyResponse(HttpStatus status, Map<String, String> properties) {
        return new HttpResponse(new ResponseHeader(status, properties), "".getBytes());
    }

    public static HttpResponse of(HttpStatus status, byte[] body, Map<String, String> properties){
        return new HttpResponse(new ResponseHeader(status, properties), body);
    }

    public ResponseHeader getHeader() {
        return header;
    }

    public byte[] getBody() {
        return body;
    }
}
