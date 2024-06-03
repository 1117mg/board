package org.study.board.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.study.board.dto.Board;
import org.study.board.dto.FileDto;
import org.study.board.dto.PaginateDto;
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
    public String main(Board board, Model model, @RequestParam(defaultValue = "1") int page){

        // 게시글 총 개수
        int total = boardService.cntBoard();
        model.addAttribute("cntBoard", total);
        // 페이징
        PaginateDto paginate = new PaginateDto(5, 3);
        paginate.setPageNo(page);
        paginate.setTotalSize(total);

        board.setPageNo(page);
        board.setPageSize(paginate.getPageSize());
        board.setPageOffset(paginate.getPageOffset());

        model.addAttribute("paginate", paginate);
        model.addAttribute("board", boardService.getBoardlist(board));
        return "board/main";
    }

    @GetMapping("/board/{bno}")
    public String boardDetail(@PathVariable Integer bno, Model model, Board board){
        Board boardDetail = boardService.getBoard(bno);
        List<FileDto> file = boardService.getFile(board);
        board.setHit(boardService.hit(bno));

        model.addAttribute("board", boardDetail);
        model.addAttribute("getFile", file);

        return "board/write";
    }

    @RequestMapping("/write")
    public String write(@CookieValue(name="idx", required = false) Long idx, Model model, Board board){
        /*User loginUser=userMapper.findById(idx);
        model.addAttribute("user", loginUser);*/

        // Spring Security 수정
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        // 모델에 사용자 정보 추가
        model.addAttribute("user", username);
        if(board.getBno()==null){
            model.addAttribute("getBoard", board);
            model.addAttribute("getFile", boardService.getFile(board));
        }


        return "board/write";
    }

    @RequestMapping("/insertBoard")
    public String insertBoard(@ModelAttribute Board board, @CookieValue(name="idx", required = false) Long idx, Model model) {
        /*User loginUser=userMapper.findById(idx);
        board.setWriter(loginUser.getUsername());
        model.addAttribute("user", loginUser);*/
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        board.setWriter(username);
        // 모델에 사용자 정보 추가
        model.addAttribute("user", username);
        boardService.insertBoard(board);
        return "redirect:/main";
    }

    @DeleteMapping("/delete/{bno}")
    public ResponseEntity<String> deleteBoard(@PathVariable Integer bno) {
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
    public List<FileDto> ajaxFile(@RequestParam("files") MultipartFile[] uploadFile) {
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
