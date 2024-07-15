package org.study.board.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.study.board.dto.JoinForm;
import org.study.board.dto.LoginForm;
import org.study.board.dto.User;
import org.study.board.service.RecaptchaService;
import org.study.board.service.UserService;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@RequiredArgsConstructor
@Controller
public class UserController {

    @Autowired
    private UserService service;
    @Autowired
    private RecaptchaService recaptchaService;

    @GetMapping("/join")
    public String join(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof AnonymousAuthenticationToken){
            model.addAttribute("JoinForm", new JoinForm());
            return "thymeleaf/join";}
        return "redirect:/login";
    }

    @PostMapping("/join")
    public String join(@ModelAttribute("JoinForm") JoinForm form, BindingResult bindingResult) {
        if(form.getSnsType()!=null){
            service.join(form);
            return "thymeleaf/alert/snsloginsuccess";
        }

        // loginId 중복 체크
        if(service.checkLoginIdDuplicate(form.getLoginId())) {
            bindingResult.addError(new FieldError("joinForm", "loginId", "존재하는 아이디입니다."));
        }

        if (bindingResult.hasErrors()) {
            return "thymeleaf/join";
        }

        if (!form.getPassword().equals(form.getPasswordCheck())) {
            bindingResult.rejectValue("passwordCheck", "passwordCheck", "비밀번호가 일치하지 않습니다.");
            return "thymeleaf/join";
        }

        service.join(form);
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String loginForm(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof AnonymousAuthenticationToken){
            model.addAttribute("loginForm", new LoginForm());
            return "thymeleaf/login";}
        return "redirect:/0/main";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute("loginForm") LoginForm form, BindingResult bindingResult, HttpServletRequest request, HttpServletResponse response) {
        User loginUser = service.login(form.getLoginId(), form.getPassword());
        if (loginUser == null) {
            bindingResult.reject("loginFail", "아이디 또는 비밀번호가 맞지 않습니다.");
            return "thymeleaf/login";
        }

        // reCAPTCHA 필요 여부 확인
        if (loginUser.isLocked()) {
            String recaptchaResponse = request.getParameter("recaptchaResponse");
            // reCAPTCHA 검증
            if (recaptchaResponse == null || !recaptchaService.verifyRecaptcha(recaptchaResponse)) {
                bindingResult.reject("recaptchaFail", "reCAPTCHA 검증에 실패했습니다.");
                return "thymeleaf/login";  // 로그인 페이지로 다시 이동
            }
            // reCAPTCHA 검증 통과 후 리셋
            loginUser.setLocked(false);
            service.updateStatus(loginUser);
        }

        if (bindingResult.hasErrors()) {
            return "thymeleaf/login";
        }

        Cookie cookie = new Cookie("idx", String.valueOf(loginUser.getIdx()));
        cookie.setMaxAge(60 * 60 * 24);  // 쿠키 유효 시간 : 1일
        response.addCookie(cookie);

        log.info("로그인 성공: {}", loginUser);
        return "redirect:/0/main";
    }

    @GetMapping("/logout")
    public String logout(HttpServletResponse response) {

        Cookie cookie = new Cookie("idx", null);
        cookie.setMaxAge(0); //쿠키 종료
        response.addCookie(cookie);
        return "redirect:/0/main";
    }

    @GetMapping("/check-username")
    @ResponseBody
    public boolean checkUsername(@RequestParam String loginId) {
        return service.checkLoginIdDuplicate(loginId);
    }
}
