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
import java.sql.Timestamp;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private final UserMapper mapper;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws ServletException, IOException {
        String userId = request.getParameter("loginId");
        User user = mapper.findByLoginId(userId);

        if (user == null) {
            // 등록되지 않은 계정일 경우
            request.getSession().setAttribute("notFoundMessage", "등록되지 않은 계정입니다.");
            super.setDefaultFailureUrl("/login?error=notFound");
        } else {
            // 등록된 계정이지만 비밀번호가 틀린 경우
            user.incrementFailedAttempts();
            mapper.updateStatus(user);

            if (user.getFailedAttempts() >= 5) {
                // 계정 잠금 처리
                user.setLocked(true);
                user.setLockTime(new Timestamp(System.currentTimeMillis()));
                mapper.updateStatus(user);
                super.setDefaultFailureUrl("/login?error=locked");
            } else {
                // 남은 시도 횟수 계산
                int remainingAttempts = 5 - user.getFailedAttempts();
                super.setDefaultFailureUrl("/login?error=incorrectPassword&remainingAttempts=" + remainingAttempts);
            }
        }

        super.onAuthenticationFailure(request, response, exception);
    }
}