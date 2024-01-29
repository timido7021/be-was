package controller;

import annotations.AuthRequired;
import annotations.GetMapping;
import annotations.PostMapping;
import controller.util.FileUtil;
import model.User;
import service.QnaService;
import webserver.http.HttpRequest;
import webserver.http.HttpResponse;
import webserver.http.HttpStatus;
import webserver.http.SessionManager;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class QnaController {
    public static QnaController getInstance() {
        return QnaController.LazyHolder.INSTANCE;
    }

    private static class LazyHolder {
        private static final QnaController INSTANCE = new QnaController();
    }

    @GetMapping(route = "/qna/write.html")
    @AuthRequired
    public void qnaForm(HttpRequest request, HttpResponse response) throws IOException {
        User user = SessionManager.findUserByRequest(request);
        StringBuilder htmlBuilder = new StringBuilder();

        byte[] listTemplate = FileUtil.readFile(new File("src/main/resources/templates/qna/write.html"));

        String template = new String(listTemplate);
        String beforeQnaAuthor = template.substring(0, template.indexOf("{{value}}"));
        String afterQnaAuthor = template.substring(template.indexOf("{{value}}")+"{{value}}".length());
        htmlBuilder.append(beforeQnaAuthor).append(user.getName());
        htmlBuilder.append(afterQnaAuthor);

        response.addHeaderProperty("Content-Type", "text/html");
        response.setStatusCode(HttpStatus.OK);
        response.setBody(htmlBuilder.toString().getBytes());

        FileUtil.generateDynamicMenuBar(request, response);
    }

    @PostMapping(route = "/qna/write")
    @AuthRequired
    public void createQna(HttpRequest request, HttpResponse response) throws IOException {
        QnaService qnaService = QnaService.getInstance();

        Map<String, String> qnaProperties = request.getBody().convertRawStringAsMap();
        String author = qnaProperties.getOrDefault("writer", "");
        String title = qnaProperties.getOrDefault("title", "");
        String content = qnaProperties.getOrDefault("contents", "");

        boolean isSaved = qnaService.saveQna(author, title, content);

        if (isSaved) {
            response.setStatusCode(HttpStatus.FOUND);
            response.addHeaderProperty("Location", "/index.html");
        } else {
            response.setStatusCode(HttpStatus.FOUND);
            response.addHeaderProperty("Location", "/qna/write.html");
        }
    }
}
