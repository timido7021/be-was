package dispatcher;

import controller.StaticResourceController;
import controller.UserController;
import http.HttpRequest;
import http.HttpResponse;
import http.header.ResponseHeader;
import http.status.HttpStatus;
import utils.FileUtil;

import java.io.IOException;
import java.util.Map;

public class RequestDispatcher {
    private final UserController userController = UserController.getInstance();
    private final StaticResourceController staticResourceController = StaticResourceController.getInstance();

    public static RequestDispatcher getInstance() {
        return RequestDispatcher.LazyHolder.INSTANCE;
    }

    private static class LazyHolder {
        private static final RequestDispatcher INSTANCE = new RequestDispatcher();
    }


    public void dispatchHandler(HttpRequest request, HttpResponse response) throws IOException {
        String method = request.getMethod();
        String url = request.getUrl();

        if (method.equals("GET")) {
            if (url.equals("/")) {
                response.setEmptyBody();
                response.setHeader(
                        ResponseHeader.of(HttpStatus.SEE_OTHER, Map.of("Location", "/index.html"))
                );
            }
            if (FileUtil.extensions.stream()
                    .anyMatch(extension -> url.endsWith(extension))) {
                staticResourceController.handle(request, response);
            }
            return;
        }

        if (method.equals("POST")) {
            if (url.equals("/user/create"))
                userController.signup(request, response);
            return;
        }

        response.setEmptyBody();
        response.setHeader(
                ResponseHeader.of(HttpStatus.METHOD_NOT_ALLOWED, Map.of("Allow", "GET"))
        );
    }
}
