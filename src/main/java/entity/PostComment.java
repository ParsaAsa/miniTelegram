package entity;

import jakarta.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "post_comments")
public class PostComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Post post;

    @ManyToOne
    private User author;

    private String content;
    private Timestamp createdAt;

    // Getters and setters
}
