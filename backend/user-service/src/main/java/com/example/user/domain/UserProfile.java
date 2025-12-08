package com.example.user.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class UserProfile {
    @Id
    private String id;
    private String keycloakUserId;
    private String displayName;
    private String email;
    private String preferences;

    public UserProfile() {}

    public UserProfile(String id, String keycloakUserId, String displayName, String email, String preferences) {
        this.id = id;
        this.keycloakUserId = keycloakUserId;
        this.displayName = displayName;
        this.email = email;
        this.preferences = preferences;
    }
    // getters/setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getKeycloakUserId() { return keycloakUserId; }
    public void setKeycloakUserId(String keycloakUserId) { this.keycloakUserId = keycloakUserId; }
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPreferences() { return preferences; }
    public void setPreferences(String preferences) { this.preferences = preferences; }
}
