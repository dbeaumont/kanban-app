package com.example.board.api;

import com.example.board.application.BoardService;
import com.example.board.domain.Board;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/boards")
public class BoardController {

    private final BoardService service;

    public BoardController(BoardService service) {
        this.service = service;
    }

    @GetMapping
    public List<Board> all() { return service.findAll(); }

    @GetMapping("/{id}")
    public Board one(@PathVariable String id) { return service.findById(id); }

    @PostMapping
    public Board create(@Valid @RequestBody Board board) { return service.create(board); }

    @PutMapping("/{id}")
    public Board update(@PathVariable String id, @Valid @RequestBody Board board) { return service.update(id, board); }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) { service.delete(id); }
}
