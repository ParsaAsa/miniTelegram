package entity;

import jakarta.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "chats")
public class Chat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(name = "is_group", nullable = false)
    private boolean isGroup;

    @Column(name = "created_at")
    private Timestamp createdAt;

    // Constructors
    public Chat() {}

    public Chat(String name, boolean isGroup, Timestamp createdAt) {
        this.name = name;
        this.isGroup = isGroup;
        this.createdAt = createdAt;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isGroup() {
        return isGroup;
    }

    public void setGroup(boolean isGroup) {
        this.isGroup = isGroup;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}
