package model;

import java.util.Map;

public class HttpRequest {
    private String url;
    private String method;
    private Map<String, String> pathVariables;

    public HttpRequest(String method, String url, Map<String, String> pathVariables) {
        this.method = method;
        this.url = url;
        this.pathVariables = pathVariables;
    }

    public String getUrl() {
        return url;
    }

    public String getMethod() {
        return method;
    }

    public Map<String, String> getPathVariables() {
        return pathVariables;
    }
}
