package entity;

import jakarta.persistence.*;

@Entity
@Table(name = "users") // Avoid PostgreSQL reserved word "User"
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    private String displayName;
    private String profilePictureUrl;
    private String createdAt;
    private String lastOnline;

    // Constructors
    public User() {}

    public User(String username, String password, String displayName, String profilePictureUrl, String createdAt, String lastOnline) {
        this.username = username;
        this.password = password;
        this.displayName = displayName;
        this.profilePictureUrl = profilePictureUrl;
        this.createdAt = createdAt;
        this.lastOnline = lastOnline;
    }

    // Getters and setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getLastOnline() {
        return lastOnline;
    }

    public void setLastOnline(String lastOnline) {
        this.lastOnline = lastOnline;
    }
}
