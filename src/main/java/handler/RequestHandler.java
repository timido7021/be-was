package handler;

import java.io.*;
import java.net.Socket;
import java.util.Map;
import java.util.TreeMap;

import http.HttpRequest;
import http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.HttpUtil;
import http.status.HttpStatus;

public class RequestHandler implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;
    private FileHandler fileHandler;
    private UserHandler userHandler;

    public RequestHandler(Socket connectionSocket, FileHandler fileHandler, UserHandler userHandler) {
        this.connection = connectionSocket;
        this.fileHandler = fileHandler;
        this.userHandler = userHandler;
    }

    public void run() {
        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));

            HttpRequest httpRequest = HttpUtil.getHttpRequest(bufferedReader);
            logger.debug(httpRequest.toString());

            String method = httpRequest.getMethod();
            String url = httpRequest.getUrl();

            DataOutputStream dos = new DataOutputStream(out);
            HttpResponse response;
            Map<String, String> properties = new TreeMap<>();

            if (method.equals("GET")) {
                if (url.startsWith("/user") && !url.endsWith(".html")) {
                    response = userHandler.handle(httpRequest);
                } else if (url.equals("/")) {
                    properties.put("Location", "/index.html");
                    response = HttpResponse.emptyBodyResponse(HttpStatus.SEE_OTHER, properties);
                } else {
                    response = fileHandler.handle(httpRequest);
                }
            } else {
                properties.put("Allow", "GET");
                response = HttpResponse.emptyBodyResponse(HttpStatus.METHOD_NOT_ALLOWED, properties);
            }

            writeResponse(dos, response);
        } catch (IOException | NullPointerException e) {
            logger.error(e.getMessage());
        }
    }

    private void writeResponse(DataOutputStream dos, HttpResponse response) throws IOException {
        dos.writeBytes(response.getHeader().toString());
        dos.write(response.getBody(), 0, response.getBody().length);
        dos.flush();
    }
}
