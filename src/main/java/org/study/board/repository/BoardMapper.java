package org.study.board.repository;

import org.apache.ibatis.annotations.Mapper;
import org.study.board.dto.Board;
import org.study.board.dto.FileDto;

import java.util.List;
import java.util.Map;

@Mapper
public interface BoardMapper {

    List<Board> getBoardList(Board board);
    // 첨부파일 리스트
    List<FileDto> getFile(Board board);
    Board getBoard(Integer bno);
    void insertBoard(Board board);
    void updateBoard(Board board);
    boolean deleteBoard(int bno);
    void insertFile(Board board);
}
