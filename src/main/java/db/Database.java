package db;

import model.Qna;
import model.User;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class Database {
    private static Map<String, User> users = new ConcurrentHashMap<>();
    private static Map<Long, Qna> qnaMap = new ConcurrentHashMap<>();

    public static void addUser(User user) {
        users.put(user.getUserId(), user);
    }

    public static void addQna(Qna qna) {
        qnaMap.put(qna.getQnaId(), qna);
    }

    public static User findUserById(String userId) {
        return users.get(userId);
    }

    public static Qna findQnaById(Long qnaId) {
        return qnaMap.get(qnaId);
    }

    public static Collection<User> findAllUsers() {
        return users.values();
    }

    public static Collection<Qna> findAllQna() {
        return qnaMap.values();
    }
}
