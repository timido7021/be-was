package controller;

import http.HttpRequest;
import http.HttpResponse;
import http.header.ResponseHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.DateTimeUtil;
import utils.HttpUtil;
import http.status.HttpStatus;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

public class StaticResourceController {
    private static final Logger logger = LoggerFactory.getLogger(StaticResourceController.class);

    public static StaticResourceController getInstance() {
        return LazyHolder.INSTANCE;
    }

    public static List<String> extensions = List.of(
            ".html",
            ".css",
            ".js",
            ".ttf",
            ".woff",
            ".png",
            ".ico"
    );

    private static class LazyHolder {
        private static final StaticResourceController INSTANCE = new StaticResourceController();
    }

    public File getFileFromUrl(String url) throws IOException {
        String contentType = HttpUtil.getContentTypeFromUrl(url);
        if (contentType.equals("text/html")) {
            return new File("src/main/resources/templates" + url);
        } else {
            return new File("src/main/resources/static" + url);
        }
    }

    public void handle(HttpRequest request, HttpResponse response) throws IOException {
        String contentType = HttpUtil.getContentTypeFromUrl(request.getUrl());
        File file = getFileFromUrl(request.getUrl());
        byte[] body = Files.readAllBytes(file.toPath());

        Map<String, String> properties = new TreeMap<>();
        properties.put("Content-Type", contentType);
        properties.put("Content-Length", String.valueOf(body.length));

        LocalDateTime nowDate = LocalDateTime.now();
        LocalDateTime lastModified = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(file.lastModified()), ZoneId.systemDefault()
        );

        properties.put("Date", DateTimeUtil.getGMTDateString(nowDate));
        properties.put("Last-Modified", DateTimeUtil.getGMTDateString(lastModified));

        response.setHeader(ResponseHeader.of(HttpStatus.OK, properties));
        response.setBody(body);
    }
}
