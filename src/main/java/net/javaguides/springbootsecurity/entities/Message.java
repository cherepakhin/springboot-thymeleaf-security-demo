package net.javaguides.springbootsecurity.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Objects;

/**
 * @author Ramesh Fadatare
 */
@Entity
@Table(name = "messages")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer n;

    @Column(nullable = false)
    private String content;

    public Integer getN() {
        return n;
    }

    public void setN(Integer n) {
        this.n = n;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Message)) return false;
        Message message = (Message) o;
        return Objects.equals(n, message.n) && Objects.equals(content, message.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(n, content);
    }

    @Override
    public String toString() {
        return "Message{" +
                "n=" + n +
                ", content='" + content + '\'' +
                '}';
    }
}
