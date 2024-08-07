package org.study.board.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.study.board.dto.User;
import org.study.board.service.GoogleService;
import org.study.board.service.KakaoService;
import org.study.board.service.NaverService;
import org.study.board.service.UserService;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/oauth")
public class OauthController {

    @Autowired
    private KakaoService kakaoService;
    @Autowired
    private NaverService naverService;
    @Autowired
    private GoogleService googleService;
    @Autowired
    private UserService userService;

    @GetMapping("/kakao-login")
    public String kakaoLogin(@RequestParam("code") String code, HttpServletResponse response, Model model) {
        String access_Token = kakaoService.getKakaoAccessToken(code);
        log.info(access_Token);
        HashMap<String, Object> userInfo = kakaoService.getUserInfo(access_Token);
        log.info("카카오 사용자 정보 : " + userInfo);

        String email = (String) userInfo.get("email");
        String name = (String) userInfo.get("nickname");
        String snsType="kakao";

        // 최초 가입 시 회원가입 페이지로 이동
        if(userService.findByName(name)==null){
            model.addAttribute("userId", email);
            model.addAttribute("password", "default_password");
            model.addAttribute("username", name);
            model.addAttribute("snsType",snsType);
            return "thymeleaf/join";
        }

        if(userService.findSnsUser(name,snsType)==null) {
            userService.snsJoin(email, name, snsType);
            return "thymeleaf/alert/snslogincheck";
        }

        User user=userService.findByName(name);

        // 로그인
        userService.loginWithToken(name, snsType, access_Token);

        Cookie cookie = new Cookie("idx", String.valueOf(user.getIdx()));
        cookie.setMaxAge(60 * 60 * 24);  // 쿠키 유효 시간 : 1일
        response.addCookie(cookie);

        return "redirect:/0/main";
    }

    @GetMapping("/naver-login")
    public String naverLogin(@RequestParam("code") String code, HttpServletResponse response, Model model) throws Exception {
        String access_Token = naverService.getNaverAccessToken(code);
        log.info(access_Token);
        HashMap<String, Object> userInfo = naverService.getUserInfo(access_Token);
        log.info("네이버 사용자 정보 : " + userInfo);

        String email = (String) userInfo.get("email");
        String name = (String) userInfo.get("name");
        String snsType="naver";

        // 최초 가입 시 회원가입 페이지로 이동
        if(userService.findByName(name)==null){
            model.addAttribute("userId", email);
            model.addAttribute("password", "default_password");
            model.addAttribute("username", name);
            model.addAttribute("snsType",snsType);
            return "thymeleaf/join";
        }

        if(userService.findSnsUser(name,snsType)==null) {
            userService.snsJoin(email, name, snsType);
            return "thymeleaf/alert/snslogincheck";
        }

        User user=userService.findByName(name);

        // 로그인
        userService.loginWithToken(name, snsType, access_Token);

        Cookie cookie = new Cookie("idx", String.valueOf(user.getIdx()));
        cookie.setMaxAge(60 * 60 * 24);  // 쿠키 유효 시간 : 1일
        response.addCookie(cookie);

        return "redirect:/0/main";
    }

    @GetMapping("/google-login")
    public String googleLogin(@RequestParam("code") String code, HttpServletResponse response, Model model) throws Exception {
        String access_Token = googleService.getGoogleAccessToken(code);
        log.info(access_Token);
        HashMap<String, Object> userInfo = googleService.getUserInfo(access_Token);
        log.info("구글 사용자 정보 : " + userInfo);

        String email = (String) userInfo.get("email");
        String name = (String) userInfo.get("name");
        String snsType="google";

        // 최초 가입 시 회원가입 페이지로 이동
        if(userService.findByName(name)==null){
            model.addAttribute("userId", email);
            model.addAttribute("password", "default_password");
            model.addAttribute("username", name);
            model.addAttribute("snsType",snsType);
            return "thymeleaf/join";
        }

        if(userService.findSnsUser(name,snsType)==null) {
            userService.snsJoin(email, name, snsType);
            return "thymeleaf/alert/snslogincheck";
        }

        User user=userService.findByName(name);

        // 로그인
        userService.loginWithToken(name, snsType, access_Token);

        Cookie cookie = new Cookie("idx", String.valueOf(user.getIdx()));
        cookie.setMaxAge(60 * 60 * 24);  // 쿠키 유효 시간 : 1일
        response.addCookie(cookie);

        return "redirect:/0/main";
    }
}
