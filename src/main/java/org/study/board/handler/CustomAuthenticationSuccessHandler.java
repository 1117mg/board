package org.study.board.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.study.board.dto.User;
import org.study.board.repository.UserMapper;
import org.study.board.service.RecaptchaService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private final UserMapper mapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        User userDetails = (User) authentication.getPrincipal();
        String userId = userDetails.getUserId();
        User user = mapper.findByLoginId(userId);


        user.setFailedAttempts(0);
        user.setLocked(false);
        mapper.updateStatus(user);
        response.sendRedirect("/main");

        /*if (user.isLocked() && !isRecaptchaValid) {
            response.sendRedirect("/login?error=locked");
        } else {
            // 로그인 성공 시 실패 시도 횟수 초기화 및 계정 잠금 해제
            user.setFailedAttempts(0);
            user.setLocked(false);
            mapper.updateStatus(user);
            response.sendRedirect("/main");
            System.out.println("Authentication success for user: " + userId);  // 로그 추가

        }*/
    }
}
