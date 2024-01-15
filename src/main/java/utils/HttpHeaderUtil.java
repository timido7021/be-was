package utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class HttpHeaderUtil {
    private static final Logger logger = LoggerFactory.getLogger(HttpHeaderUtil.class);

    public static String[] getHttpMethodAndUrl(BufferedReader bufferedReader) throws IOException {
        String httpRequest = bufferedReader.readLine().trim();
        String[] httpRequestTokens = httpRequest.split(" ");
        String httpMethod = httpRequestTokens[0];
        String requestUrl = httpRequestTokens[1];

        logger.debug("HTTP Method: " + httpMethod);
        logger.debug("HTTP Request URL: " + requestUrl);

        String requestHeaderLine = "";

        while (!(requestHeaderLine = bufferedReader.readLine().trim()).isEmpty()) {
            if (
                    requestHeaderLine.startsWith("Host: ")
                    || requestHeaderLine.startsWith("Connection: ")
                    || requestHeaderLine.startsWith("Accept")
                    || requestHeaderLine.startsWith("User-Agent: ")
                    || requestHeaderLine.startsWith("Referer: ")
            )
                logger.debug(requestHeaderLine);
        }

        return httpRequestTokens;
    }

    public static String getContentType(String requestedUrl) {
        Path source = Paths.get(requestedUrl);
        try {
            String contentType = Files.probeContentType(source);
            if (contentType == null) {
                if (requestedUrl.endsWith(".woff"))
                    contentType = "font/woff";
                else
                    throw new Exception("cannot determine content-type.");
            }
            return contentType;
        } catch (Exception e) {
            logger.error(e.getMessage());
            return "text/plain";
        }
    }
}
