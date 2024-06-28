package org.study.board.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.study.board.dto.User;
import org.study.board.repository.UserMapper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private final UserMapper mapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String userId = userDetails.getUsername();
        User user = mapper.findByLoginId(userId);

        user.setFailedAttempts(0);
        user.setLocked(false);
        mapper.updateStatus(user);
        response.sendRedirect("/main");

        // 계정이 잠겨있는 경우
        /*if (user.isLocked() || user.getFailedAttempts() >= 5) {
            response.sendRedirect("/login?error=locked");
        } else {
            // 계정이 잠겨있지 않은 경우 로그인 성공 처리
            user.setFailedAttempts(0);
            user.setLocked(false);
            mapper.updateStatus(user);
            response.sendRedirect("/main");
        }*/
    }
}
