package com.example.bff.dto;

import java.time.Instant;

public record TaskDto(String id, String boardId, String columnId, String title, String description,
                      String status, String assigneeId, String labels, Instant dueDate,
                      Instant createdAt, Instant updatedAt) {}
