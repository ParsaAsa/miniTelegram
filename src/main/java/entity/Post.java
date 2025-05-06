package entity;

import jakarta.persistence.*;
import java.sql.Timestamp;
import java.util.List;

@Entity
@Table(name = "posts")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User author;

    private String content;
    private Timestamp createdAt;
    private int likesCount;
    private int commentsCount;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<Media> mediaList;

    // Getters and setters
}
