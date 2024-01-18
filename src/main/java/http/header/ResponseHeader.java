package http.header;

import http.status.HttpStatus;

import java.util.Map;
import java.util.stream.Collectors;

public class ResponseHeader extends Header {
    private HttpStatus status;
    private Map<String, String> properties;

    public ResponseHeader(HttpStatus status, Map<String, String> properties) {
        this.status = status;
        this.properties = properties;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getProperty(String property) throws RuntimeException {
        String result = properties.getOrDefault(property, null);
        if (result == null)
            throw new RuntimeException("No Such Property in ResponseHeader");
        return result;
    }

    @Override
    public String toString() throws RuntimeException {
        String result = String.format("HTTP/1.1 %d %s\r\n", status.getCode(), status.getMessage());

        result += properties.entrySet().stream()
                .map(e -> e.getKey() + ": " + e.getValue())
                .collect(Collectors.joining("\r\n"));

        result += "\r\n\r\n";
        return result;
    }
}
