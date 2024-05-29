package org.study.board.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.study.board.dto.Board;
import org.study.board.dto.User;
import org.study.board.repository.UserMapper;
import org.study.board.service.BoardService;

import java.util.List;

@Slf4j
@Controller
public class BoardController {

    @Autowired
    private BoardService boardService;
    @Autowired
    private UserMapper userMapper;

    @RequestMapping("/main")
    public String main(@CookieValue(name="idx", required = false) Long idx, Board board, Model model){

        List<Board> boardList = boardService.getBoardlist(board);
        if(idx == null){
            model.addAttribute("board", boardList);
            return "board/main";
        }

        //로그인
        User loginUser=userMapper.findById(idx);
        if(loginUser == null){
            return "login";
        }
        model.addAttribute("user", loginUser);
        model.addAttribute("board", boardList);
        return "board/main";
    }

    @GetMapping("/board/{bno}")
    public String boardDetail(@PathVariable int bno, Model model){
        Board board = boardService.getBoard(bno);
        model.addAttribute("board", board);
        return "board/write";
    }

    @RequestMapping("/write")
    public String write(@CookieValue(name="idx", required = false) Long idx, Model model){
        User loginUser=userMapper.findById(idx);
        /*Board board = boardService.getBoard(bno);
        if(board!=null){
            model.addAttribute("board", board);
        }*/
        model.addAttribute("user", loginUser);
        return "board/write";
    }

    @RequestMapping("/insertBoard")
    public String insertBoard(Model model, int bno){
        Board board = boardService.getBoard(bno);
        if(board.getBno()==null){
            boardService.insertBoard(bno);
        }else{
            boardService.updateBoard(bno);
        }
        model.addAttribute("board", board);
        return "redirect:/main";
    }

    @DeleteMapping("/delete/{bno}")
    public ResponseEntity<String> deleteBoard(@PathVariable int bno) {
        log.info("Delete board bno: {}", bno);
        boolean deleted = boardService.deleteBoard(bno);
        if (deleted) {
            return ResponseEntity.ok("게시물이 성공적으로 삭제되었습니다.");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("게시물 삭제 중에 오류가 발생했습니다.");
        }
    }
}
