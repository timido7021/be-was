package service;

import db.Database;
import model.Qna;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class QnaService {
    public static QnaService getInstance() {
        return QnaService.LazyHolder.INSTANCE;
    }

    private static class LazyHolder {
        private static final QnaService INSTANCE = new QnaService();
    }

    private static final Logger logger = LoggerFactory.getLogger(QnaService.class);

    private static AtomicLong qnaCounter = new AtomicLong(0);

    public boolean saveQna(String author, String title, String content) {
        try {
            Qna newQna = new Qna(qnaCounter.addAndGet(1), author, title, content, LocalDateTime.now());
            Database.addQna(newQna);
            logger.debug("Qna saved: {}", newQna);
            logger.debug("DB Qna-s: {}", Database.findAllQna());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Qna getQna(Long qnaId) {
        Qna qna = Database.findQnaById(qnaId);

        return qna;
    }

    public List<Qna> listAll() {
        return new ArrayList<>(Database.findAllQna());
    }
}
