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
            request.getSession().setAttribute("notFound", "등록되지 않은 계정입니다.");
            super.onAuthenticationFailure(request, response, exception);
            return;
        }

        user.incrementFailedAttempts();

        if (user.getFailedAttempts() >= 5) {
            user.setLocked(true);
            user.setLockTime(new Timestamp(System.currentTimeMillis()));
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