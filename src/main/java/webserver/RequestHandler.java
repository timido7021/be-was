package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestHandler implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        logger.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));

            String requestHeaderLine = "";
            String method = "";
            String requestUrl = "";

            while (!(requestHeaderLine = bufferedReader.readLine().trim()).isEmpty()) {
                logger.debug(requestHeaderLine);

                String[] tokens = requestHeaderLine.split(" ");
                if (tokens[0].equals("GET")
                        || tokens[0].equals("POST")) {
                    method = tokens[0];
                    requestUrl = tokens[1];
                }
            }

            // TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.

            DataOutputStream dos = new DataOutputStream(out);
            byte[] body;
            String contentType;
            if (method.equals("GET")) {
                if (requestUrl.equals("/index.html")) {
                    body = Files.readAllBytes(
                            new File("./src/main/resources/templates" + requestUrl).toPath());
                    contentType = "text/html";
                }
                else  {
                    body = Files.readAllBytes(
                            new File("./src/main/resources/static" + requestUrl).toPath());

                    if (requestUrl.endsWith(".js"))
                        contentType = "text/javascript";
                    else if (requestUrl.endsWith(".css"))
                        contentType = "text/css";
                    else if (requestUrl.endsWith(".ico"))
                        contentType = "image/vnd.microsoft.icon";
                    else if (requestUrl.endsWith(".ttf"))
                        contentType = "text/ttf";
                    else
                        contentType = "text/plain";
                }
            } else {
                body = "Hello World".getBytes();
                contentType = "text/html";
            }

            response200Header(dos, body.length, contentType);
            responseBody(dos, body);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent, String contentType) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes(String.format("Content-Type: %s;charset=utf-8\r\n", contentType));
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }
}
