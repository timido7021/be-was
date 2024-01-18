package http.header;

import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class RequestHeader extends Header {
    private Map<String, String> properties;

    public RequestHeader(Map<String, String> properties) {
        this.properties = properties;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    @Override
    public String toString() {
        Set<String> requiredHeaders = new TreeSet<>();

        requiredHeaders.add("Host");
        requiredHeaders.add("Connection");
        requiredHeaders.add("Referer");
        requiredHeaders.add("User-Agent");
        requiredHeaders.add("Accept");
        requiredHeaders.add("Accept-Language");
        requiredHeaders.add("Accept-Encoding");

        return properties.entrySet().stream().map(e -> {
                    if (requiredHeaders.contains(e.getKey()))
                        return e.getKey() + ": " + e.getValue();
                    else return "";
                })
                .filter(e -> !e.isEmpty())
                .collect(Collectors.joining("|"));
    }
}
