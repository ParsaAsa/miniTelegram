package entity;

import jakarta.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "media")
public class Media {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private MediaType type;

    private String url;
    private Timestamp uploadedAt;

    @ManyToOne
    private User uploader;

    @ManyToOne
    private Message message;

    @ManyToOne
    private Post post;

    // Getters and setters
}
