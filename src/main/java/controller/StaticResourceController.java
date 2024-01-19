package controller;

import http.HttpRequest;
import http.HttpResponse;
import http.header.ResponseHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.DateTimeUtil;
import utils.FileUtil;
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

    public void handle(HttpRequest request, HttpResponse response) throws IOException {
        File file = FileUtil.getFileFromUrl(request.getUrl());
        String contentType = FileUtil.getContentType(file.toPath());

        byte[] body = Files.readAllBytes(file.toPath());

        LocalDateTime nowDate = LocalDateTime.now();
        LocalDateTime lastModified = LocalDateTime.ofInstant(
                Instant.ofEpochSecond(
                        Instant.ofEpochMilli(file.lastModified())
                                .getEpochSecond()
                ),
                ZoneId.systemDefault()
        );


        Map<String, String> properties = new HashMap<>();

        properties.put("Content-Type", contentType);
        properties.put("Cache-Control", "public, max-age=30");
        properties.put("Content-Length", String.valueOf(body.length));
        properties.put("Date", DateTimeUtil.getGMTDateString(nowDate));
        properties.put("Last-Modified", DateTimeUtil.getGMTDateString(lastModified));


        String modifiedSince = request.getRequestHeader()
                .getProperties()
                .getOrDefault("If-Modified-Since", "");

        if (!modifiedSince.isBlank()) {
            try {
                if (DateTimeUtil.parseGMTDateString(modifiedSince)
                        .isEqual(lastModified)
                ) {
                    response.setHeader(
                            ResponseHeader.of(HttpStatus.NOT_MODIFIED, properties)
                    );
                    response.setEmptyBody();
                    return;
                }
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }

        response.setHeader(ResponseHeader.of(HttpStatus.OK, properties));
        response.setBody(body);
    }
}
