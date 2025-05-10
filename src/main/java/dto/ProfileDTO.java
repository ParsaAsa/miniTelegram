package dto;

public class ProfileDTO {
    private String displayName;
    private String profilePicture;

    public ProfileDTO(String displayName, String profilePicture) {
        this.displayName = displayName;
        this.profilePicture = profilePicture;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getProfilePicture() {
        return profilePicture;
    }
}
