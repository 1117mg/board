package org.study.board.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.study.board.dto.Board;
import org.study.board.dto.User;
import org.study.board.repository.UserMapper;
import org.study.board.service.BoardService;
import org.study.board.service.UserService;

import java.util.Optional;

@Slf4j
@Controller
public class BoardController {

    @Autowired
    private BoardService boardService;
    @Autowired
    private UserMapper userMapper;

    @RequestMapping("/main")
    public String main(@CookieValue(name="userId", required = false) Long idx, Board board, Model model){
        if(idx == null){
            return "board/main";
        }

        //로그인
        User loginUser=userMapper.findById(idx);
        if(loginUser == null){
            return "login";
        }
        model.addAttribute("user", loginUser);

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
