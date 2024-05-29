package org.study.board.controller;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.study.board.dto.JoinForm;
import org.study.board.dto.LoginForm;
import org.study.board.dto.User;
import org.study.board.service.UserService;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RequiredArgsConstructor
@Controller
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    @Autowired
    private UserService service;

    @GetMapping("/user/main")
    public String userList(Model model) {
        List<User> users = service.getAllUsers();
        model.addAttribute("users", users);
        return "user/main";
    }

    @GetMapping("/join")
    public String join(Model model) {

        model.addAttribute("JoinForm", new JoinForm());
        return "join";
    }

    @PostMapping("/join")
    public String join(@ModelAttribute("JoinForm") JoinForm form, BindingResult bindingResult) {
        // loginId 중복 체크
        if(service.checkLoginIdDuplicate(form.getLoginId())) {
            bindingResult.addError(new FieldError("joinForm", "loginId", "존재하는 아이디입니다."));
        }

        if (bindingResult.hasErrors()) {
            return "join";
        }

        if (!form.getPassword().equals(form.getPasswordCheck())) {
            bindingResult.rejectValue("passwordCheck", "passwordCheck", "비밀번호가 일치하지 않습니다.");
            return "join";
        }

        service.join(form);
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String loginForm(Model model) {

        model.addAttribute("loginForm", new LoginForm());
        return "login";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute("loginForm") LoginForm form, BindingResult bindingResult, HttpServletResponse response) {

        User loginUser = service.login(form.getLoginId(), form.getPassword());
        log.info("login? {}", loginUser);

        if (loginUser == null) {
            bindingResult.reject("loginFail", "아이디 또는 비밀번호가 맞지 않습니다.");
        }

        if (bindingResult.hasErrors()) {
            return "login";
        }

        Cookie cookie = new Cookie("idx", String.valueOf(loginUser.getIdx()));
        cookie.setMaxAge(60 * 60);  // 쿠키 유효 시간 : 1시간
        response.addCookie(cookie);

        // 로그인 성공 처리
        return "redirect:/main";
    }

    @GetMapping("/logout")
    public String logout(HttpServletResponse response) {

        Cookie cookie = new Cookie("idx", null);
        cookie.setMaxAge(0); //쿠키 종료
        response.addCookie(cookie);
        return "redirect:/main";
    }

    @GetMapping("/user/info/{userId}")
    public String userInfo(@PathVariable String userId, Model model) {
        log.info("Fetching user info for userId: {}", userId);
        User user = service.getLoginUser(userId);
        if (user == null) {
            log.warn("User not found for userId: {}", userId);
            return "redirect:/login";
        }
        log.info("User found: {}", user);
        model.addAttribute("user", user);
        return "user/info";
    }

    @GetMapping("/check-username")
    @ResponseBody
    public boolean checkUsername(@RequestParam String loginId) {
        return service.checkLoginIdDuplicate(loginId);
    }

}
