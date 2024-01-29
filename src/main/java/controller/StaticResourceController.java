package controller;

import annotations.GetMapping;
import model.Qna;
import service.QnaService;
import webserver.http.HttpRequest;
import webserver.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import controller.util.DateTimeUtil;
import controller.util.FileUtil;
import webserver.http.HttpStatus;

import java.io.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class StaticResourceController {
    private static final Logger logger = LoggerFactory.getLogger(StaticResourceController.class);

    public static StaticResourceController getInstance() {
        return LazyHolder.INSTANCE;
    }

    private static class LazyHolder {
        private static final StaticResourceController INSTANCE = new StaticResourceController();
    }

    @GetMapping(route = "/index.html")
    public void index(HttpRequest request, HttpResponse response) throws IOException {
        StringBuilder htmlBuilder = new StringBuilder();
        byte[] indexTemplate = FileUtil.readFile(new File("src/main/resources/templates/index.html"));

        String template = new String(indexTemplate);

        String beforeQnaList = template.substring(0, template.indexOf("{{qna_list}}"));
        String afterQnaList = template.substring(template.indexOf("{{qna_list}}") + "{{qna_list}}".length());

        QnaService qnaService = QnaService.getInstance();
        List<Qna> qnaList = qnaService.listAll();

        htmlBuilder.append(beforeQnaList);

        if (qnaList.isEmpty()) {
            htmlBuilder.append("<li>현재 게시글이 없습니다.</li>");
        } else {

            for (Qna qna : qnaList) {
                String createdAt = qna.getCreatedAtAsString();
                htmlBuilder.append("<li><div class=\"wrap\">\n")
                        .append("<div class=\"main\">\n")
                        .append("<strong class=\"subject\">\n")
                        .append("<a href=\"./qna/show?qnaId=").append(qna.getQnaId()).append("\">")
                        .append(qna.getTitle()).append("</a>\n")
                        .append("</strong>\n")
                        .append("<div class=\"auth-info\">\n")
                        .append("<i class=\"icon-add-comment\"></i>\n")
                        .append("<span class=\"time\">").append(createdAt).append("</span>\n")
                        .append("<a href=\"./user/profile.html\" class=\"author\">").append(qna.getAuthor()).append("</a>\n")
                        .append("</div>\n")
                        .append("<div class=\"reply\" title=\"댓글\">\n")
                        .append("<i class=\"icon-reply\"></i>\n")
                        .append("<span class=\"point\">").append(qna.getQnaId()).append("</span>\n")
                        .append("</div>\n").append("</div>\n").append("</div></li>");
            }
        }

        htmlBuilder.append(afterQnaList);

        response.addHeaderProperty("Content-Type", "text/html");
        response.setBody(htmlBuilder.toString().getBytes());

        FileUtil.generateDynamicMenuBar(request, response);
    }

    public void handle(HttpRequest request, HttpResponse response) throws IOException {
        File file = FileUtil.getFileFromUrl(request.getUrl());
        String contentType = FileUtil.getContentType(request.getUrl());

        if (!file.exists()) {
            response.setStatusCode(HttpStatus.NOT_FOUND);
            response.set404Body();
            return;
        }

        byte[] body = FileUtil.readFile(file);

        // 마지막 파일이 변경된 시점을 lastModified()를 통해 알 수 있다.
        LocalDateTime nowDate = LocalDateTime.now();
        LocalDateTime lastModified = LocalDateTime.ofInstant(
                Instant.ofEpochSecond(
                        Instant.ofEpochMilli(file.lastModified())
                                .getEpochSecond()
                ),
                ZoneId.systemDefault()
        );

        response.addHeaderProperty("Content-Type", contentType);

        if (contentType.equals("text/html")) {
            response.setBody(body);
            FileUtil.generateDynamicMenuBar(request, response);
            return;
        }

        response.addHeaderProperty("Cache-Control", "public, max-age=30");
        response.addHeaderProperty("Content-Length", String.valueOf(body.length));
        response.addHeaderProperty("Date", DateTimeUtil.getGMTDateString(nowDate));
        response.addHeaderProperty("Last-Modified", DateTimeUtil.getGMTDateString(lastModified));

        // 만약 이전의 요청으로 마지막으로 언제 수정됬는지 브라우저가 알면 요청헤더로 다음과 같은 값이 추가된다.
        String modifiedSince = request.getHeaderProperty("If-Modified-Since");

        if (!modifiedSince.isBlank()) {
            if (DateTimeUtil.parseGMTDateString(modifiedSince)
                    .isEqual(lastModified)) {
                // 수정한 날짜가 동일하다면 파일이 수정되지 않았다는 의미이므로
                // Empty Body와 함께 304 Not Modified 로 응답함
                response.setStatusCode(HttpStatus.NOT_MODIFIED);
                return;
            }
        }

        response.setStatusCode(HttpStatus.OK);
        response.setBody(body);
    }
}
