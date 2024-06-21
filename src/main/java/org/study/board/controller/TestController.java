package org.study.board.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TestController {

    @GetMapping("test")
    public String test(Model model){
        model.addAttribute("data", "Hello Spring");
        return "test/basic";
    }

    @GetMapping("main")
    public String main(){
        return "thymeleaf/main";
    }
}
