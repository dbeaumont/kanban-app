package com.example.board.application;

import com.example.board.domain.Board;
import com.example.board.infrastructure.BoardRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class BoardServiceTest {

    @Test
    void updateCopiesFields() {
        BoardRepository repo = Mockito.mock(BoardRepository.class);
        Board existing = new Board("1", "Old", "Desc", "owner");
        Mockito.when(repo.findById("1")).thenReturn(Optional.of(existing));
        Mockito.when(repo.save(Mockito.any())).thenAnswer(inv -> inv.getArgument(0));
        BoardService service = new BoardService(repo);

        Board updated = service.update("1", new Board(null, "New", "NewDesc", "owner2"));

        assertThat(updated.getName()).isEqualTo("New");
        assertThat(updated.getOwnerId()).isEqualTo("owner2");
    }
}
