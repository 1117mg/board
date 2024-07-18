package org.study.board.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
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
        User userDetails = (User) authentication.getPrincipal();
        String userId = userDetails.getUserId();
        User user = mapper.findByLoginId(userId);


        user.setFailedAttempts(0);
        user.setLocked(false);
        mapper.updateStatus(user);
        response.sendRedirect("/0/main");
    }
}
