package com.example.task.api;

import com.example.task.application.TaskService;
import com.example.task.domain.Task;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tasks")
public class TaskController {
    private final TaskService service;

    public TaskController(TaskService service) {
        this.service = service;
    }

    @GetMapping public List<Task> all() { return service.findAll(); }

    @GetMapping("/{id}") public Task one(@PathVariable("id") String id) { return service.findById(id); }

    @PostMapping public Task create(@RequestBody Task task) { return service.create(task); }

    @PutMapping("/{id}") public Task update(@PathVariable("id") String id, @RequestBody Task task) { return service.update(id, task); }

    @DeleteMapping("/{id}") public void delete(@PathVariable("id") String id) { service.delete(id); }

    @PatchMapping("/{id}/move") public Task move(@PathVariable("id") String id, @RequestParam("columnId") String columnId) { return service.move(id, columnId); }
}
