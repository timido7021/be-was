package controller.util;

import model.User;
import webserver.http.HttpRequest;
import webserver.http.HttpResponse;
import webserver.http.HttpStatus;
import webserver.http.SessionManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

public class FileUtil {
    private enum ContentType {
        HTML(".html", "text/html"),
        CSS(".css", "text/css"),
        JS(".js", "text/javascript"),
        TTF(".ttf", "font/ttf"),
        WOFF(".woff", "font/woff"),
        ICO(".ico", "image/vnd.microsoft.icon"),
        ;

        private final String extension;
        private final String mimeType;

        ContentType(String extension, String mimeType) {
            this.extension = extension;
            this.mimeType = mimeType;
        }

        public String getExtension() {
            return extension;
        }

        public String getMimeType() {
            return mimeType;
        }
    }


    public static File getFileFromUrl(String url) {
        String contentType = getContentType(url);

        // 정적 파일이 html이면 templates 디렉토리를 이용해서 파일 객체를 생성하고
        // 아니면 static 디렉토리를 이용해서 생성한다.
        if (contentType.equals("text/html")) {
            return new File("src/main/resources/templates" + url);
        } else {
            return new File("src/main/resources/static" + url);
        }
    }

    public static String getContentType(String url) {
        String urlExtension;
        ContentType contentType;

        if (url.lastIndexOf('.') > 0) {
            urlExtension = url.substring(url.lastIndexOf('.'));
        } else {
            urlExtension = "";
        }

        try {
            contentType = Stream.of(ContentType.values())
                    .filter(type -> type.getExtension().equals(urlExtension))
                    .findFirst().orElseThrow();
        } catch (NoSuchElementException e) {
            // ContentType enum에 해당하는 객체가 없다면
            // 일반 binary 파일이라는 의미의 ContentType을 반환한다.
            return "application/octet-stream";
        }

        return contentType.getMimeType();
    }

    public static byte[] readFile(File file) throws IOException {
        if (file.length() > Integer.MAX_VALUE)
            throw new IOException("File is too large");

        byte[] result = new byte[(int) file.length()];
        int offset = 0;
        int numRead = 0;
        try (InputStream is = new FileInputStream(file)) {
            while (offset < result.length && (numRead = is.read(result, offset, result.length - offset)) >= 0) {
                offset += numRead;
            }
        }

        return result;
    }

    public static void generateDynamicMenuBar(HttpRequest request, HttpResponse response) {
        User user = SessionManager.findUserByRequest(request);
        StringBuilder htmlBuilder = new StringBuilder();
        String originalBody = new String(response.getBody());
        String beforeMenuBar = originalBody.substring(0, originalBody.indexOf("{{menu_bar}}"));
        String afterMenuBar = originalBody.substring(originalBody.indexOf("{{menu_bar}}")
                + "{{menu_bar}}".length());

        String logoutBtnStyle = "border: black 0px solid; background: transparent; padding: 15px; line-height: 20px; color:#777;";

        htmlBuilder.append(beforeMenuBar);
        if (user != null) {
            htmlBuilder.append("<li><a href=\"#\">").append(user.getName()).append("</a></li>")
                    .append("<li><a href=\"/index.html\" role=\"button\">Posts</a></li>\n")
                    .append("<li><form method=\"POST\" action=\"/user/logout\"><button style=\"").append(logoutBtnStyle)
                    .append("\">로그아웃</button></form></li>\n")
                    .append("<li><a href=\"#\" role=\"button\">개인정보수정</a></li>");
        } else {
            htmlBuilder.append("<li><a href=\"/index.html\">Posts</a></li>\n")
                    .append("<li><a href=\"/user/login.html\" role=\"button\">로그인</a></li>\n")
                    .append("<li><a href=\"/user/form.html\" role=\"button\">회원가입</a></li>\n");
        }
        htmlBuilder.append(afterMenuBar);
        byte[] htmlBody = htmlBuilder.toString().getBytes();

        response.addHeaderProperty("Content-Length", String.valueOf(htmlBody.length));

        response.setStatusCode(HttpStatus.OK);
        response.setBody(htmlBody);
    }
}
