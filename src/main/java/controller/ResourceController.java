package controller;

import http.HttpRequest;
import http.HttpResponse;
import http.header.ResponseHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.HttpUtil;
import http.status.HttpStatus;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.*;

public class ResourceController {
    private static final Logger logger = LoggerFactory.getLogger(ResourceController.class);

    public static ResourceController getInstance() {
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
        private static final ResourceController INSTANCE = new ResourceController();
    }

    public byte[] getFileAsBody(String url) throws IOException {
        String contentType = HttpUtil.getContentTypeFromUrl(url);
        if (contentType.equals("text/html")) {
            return Files.readAllBytes(
                    new File("src/main/resources/templates" + url).toPath());
        }
        else {
            return Files.readAllBytes(
                    new File("src/main/resources/static" + url).toPath());
        }
    }

    public void handle(HttpRequest request, HttpResponse response) throws IOException {
        String contentType = HttpUtil.getContentTypeFromUrl(request.getUrl());
        byte[] body = getFileAsBody(request.getUrl());

        Map<String, String> properties = new TreeMap<>();
        properties.put("Content-Type", contentType);
        properties.put("Content-Length", String.valueOf(body.length));

        SimpleDateFormat format = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss z", Locale.ENGLISH);
        Date nowDate = new Date();

        properties.put("Date", format.format(nowDate));
        properties.put("Cache-Control", "public, max-age=60");

        response.setHeader(ResponseHeader.of(HttpStatus.OK, properties));
        response.setBody(body);
    }
}
