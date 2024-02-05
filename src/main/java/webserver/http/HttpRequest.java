package webserver.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpRequest {
    private final String url;
    private final String method;
    private Map<String, String> queryString;
    private RequestHeader header;

    private RequestBody body;

    private HttpRequest(String method, String url, Map<String, String> queryString, RequestHeader header, RequestBody body) {
        this.method = method;
        this.url = url;
        this.queryString = queryString;
        this.header = header;
        this.body = body;
    }

    public static HttpRequest createFromReader(BufferedReader reader) throws IOException {
        String request = reader.readLine().trim();
        String[] httpRequestTokens = request.split(" ");

        String method = httpRequestTokens[0];
        String url = httpRequestTokens[1];
        Map<String, String> queryString = new HashMap<>();

        String[] url_split = url.split("\\?");

        url = url_split[0];

        if (url_split.length > 1) {
            String[] queryStringArray = url_split[1].split("&");

            for (String queryKeyValue : queryStringArray) {
                String[] query_split = queryKeyValue.split("=");
                if (query_split.length != 2 || query_split[1].isBlank())
                    throw new IOException("Incorrect Query String");

                query_split[1] = URLDecoder.decode(query_split[1], "UTF-8");
                queryString.put(query_split[0], query_split[1]);
            }
        }

        RequestHeader header = RequestHeader.createFromReader(reader);

        RequestBody body = RequestBody.createEmptyBody();

        if (!header.getHeaderProperty("Content-Length").isBlank()) {
            int contentLength = Integer.parseInt(header.getHeaderProperty("Content-Length"));
            body = RequestBody.createFromReader(reader, contentLength);
        }

        return new HttpRequest(method, url, queryString, header, body);
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
    public RequestBody getBody() {
        return body;
    }

    public String getHeaderProperty(String key) {
        return header.getHeaderProperty(key);
    }

    public Map<String, String> getCookie() throws IOException {
        String cookie = header.getHeaderProperty("Cookie");
        if (cookie.isBlank())
            return Map.of();

        Map<String, String> result = new HashMap<>();
        String[] propertyString = cookie.split(";");

        for (String property : propertyString) {
            String[] property_split = property.split("=");

            if (property_split.length != 2 || property_split[1].isBlank())
                continue;
            property_split[1] = URLDecoder.decode(property_split[1], "UTF-8");
            result.put(property_split[0].trim(), property_split[1].trim());
        }

        return result;
    }

    @Override
    public String toString() {
        String query = queryString.entrySet().stream()
                .map(e -> e.getKey() + ":" + e.getValue())
                .collect(Collectors.joining(" | "));

        return "HttpRequest(method = " + method + ",url = " +
                url + ", queryString = {" + query +
                "}, header = {" + header.toString() + "})";
    }
}
