package http;

import com.google.common.collect.Maps;
import model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class SessionManager {

    private static class Session {
        private LocalDateTime createdAt;
        private String sessionKey;

        private User user;

        public Session(LocalDateTime createdAt, String sessionKey, User user) {
            this.createdAt = createdAt;
            this.sessionKey = sessionKey;
            this.user = user;
        }

        public LocalDateTime getCreatedAt() {
            return createdAt;
        }

        public String getSessionKey() {
            return sessionKey;
        }

        public User getUser() {
            return user;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof String) {
                return sessionKey.equals(obj);
            }
            return super.equals(obj);
        }
    }

    private static Map<String, Session> sessionMap = Maps.newHashMap();

    public static String createSession(User user) {
        String randomKey = UUID.randomUUID().toString();
        LocalDateTime createdAt = LocalDateTime.now();
        sessionMap.put(randomKey, new Session(createdAt, randomKey, user));


        List<String> expired = sessionMap.entrySet().stream()
                .filter(e -> e.getValue().getCreatedAt().plusMinutes(5).isBefore(createdAt))
                .map(e -> e.getKey()).collect(Collectors.toList());

        expired.forEach(
                key -> sessionMap.remove(key)
        );

        return randomKey;
    }

    public static User findUserBySessionKey(String key) {
        Session session = sessionMap.getOrDefault(key, null);
        if (session == null)
            return null;

        LocalDateTime now = LocalDateTime.now();

        if (session.getCreatedAt().plusMinutes(5).isAfter(now))
            return session.getUser();

        return null;
    }
}
