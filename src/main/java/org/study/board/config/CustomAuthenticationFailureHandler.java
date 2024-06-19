package org.study.board.config;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.study.board.dto.User;
import org.study.board.repository.UserMapper;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private final UserMapper mapper;

    // 로그인 실패 시
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws ServletException, IOException {
        String userId = request.getParameter("loginId");
        User user = mapper.findByLoginId(userId);

        // 사용자가 존재하지 않는 경우
        if (user == null) {
            request.getSession().setAttribute("notFound", "등록되지 않은 계정입니다.");
            super.onAuthenticationFailure(request, response, exception);
            return;
        }

        // 사용자가 존재하는 경우
        user.incrementFailedAttempts();

        // 실패 횟수가 5회인 경우
        if (user.getFailedAttempts() >= 5) {
            user.setLocked(true);
            mapper.updateStatus(user);
            setDefaultFailureUrl("/login?error=locked");
        } else {
            mapper.updateStatus(user);
            int remainingAttempts = 5 - user.getFailedAttempts();
            setDefaultFailureUrl("/login?error=true&remainingAttempts=" + remainingAttempts);
        }
        super.onAuthenticationFailure(request, response, exception);
    }
}