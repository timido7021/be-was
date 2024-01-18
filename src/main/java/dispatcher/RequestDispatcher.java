package dispatcher;

import controller.ResourceController;
import controller.UserController;
import http.HttpRequest;
import http.HttpResponse;
import http.header.ResponseHeader;
import http.status.HttpStatus;

import java.io.IOException;
import java.util.Map;

public class RequestDispatcher {
    private final UserController userController = UserController.getInstance();
    private final ResourceController resourceController = ResourceController.getInstance();

    public static RequestDispatcher getInstance() {
        return RequestDispatcher.LazyHolder.INSTANCE;
    }

    private static class LazyHolder {
        private static final RequestDispatcher INSTANCE = new RequestDispatcher();
    }


    public void dispatchHandler(HttpRequest request, HttpResponse response) throws IOException {
        String method = request.getMethod();
        String url = request.getUrl();

        if (method.equals("GET")) { // Redirect - file - service
            if (url.equals("/")) { // html 같은 정적 파일
                response.setEmptyBody();
                response.setHeader(
                        ResponseHeader.of(HttpStatus.SEE_OTHER,  Map.of("Location", "/index.html"))
                );
            }
            else if (ResourceController.extensions.stream().anyMatch(
                    extension -> url.endsWith(extension)
            )) {
                resourceController.handle(request, response);
            } else {
                userController.handle(request, response);
            }
        } else {
            response.setEmptyBody();
            response.setHeader(
                    ResponseHeader.of(HttpStatus.METHOD_NOT_ALLOWED, Map.of("Allow", "GET"))
            );
        }
    }
}
