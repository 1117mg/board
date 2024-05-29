package org.study.board.repository;

import org.apache.ibatis.annotations.Mapper;
import org.study.board.dto.Board;

import java.util.List;

@Mapper
public interface BoardMapper {

    List<Board> getBoardList(Board board);
    Board getBoard(int bno);
    void insertBoard(int bno);
    void updateBoard(int bno);
    boolean deleteBoard(int bno);
}
