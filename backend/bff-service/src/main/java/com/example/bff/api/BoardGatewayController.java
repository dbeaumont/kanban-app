package com.example.bff.api;

import com.example.bff.dto.BoardDto;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/boards")
public class BoardGatewayController {

    private final WebClient boardWebClient;

    public BoardGatewayController(@Qualifier("boardWebClient") WebClient boardWebClient) {
        this.boardWebClient = boardWebClient;
    }

    @GetMapping
    public Mono<List<BoardDto>> findAll() {
        return boardWebClient.get().uri("/boards")
                .retrieve().bodyToFlux(BoardDto.class).collectList();
    }

    @GetMapping("/{id}")
    public Mono<BoardDto> findOne(@PathVariable("id") String id) {
        return boardWebClient.get().uri("/boards/{id}", id).retrieve().bodyToMono(BoardDto.class);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<BoardDto> create(@RequestBody BoardDto dto) {
        return boardWebClient.post().uri("/boards").bodyValue(dto).retrieve().bodyToMono(BoardDto.class);
    }

    @PutMapping("/{id}")
    public Mono<BoardDto> update(@PathVariable("id") String id, @RequestBody BoardDto dto) {
        return boardWebClient.put().uri("/boards/{id}", id).bodyValue(dto).retrieve().bodyToMono(BoardDto.class);
    }

    @DeleteMapping("/{id}")
    public Mono<Void> delete(@PathVariable("id") String id) {
        return boardWebClient.delete().uri("/boards/{id}", id).retrieve().bodyToMono(Void.class);
    }
}
