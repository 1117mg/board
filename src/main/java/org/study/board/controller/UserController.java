package org.study.board.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.study.board.dto.JoinForm;
import org.study.board.dto.LoginForm;
import org.study.board.dto.User;
import org.study.board.dto.UserBackup;
import org.study.board.service.AdminService;
import org.study.board.service.RecaptchaService;
import org.study.board.service.UserService;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Controller
public class UserController {

    @Autowired
    private UserService service;
    @Autowired
    private AdminService adminService;
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
    public String join(@ModelAttribute("JoinForm") JoinForm form, BindingResult bindingResult, Model model) {
        if(form.getSnsType()!=""){
            service.join(form);
            return "thymeleaf/alert/snsloginsuccess";
        }

        // loginId 중복 체크
        if (service.checkLoginIdDuplicate(form.getLoginId())) {
            bindingResult.addError(new FieldError("JoinForm", "loginId", "존재하는 아이디입니다."));
            model.addAttribute("errorMessage", "존재하는 아이디입니다.");
            return "thymeleaf/join";
        }

        // username 중복 체크
        // 현재 sns사용자를 휴대폰 번호 대신 이름으로 구분하고 있기 때문
        // 추후에 휴대폰 번호로 구분할 시 해당 로직은 필요없음
        if (service.findByName(form.getUsername()) != null) {
            model.addAttribute("errorMessage", "해당 이름으로 가입 이력이 존재합니다.");
            return "thymeleaf/join";
        }

        // phoneNo 중복 체크
        if (service.findByPhoneNo(form.getPhoneNo()) != null) {
            model.addAttribute("errorMessage", "해당 전화번호로 가입 이력이 존재합니다.");
            return "thymeleaf/join";
        }

        // 최근 탈퇴한 사용자인지 체크
        UserBackup recentBackup = service.findRecentBackup(form.getPhoneNo(), LocalDateTime.now().minusDays(30));
        if (recentBackup != null) {
            model.addAttribute("errorMessage", "탈퇴 후 30일 이내 동일 번호로 재가입은 불가합니다.");
            return "thymeleaf/join";
        }

        if (bindingResult.hasErrors()) {
            return "thymeleaf/join";
        }

        if (!form.getPassword().equals(form.getPasswordCheck())) {
            bindingResult.rejectValue("passwordCheck", "passwordCheck", "비밀번호가 일치하지 않습니다.");
            model.addAttribute("errorMessage", "비밀번호가 일치하지 않습니다.");
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

        // 로그인 성공 시 처리
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
    public boolean checkUsername(@RequestParam("loginId") String loginId) {
        log.info("자바스크립트에서 아이디를 제대로 읽어오고 있을까?: {}", loginId);
        return service.checkLoginIdDuplicate(loginId);
    }

    @GetMapping("/mypage/{userId}")
    public String mypage(@PathVariable String userId, Model model){
        log.info(userId);
        User user = service.findByLoginId(userId);
        if (user == null) {
            log.warn("유저확인불가: {}", userId);
            return "redirect:/login";
        }
        model.addAttribute("user", user);
        return "thymeleaf/mypage";
    }

    @PostMapping("/mypage/update")
    public String updateMypage(@ModelAttribute User user){
        String userId=user.getUserId();
        adminService.updateUser(user);
        return "redirect:/mypage/"+userId;
    }

    @GetMapping("/mypage/delete/{idx}")
    public String deleteMe(@PathVariable long idx, HttpServletRequest request, HttpServletResponse response){
        User user=service.findById(idx);
        adminService.deleteUser(user);

        // 탈퇴 후 로그아웃 처리
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            new SecurityContextLogoutHandler().logout(request, response, authentication);
        }

        return "redirect:/0/main";
    }
}
