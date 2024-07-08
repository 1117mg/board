package org.study.board.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.study.board.dto.User;
import org.study.board.repository.UserMapper;
import org.study.board.service.RecaptchaService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Timestamp;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private final UserMapper mapper;
    private final RecaptchaService recaptchaService;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws ServletException, IOException {
        String userId = request.getParameter("loginId");
        User user = mapper.findByLoginId(userId);

        String recaptchaResponse = request.getParameter("recaptchaResponse");
        boolean isRecaptchaValid = recaptchaService.verifyRecaptcha(recaptchaResponse);

        if (user == null) {
            // 등록되지 않은 계정일 경우
            request.getSession().setAttribute("notFoundMessage", "등록되지 않은 계정입니다.");
            super.setDefaultFailureUrl("/login?error=notFound");
        } else {
            // 등록된 계정이지만 비밀번호가 틀린 경우
            user.incrementFailedAttempts();
            mapper.updateStatus(user);

            if (user.getFailedAttempts() >= 5 && !isRecaptchaValid) {
                // 실패 횟수 5회 이상이고 reCAPTCHA 검증 실패 시 계정 잠금 처리
                user.setLocked(true);
                user.setLockTime(new Timestamp(System.currentTimeMillis()));
                mapper.updateStatus(user);
                super.setDefaultFailureUrl("/login?error=locked");
            } else if(user.getFailedAttempts() < 5 && user.getFailedAttempts() > 0) {
                // 남은 시도 횟수 계산
                int remainingAttempts = 5 - user.getFailedAttempts();
                super.setDefaultFailureUrl("/login?error=true&remainingAttempts=" + remainingAttempts);
            } else {
                // 아이디와 비밀번호가 일치하면서, 또는 reCAPTCHA 검증이 성공한 경우는 성공 핸들러로 리다이렉트
                user.setFailedAttempts(0);
                user.setLocked(false);
                user.setLockTime(null);
                mapper.updateStatus(user);
                super.setDefaultFailureUrl("/login?error=success");
            }
        }

        /*if (user == null) {
            // 등록되지 않은 계정일 경우
            request.getSession().setAttribute("notFoundMessage", "등록되지 않은 계정입니다.");
            super.setDefaultFailureUrl("/login?error=notFound");
        } else {
            // 등록된 계정이지만 비밀번호가 틀린 경우
            user.incrementFailedAttempts();
            mapper.updateStatus(user);

            if (user.getFailedAttempts() >= 5) {
                if(isRecaptchaValid){
                    user.setFailedAttempts(0);
                    user.setLocked(false);
                    user.setLockTime(null);
                    mapper.updateStatus(user);
                    request.getSession().removeAttribute("SPRING_SECURITY_LAST_EXCEPTION");
                    response.sendRedirect("/main");
                    return;
                }else{
                    // 계정 잠금 처리
                    user.setLocked(true);
                    user.setLockTime(new Timestamp(System.currentTimeMillis()));
                    mapper.updateStatus(user);
                    super.setDefaultFailureUrl("/login?error=locked");
                }
            } else {
                // 남은 시도 횟수 계산
                int remainingAttempts = 5 - user.getFailedAttempts();
                super.setDefaultFailureUrl("/login?error=true&remainingAttempts=" + remainingAttempts);
            }
        }*/

        super.onAuthenticationFailure(request, response, exception);
    }
}