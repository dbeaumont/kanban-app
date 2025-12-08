package com.example.bff.dto;

public record UserProfileDto(String id, String keycloakUserId, String displayName, String email, String preferences) {}
