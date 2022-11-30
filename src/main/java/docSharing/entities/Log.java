package docSharing.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ibm.icu.util.LocaleData;
import jakarta.validation.constraints.NotNull;

import javax.persistence.*;
import java.time.LocalDateTime;


@Entity
public class Log {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY, targetEntity = User.class)
    @JoinColumn(name = "user_id")
    @NotNull(message = "User not set")
    @JsonIgnore
    private User user;


    @Column(name = "user_id", insertable = false, updatable = false)
    private Long userId;


    @ManyToOne(fetch = FetchType.LAZY, targetEntity = Document.class)
    @JoinColumn(name = "doc_id")
    @NotNull(message = "Document not set")
    @JsonIgnore
    private Document document;

    @Column(name = "doc_id", insertable = false, updatable = false)
    private Long docId;

    @Column(name = "logContent")
    private String logContent;

    @Column(name = "time")
    private LocalDateTime localDateTime;


    public Log() {

    }

    public Log(Long userId, Long docId, String logContent, LocalDateTime localDateTime) {

        this.userId = userId;
        this.docId = docId;
        this.logContent = logContent;
        this.localDateTime = localDateTime;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    public void setLogContent(String logContent) {
        this.logContent = logContent;
    }

    public void setLocalDateTime(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
    }

    public int getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public Document getDocument() {
        return document;
    }

    public String getLogContent() {
        return logContent;
    }

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }

    @Override
    public String toString() {
        return "Log{" +
                "id=" + id +
                ", userId=" + userId +
                ", docId=" + docId +
                ", logContent='" + logContent + '\'' +
                ", localDateTime=" + localDateTime +
                '}';
    }
}



