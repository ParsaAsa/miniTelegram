package entity;

import jakarta.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "chat_members")
public class ChatMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "chat_id")
    private Chat chat;

    @Column(nullable = false)
    private Timestamp joinedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChatRole role;

    // Constructors
    public ChatMember() {}

    public ChatMember(User user, Chat chat, Timestamp joinedAt, ChatRole role) {
        this.user = user;
        this.chat = chat;
        this.joinedAt = joinedAt;
        this.role = role;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Chat getChat() {
        return chat;
    }

    public void setChat(Chat chat) {
        this.chat = chat;
    }

    public Timestamp getJoinedAt() {
        return joinedAt;
    }

    public void setJoinedAt(Timestamp joinedAt) {
        this.joinedAt = joinedAt;
    }

    public ChatRole getRole() {
        return role;
    }

    public void setRole(ChatRole role) {
        this.role = role;
    }
}
