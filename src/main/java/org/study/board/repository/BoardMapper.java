package org.study.board.repository;

import org.apache.ibatis.annotations.Mapper;
import org.study.board.dto.Board;
import org.study.board.dto.FileDto;

import java.util.List;

@Mapper
public interface BoardMapper {

    List<Board> getBoardList(Board board);
    List<FileDto> getFile(Board board);
    Board getBoard(Integer bno);
    int hit(Integer bno);
    Integer cntBoard(Integer boardType);
    void insertBoard(Board board);
    void updateBoard(Board board);
    boolean deleteBoard(Integer bno);
    void insertFile(Board board);
    void deleteFile(Board board);
}
