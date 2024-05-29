package org.study.board.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.study.board.dto.Board;
import org.study.board.dto.FileDto;
import org.study.board.dto.User;
import org.study.board.repository.BoardMapper;
import org.study.board.repository.UserMapper;
import org.study.board.service.BoardService;
import org.study.board.util.FileUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Controller
public class BoardController {

    @Autowired
    private BoardService boardService;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private HttpServletRequest request;


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
    public String boardDetail(@PathVariable Integer bno, Model model){
        Board board = boardService.getBoard(bno);
        model.addAttribute("board", board);
        return "board/write";
    }

    @RequestMapping("/write")
    public String write(@CookieValue(name="idx", required = false) Long idx, Model model, Board board){
        User loginUser=userMapper.findById(idx);
        model.addAttribute("user", loginUser);
        if(board.getBno()==null){
            model.addAttribute("getBoard", board);
            model.addAttribute("getFile", boardService.getFile(board));
        }
        return "board/write";
    }

    @RequestMapping("/insertBoard")
    public String insertBoard(@ModelAttribute Board board, @CookieValue(name="idx", required = false) Long idx, Model model) {
        User loginUser=userMapper.findById(idx);
        board.setWriter(loginUser.getUsername());
        model.addAttribute("user", loginUser);
        boardService.insertBoard(board);
        return "redirect:/main";
    }

    @DeleteMapping("/delete/{bno}")
    public ResponseEntity<String> deleteBoard(@PathVariable int bno) {
        boolean deleted = boardService.deleteBoard(bno);
        if (deleted) {
            return ResponseEntity.ok("게시물이 성공적으로 삭제되었습니다.");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("게시물 삭제 중에 오류가 발생했습니다.");
        }
    }

    /*ajax로 첨부파일 처리*/
    @RequestMapping("/ajaxFile")
    @ResponseBody
    public List<FileDto> ajaxFile(@RequestParam("files") MultipartFile[] uploadFile, @RequestParam("files") List<MultipartFile> files) throws IOException {
        // 파일 등록
        List<FileDto> fileList = FileUtil.uploadFile(uploadFile);
        return fileList;
    }

    /*파일 다운로드*/
    @RequestMapping("/downloadFile")
    public ResponseEntity<Resource> downloadFile(@ModelAttribute FileDto fileDto) throws IOException {
        return boardService.downloadFile(fileDto);
    }

}
