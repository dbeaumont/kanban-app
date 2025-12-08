package com.example.user.application;

import com.example.user.domain.UserProfile;
import com.example.user.infrastructure.UserProfileRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserProfileService {
    private final UserProfileRepository repository;

    public UserProfileService(UserProfileRepository repository) {
        this.repository = repository;
    }

    public List<UserProfile> findAll() { return repository.findAll(); }

    public UserProfile findById(String id) { return repository.findById(id).orElseThrow(); }

    public UserProfile findByKeycloakId(String keycloakUserId) { return repository.findByKeycloakUserId(keycloakUserId).orElseThrow(); }

    public UserProfile create(UserProfile profile) {
        profile.setId(UUID.randomUUID().toString());
        return repository.save(profile);
    }

    public UserProfile update(String id, UserProfile profile) {
        UserProfile existing = findById(id);
        existing.setDisplayName(profile.getDisplayName());
        existing.setEmail(profile.getEmail());
        existing.setPreferences(profile.getPreferences());
        return repository.save(existing);
    }

    public void delete(String id) { repository.deleteById(id); }
}
