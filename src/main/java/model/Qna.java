package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Qna {
    Long qnaId;
    String author;
    String title;
    String content;
    LocalDateTime createdAt;

    public Qna(Long qnaId, String author, String title, String content, LocalDateTime createdAt) {
        this.qnaId = qnaId;
        this.author = author;
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
    }

    public Long getQnaId() {
        return qnaId;
    }

    public String getAuthor() {
        return author;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getCreatedAtAsString() {
        return createdAt.format(
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        );
    }

    @Override
    public String toString() {
        return "Qna{" +
                "author='" + author + '\'' +
                ", title='" + title + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
