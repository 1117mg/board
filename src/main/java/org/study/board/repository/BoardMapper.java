package org.study.board.repository;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.study.board.dto.Board;
import org.study.board.dto.FileDto;

import java.util.List;

@Mapper
public interface BoardMapper {

    List<Board> getBoardList(Board board);
    List<Board> getQnaList(Board board);
    List<FileDto> getFile(Board board);
    Board getBoard(Integer bno);
    void updateSorts(@Param("gno") int gno, @Param("sorts") int sorts);
    List<Board> getParentBoards(Integer bno);
    List<Board> getChildBoards(Integer bno);
    int hit(Integer bno);
    Integer cntBoard(Integer boardType);
    Integer getMaxGno();
    void insertBoard(Board board);
    void updateBoard(Board board);
    boolean deleteBoard(Integer bno);
    void deleteChildBoards(Integer bno);
    void insertFile(Board board);
    void deleteFile(Board board);
}
