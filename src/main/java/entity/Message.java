package entity;

import jakarta.persistence.*;
import java.sql.Timestamp;
import java.util.List;

@Entity
@Table(name = "messages")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "sender_id")
    private User sender;

    @ManyToOne(optional = false)
    @JoinColumn(name = "chat_id")
    private Chat chat;

    @Column(nullable = false)
    private String content;

    private Timestamp sentAt;
    private Timestamp editedAt;

    @ManyToOne
    @JoinColumn(name = "reply_to_id")
    private Message replyTo;

    @OneToMany(mappedBy = "message", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Media> mediaList;

    // Constructors
    public Message() {}

    public Message(User sender, Chat chat, String content, Timestamp sentAt, Message replyTo) {
        this.sender = sender;
        this.chat = chat;
        this.content = content;
        this.sentAt = sentAt;
        this.replyTo = replyTo;
    }

    // Getters and setters
    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public User getSender() { return sender; }

    public void setSender(User sender) { this.sender = sender; }

    public Chat getChat() { return chat; }

    public void setChat(Chat chat) { this.chat = chat; }

    public String getContent() { return content; }

    public void setContent(String content) { this.content = content; }

    public Timestamp getSentAt() { return sentAt; }

    public void setSentAt(Timestamp sentAt) { this.sentAt = sentAt; }

    public Timestamp getEditedAt() { return editedAt; }

    public void setEditedAt(Timestamp editedAt) { this.editedAt = editedAt; }

    public Message getReplyTo() { return replyTo; }

    public void setReplyTo(Message replyTo) { this.replyTo = replyTo; }

    public List<Media> getMediaList() { return mediaList; }

    public void setMediaList(List<Media> mediaList) { this.mediaList = mediaList; }
}
