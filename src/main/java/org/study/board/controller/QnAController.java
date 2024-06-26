package org.study.board.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.study.board.aop.BoardAop;
import org.study.board.dto.Board;
import org.study.board.dto.FileDto;
import org.study.board.dto.PaginateDto;
import org.study.board.service.BoardService;
import org.study.board.util.FileUtil;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

@RequestMapping("/1")
@Slf4j
@Controller
public class QnAController {

    @Autowired
    private BoardService boardService;
    @Autowired
    private BoardAop aop;

    // 타임리프
    @RequestMapping("/test")
    public String test(Model model){
        model.addAttribute("name","thymeleaf");
        return "thymeleaf/test";
    }

    @RequestMapping("/main")
    public String main(Board board, Model model, @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "1") int boardType){
        board.setBoardType(boardType);
        // 게시글 총 개수
        int total = boardService.cntBoard(board.getBoardType());
        model.addAttribute("cntBoard", total);
        // 페이징
        PaginateDto paginate = new PaginateDto(5, 3);
        paginate.setPageNo(page);
        paginate.setTotalSize(total);

        // 현재 요청의 쿼리 파라미터를 포함한 URL을 생성
        String params = "boardType=" + boardType; // 필요한 다른 파라미터를 추가
        paginate.setParams(params);

        paginate._calc(); // 페이지 네비게이션 계산

        board.setPageNo(page);
        board.setPageSize(paginate.getPageSize());
        board.setPageOffset(paginate.getPageOffset());

        model.addAttribute("paginate", paginate);
        model.addAttribute("board", boardService.getQnaList(board));
        model.addAttribute("boardType", board.getBoardType());
        return "thymeleaf/board";
    }

    @GetMapping("/board/{bno}")
    public String boardDetail(@PathVariable Integer bno, Model model){
        Board boardDetail = boardService.getBoard(bno);
        List<FileDto> file = boardService.getFile(boardDetail);
        boardDetail.setHit(boardService.hit(bno));

        // 상위 글
        List<Board> parentBoards = boardService.getParentBoards(bno);
        // 하위 글
        List<Board> childBoards = boardService.getChildBoards(bno);

        model.addAttribute("board", boardDetail);
        model.addAttribute("getFile", file);
        model.addAttribute("parentBoards", parentBoards);
        model.addAttribute("childBoards", childBoards);
        model.addAttribute("hasChildBoards", !childBoards.isEmpty());

        return "thymeleaf/write";
    }

    @GetMapping("/write")
    public String write(Model model, Board board, @RequestParam(required = false) Integer parentBno, @RequestParam(defaultValue = "1") int boardType){
        // 모델에 사용자 정보 추가
        aop.addUserToModel(model);
        board.setBoardType(boardType);

        if(board.getBno()==null){
            model.addAttribute("getBoard", board);
            model.addAttribute("getFile", boardService.getFile(board));
        }

        return "thymeleaf/write";
    }

    @PostMapping("/insertBoard")
    public String insertBoard(@ModelAttribute Board board, @RequestParam(defaultValue = "1") int boardType) {
        board.setBoardType(boardType);
        boardService.insertBoard(board);
        return "redirect:/1/main";
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
