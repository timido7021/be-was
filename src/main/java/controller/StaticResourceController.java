package controller;

import http.HttpRequest;
import http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.DateTimeUtil;
import utils.FileUtil;
import http.status.HttpStatus;

import java.io.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class StaticResourceController {
    private static final Logger logger = LoggerFactory.getLogger(StaticResourceController.class);

    public static StaticResourceController getInstance() {
        return LazyHolder.INSTANCE;
    }

    private static class LazyHolder {
        private static final StaticResourceController INSTANCE = new StaticResourceController();
    }

    public void handle(HttpRequest request, HttpResponse response) throws IOException {
        File file = FileUtil.getFileFromUrl(request.getUrl());
        String contentType = FileUtil.getContentType(request.getUrl());

        if (!file.exists()) {
            response.setStatusCode(HttpStatus.NOT_FOUND);
            return;
        }

        byte[] body = FileUtil.readFile(file);

        LocalDateTime nowDate = LocalDateTime.now();
        LocalDateTime lastModified = LocalDateTime.ofInstant(
                Instant.ofEpochSecond(
                        Instant.ofEpochMilli(file.lastModified())
                                .getEpochSecond()
                ),
                ZoneId.systemDefault()
        );

        response.addHeaderProperty("Content-Type", contentType);
        response.addHeaderProperty("Cache-Control", "public, max-age=30");
        response.addHeaderProperty("Content-Length", String.valueOf(body.length));
        response.addHeaderProperty("Date", DateTimeUtil.getGMTDateString(nowDate));
        response.addHeaderProperty("Last-Modified", DateTimeUtil.getGMTDateString(lastModified));

        String modifiedSince = request.getHeaderProperty("If-Modified-Since");

        if (!modifiedSince.isBlank()) {
            try {
                if (DateTimeUtil.parseGMTDateString(modifiedSince)
                        .isEqual(lastModified)
                ) {
                    response.setStatusCode(HttpStatus.NOT_MODIFIED);
                    return;
                }
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }

        response.setStatusCode(HttpStatus.OK);
        response.setBody(body);
    }
}
