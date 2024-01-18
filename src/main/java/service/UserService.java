package service;

import controller.UserController;
import db.Database;
import model.User;

public class UserService {
    public static UserService getInstance() {
        return UserService.LazyHolder.INSTANCE;
    }

    private static class LazyHolder {
        private static final UserService INSTANCE = new UserService();
    }

    public boolean saveUser(User user) {
        try {
            Database.addUser(user);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
