package org.study.board.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.study.board.dto.Board;

@Slf4j
@Controller
public class BoardController {

    @RequestMapping("/main")
    public String main(Board board, Model model){

        return "board/main";
    }

    @RequestMapping("/write")
    public String write(Board board){
        return "board/write";
    }

    @RequestMapping("/insertBoard")
    public String insertBoard(Board board){
        return "redirect:/main";
    }

    @RequestMapping("/deleteBoard")
    public String deleteBoard(Board board){
        return "redirect:/main";
    }
}
