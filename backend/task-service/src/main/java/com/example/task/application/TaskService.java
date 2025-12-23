package com.example.task.application;

import com.example.task.domain.Task;
import com.example.task.infrastructure.TaskRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class TaskService {
    private final TaskRepository repository;

    public TaskService(TaskRepository repository) {
        this.repository = repository;
    }

    public List<Task> findAll() { return repository.findAll(); }

    public Task findById(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found: " + id));
    }

    public Task create(Task task) {
        task.setId(UUID.randomUUID().toString());
        task.setCreatedAt(Instant.now());
        task.setUpdatedAt(Instant.now());
        return repository.save(task);
    }

    public Task update(String id, Task incoming) {
        Task existing = findById(id);
        existing.setTitle(incoming.getTitle());
        existing.setDescription(incoming.getDescription());
        existing.setStatus(incoming.getStatus());
        existing.setAssigneeId(incoming.getAssigneeId());
        existing.setLabels(incoming.getLabels());
        existing.setDueDate(incoming.getDueDate());
        existing.setUpdatedAt(Instant.now());
        return repository.save(existing);
    }

    public void delete(String id) { repository.deleteById(id); }

    public Task move(String id, String columnId) {
        Task task = findById(id);
        task.setColumnId(columnId);
        task.setUpdatedAt(Instant.now());
        return repository.save(task);
    }
}
