package org.study.board.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.study.board.dto.LoginForm;
import org.study.board.dto.User;
import org.study.board.repository.UserMapper;
import org.study.board.service.OauthService;
import org.study.board.service.UserService;

import javax.json.Json;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.HashMap;

@Slf4j
@RequiredArgsConstructor
@Controller
public class OauthController {

    @Autowired
    private OauthService oauthService;

    //String clientId ="a411d09a7888de46a12e564c7859c055";
    //String redirectUri="http://localhost:8080/kakao-login";

    @GetMapping("/kakao-login")
    public String kakaoForm(@RequestParam("code") String code, HttpSession session) {
        String access_Token = oauthService.getKakaoAccessToken(code);
        HashMap<String, Object> userInfo = oauthService.getUserInfo(access_Token);
        System.out.println("카카오 사용자 정보 : " + userInfo);

        if (userInfo.get("email") != null) {
            LoginForm loginForm = new LoginForm();
            loginForm.setLoginId((String) userInfo.get("email"));
            loginForm.setPassword(access_Token);
            session.setAttribute("loginForm", loginForm);
            session.setMaxInactiveInterval(60 * 30);
            session.setAttribute("kakaoToken", access_Token);
        }

        return "redirect:/0/main";
    }

    /*@GetMapping("/kakao-login")
    public String kakaoLogin(@Valid @ModelAttribute("loginForm") LoginForm form, BindingResult bindingResult, HttpServletResponse response) {

        User user = userService.findByLoginId(form.getLoginId());

        if (user == null) {
            // 사용자가 데이터베이스에 없으면 회원가입 후 로그인
            user = new User();
            user.setUserId(form.getLoginId());
            user.setUsername(form.getLoginId());
            user.setPassword(form.getPassword());  // 이 부분은 실제로는 비밀번호를 저장할 때 해싱 등의 방법을 사용해야 합니다.
            userService.register(user);
        }
        //User loginUser = userService.login(form.getLoginId(), form.getPassword());

        if (bindingResult.hasErrors()) {
            return "thymeleaf/login";
        }
        // 쿠키 설정
        Cookie cookie = new Cookie("idx", String.valueOf(user.getIdx()));
        cookie.setMaxAge(60 * 60 * 24);  // 쿠키 유효 시간 : 1일
        response.addCookie(cookie);

        log.info("로그인 성공: {}", user);
        return "redirect:/0/main";
    }*/
}
