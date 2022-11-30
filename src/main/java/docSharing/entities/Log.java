package docSharing.entities;

import com.ibm.icu.util.LocaleData;

import javax.persistence.*;
import java.time.LocalDateTime;


@Entity
public class Log {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "doc_id")
    private Document document;

    @Column(name = "logContent")
    private String logContent;

    @Column(name = "time")
    private LocalDateTime localDateTime;


    public Log() {

    }
    public Log( User user, Document document, String logContent, LocalDateTime localDateTime) {

        this.user = user;
        this.document = document;
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


}



