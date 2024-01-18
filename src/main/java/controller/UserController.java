package controller;

import http.HttpRequest;
import http.HttpResponse;
import http.header.ResponseHeader;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import http.status.HttpStatus;
import service.UserService;

import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Stream;

public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private UserService userService = UserService.getInstance();

    public static UserController getInstance() {
        return LazyHolder.INSTANCE;
    }

    private static class LazyHolder {
        private static final UserController INSTANCE = new UserController();
    }

    public void handle(HttpRequest request, HttpResponse response) {
        Map<String, String> queryString = request.getQueryString();
        String userId = queryString.getOrDefault("userId", "");
        String password = queryString.getOrDefault("password", "");
        String name = queryString.getOrDefault("name", "");
        String email = queryString.getOrDefault("email", "");

        if (Stream.of(userId, password, name, email)
                .anyMatch(e->e.isBlank())
        ) {
            throw new RuntimeException("user property cannot be blank");
        }

        boolean isSaved = userService.saveUser(new User(userId, password, name, email));
        Map<String, String> properties = new TreeMap<>();

        if (isSaved) {
            properties.put("Location", "/user/login.html");

            response.setHeader(
                    ResponseHeader.of(HttpStatus.SEE_OTHER, properties)
            );
        } else {
            response.setHeader(
                    ResponseHeader.of(HttpStatus.BAD_REQUEST, properties)
            );
        }
    }
}
