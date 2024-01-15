package utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.RequestHandler;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class HttpHeaderUtil {
    private static final Logger logger = LoggerFactory.getLogger(HttpHeaderUtil.class);

    public static String[] getHttpMethodAndUrl(BufferedReader bufferedReader) throws IOException{
        String requestHeaderLine = "";
        String httpMethod = "";
        String requestUrl = "";

        while (!(requestHeaderLine = bufferedReader.readLine().trim()).isEmpty()) {
            logger.debug(requestHeaderLine);

            String[] tokens = requestHeaderLine.split(" ");
            if (tokens[0].equals("GET")
                    || tokens[0].equals("POST")) {
                httpMethod = tokens[0];
                requestUrl = tokens[1];
            }
        }

        String[] result = {httpMethod, requestUrl};
        return result;
    }

    public static String getContentType(String requestedUrl) {
        Path source = Paths.get(requestedUrl);
        try {
            String mimeType = Files.probeContentType(source);
            if (mimeType == null) {
                if (requestedUrl.endsWith(".woff"))
                    mimeType = "font/woff";
                else
                    throw new Exception("cannot determine content-type.");
            }
            return mimeType;
        } catch (Exception e) {
            logger.error(e.getMessage());
            return "text/plain";
        }
    }
}
