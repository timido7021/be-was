package http;

import http.header.RequestHeader;

import java.util.Map;
import java.util.stream.Collectors;

public class HttpRequest {
    private String url;
    private String method;
    private Map<String, String> queryString;

    private RequestHeader requestHeader;

    public HttpRequest(String method, String url, Map<String, String> queryString, RequestHeader requestHeader) {
        this.method = method;
        this.url = url;
        this.queryString = queryString;
        this.requestHeader = requestHeader;
    }

    public String getUrl() {
        return url;
    }

    public String getMethod() {
        return method;
    }

    public Map<String, String> getQueryString() {
        return queryString;
    }

    public RequestHeader getRequestHeader() {
        return requestHeader;
    }

    @Override
    public String toString() {
        String query = queryString.entrySet().stream()
                .map(e -> e.getKey() + ":" + e.getValue())
                .collect(Collectors.joining(" | "));

        return "HttpRequest(method = " + method + ",url = " +
                url + ", queryString = {" + query +
                "}, header = {" + requestHeader.toString() + "})";
    }
}
