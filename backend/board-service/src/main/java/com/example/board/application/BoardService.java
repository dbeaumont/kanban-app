package com.example.board.application;

import com.example.board.domain.Board;
import com.example.board.infrastructure.BoardRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class BoardService {
    private final BoardRepository repository;

    public BoardService(BoardRepository repository) {
        this.repository = repository;
    }

    public List<Board> findAll() {
        return repository.findAll();
    }

    public Board findById(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Board not found: " + id));
    }

    public Board create(Board board) {
        board.setId(UUID.randomUUID().toString());
        return repository.save(board);
    }

    public Board update(String id, Board board) {
        Board existing = findById(id);
        existing.setName(board.getName());
        existing.setDescription(board.getDescription());
        existing.setOwnerId(board.getOwnerId());
        return repository.save(existing);
    }

    public void delete(String id) {
        repository.deleteById(id);
    }
}
