package org.study.board.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TestController {

    @GetMapping("test")
    public String test(Model model){
        model.addAttribute("data", "Hello Thymeleaf");
        return "thymeleaf/test";
    }

    @GetMapping("main")
    public String main(){
        return "thymeleaf/main";
    }

    @GetMapping("error/403")
    public String error403() { return "thymeleaf/error/403";}

    @GetMapping("error/401")
    public String error401(){ return "thymeleaf/error/401";}

}
