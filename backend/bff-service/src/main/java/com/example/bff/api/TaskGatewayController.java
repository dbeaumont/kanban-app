package com.example.bff.api;

import com.example.bff.dto.TaskDto;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskGatewayController {

    private final WebClient taskWebClient;

    public TaskGatewayController(@Qualifier("taskWebClient") WebClient taskWebClient) {
        this.taskWebClient = taskWebClient;
    }

    @GetMapping
    public Mono<List<TaskDto>> findAll() {
        return taskWebClient.get().uri("/tasks")
                .retrieve().bodyToFlux(TaskDto.class).collectList();
    }

    @GetMapping("/{id}")
    public Mono<TaskDto> findOne(@PathVariable String id) {
        return taskWebClient.get().uri("/tasks/{id}", id).retrieve().bodyToMono(TaskDto.class);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<TaskDto> create(@RequestBody TaskDto dto) {
        return taskWebClient.post().uri("/tasks").bodyValue(dto).retrieve().bodyToMono(TaskDto.class);
    }

    @PutMapping("/{id}")
    public Mono<TaskDto> update(@PathVariable String id, @RequestBody TaskDto dto) {
        return taskWebClient.put().uri("/tasks/{id}", id).bodyValue(dto).retrieve().bodyToMono(TaskDto.class);
    }

    @DeleteMapping("/{id}")
    public Mono<Void> delete(@PathVariable String id) {
        return taskWebClient.delete().uri("/tasks/{id}", id).retrieve().bodyToMono(Void.class);
    }

    @PatchMapping("/{id}/move")
    public Mono<TaskDto> move(@PathVariable String id, @RequestParam String columnId) {
        return taskWebClient.patch().uri(uriBuilder -> uriBuilder.path("/tasks/{id}/move").queryParam("columnId", columnId).build(id))
                .retrieve().bodyToMono(TaskDto.class);
    }
}
