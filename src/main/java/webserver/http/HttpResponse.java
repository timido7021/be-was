package webserver.http;

import controller.util.FileUtil;

import java.io.File;
import java.io.IOException;

public class HttpResponse {
    private static byte[] notFoundBody;

    static {
        try {
            notFoundBody = FileUtil.readFile(new File("src/main/resources/templates/404.html"));
        } catch (IOException e) {
            notFoundBody = "".getBytes();
        }
    }

    private ResponseHeader header;
    private byte[] body;

    private HttpResponse(ResponseHeader header, byte[] body) {
        this.header = header;
        this.body = body;
    }

    public static HttpResponse of() {
        return new HttpResponse(ResponseHeader.of(), "".getBytes());
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    public void set404Body() {
        addHeaderProperty("Content-Type", "text/html");
        this.body = notFoundBody;
    }

    public HttpStatus getStatusCode() {
        return header.getStatus();
    }

    public void setStatusCode(HttpStatus status) {
        header.setStatus(status);
    }

    public String getHeaderProperty(String key) {
        return header.getProperty(key);
    }

    public void addHeaderProperty(String key, String value) {
        header.addProperty(key, value);
    }

    public String getHeaderAsString() {
        return header.toString();
    }

}
