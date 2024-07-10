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
import org.springframework.security.crypto.password.PasswordEncoder;
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
    @Autowired
    private UserService userService;

    @GetMapping("/kakao-login")
    public String kakaoLogin(@RequestParam("code") String code, HttpServletResponse response) {
        String access_Token = oauthService.getKakaoAccessToken(code);
        HashMap<String, Object> userInfo = oauthService.getUserInfo(access_Token);
        log.info("카카오 사용자 정보 : " + userInfo);

        String email = (String) userInfo.get("email");
        String nickname = (String) userInfo.get("nickname");

        User user = userService.findByLoginId(email);

        if (user == null) {
            user = new User();
            user.setUserId(email);
            user.setPassword(access_Token);
            user.setUsername(nickname);
            log.info("등록 전 사용자 정보: {}", user);
            userService.register(user);
            log.info("DB 저장 후 사용자 정보: {}", userService.findByLoginId(email));
        }

        userService.loginWithToken(user,access_Token);

        Cookie cookie = new Cookie("idx", String.valueOf(user.getIdx()));
        cookie.setMaxAge(60 * 60 * 24);  // 쿠키 유효 시간 : 1일
        response.addCookie(cookie);

        return "redirect:/0/main";
    }
}
