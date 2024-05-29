package org.study.board.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.study.board.dto.Board;
import org.study.board.repository.BoardMapper;

import java.util.List;

@Service
public class BoardService {

    @Autowired
    private BoardMapper mapper;

    public List<Board> getBoardlist(Board board) {
        return mapper.getBoardList(board);
    }

    public Board getBoard(int bno){
        return mapper.getBoard(bno);
    }

    public void insertBoard(int bno){
        mapper.insertBoard(bno);
    }

    public void updateBoard(int bno){
        mapper.updateBoard(bno);
    }

    public boolean deleteBoard(int bno){
        return mapper.deleteBoard(bno);
    }
}
