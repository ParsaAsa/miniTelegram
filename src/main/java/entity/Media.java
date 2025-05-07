package entity;

import jakarta.persistence.*;

@Entity
@Table(name = "media")
public class Media {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String url; // Example field

    @ManyToOne
    @JoinColumn(name = "message_id")
    private Message message;

    // Constructors
    public Media() {}

    public Media(String url, Message message) {
        this.url = url;
        this.message = message;
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public Message getMessage() { return message; }
    public void setMessage(Message message) { this.message = message; }
}
