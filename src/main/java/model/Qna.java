package model;

public class Qna {
    String author;
    String title;

    String content;

    public Qna(String author, String title, String content) {
        this.author = author;
        this.title = title;
        this.content = content;
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

    @Override
    public String toString() {
        return "Qna[" +
                "author='" + author + '\'' +
                ", title='" + title + '\'' +
                ", content=\"" + content + '\"' +
                ']';
    }
}
