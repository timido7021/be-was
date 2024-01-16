package handler;

import db.Database;
import model.HttpRequest;
import model.HttpResponse;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import model.HttpStatusCode;

import java.util.Map;
import java.util.TreeMap;

public class UserHandler {
    private static final Logger logger = LoggerFactory.getLogger(UserHandler.class);

    private static UserHandler userHandler = null;

    public static UserHandler getInstance() {
        if (userHandler == null)
            userHandler = new UserHandler();
        return userHandler;
    }

    public HttpResponse handle(HttpRequest request) {
        Map<String, String> variables = request.getPathVariables();
        String userId = variables.getOrDefault("userId", null);
        String password = variables.getOrDefault("password", null);
        String name = variables.getOrDefault("name", null);
        String email = variables.getOrDefault("email", null);

        Database.addUser(new User(userId, password, name, email));

        Map<String, String> properties = new TreeMap<>();
        properties.put("Location", "/user/login.html");

        return HttpResponse.emptyBodyResponse(HttpStatusCode.SEE_OTHER, properties);
    }
}
