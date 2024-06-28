package org.study.board.interceptor;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.Optional;

@Component
public class AuthInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response,Object handler) throws Exception {

        // 1. Cookie 방식
        // 쿠키에서 사용자 토큰을 찾습니다.
        /*Optional<Cookie> userTokenCookie = Arrays.stream(request.getCookies())
                .filter(cookie -> cookie.getName().equals("idx"))
                .findFirst();

        // 사용자 토큰이 존재하지 않으면 로그인 페이지로 리다이렉트합니다.
        if (!userTokenCookie.isPresent()) {
            response.sendRedirect("/login");
            return false;
        }

        // 토큰이 존재하면 인증을 성공한 것으로 간주합니다.
        return true;*/

        // 2. Session 방식
        /*HttpSession session = request.getSession();
        // 사용자가 로그인을 하면 세션 메모리 영역에 user 키-값 구조로 저장 처리할 예정
        Object user=session.getAttribute("user");
        System.out.println(user);

        System.out.println("preHandler 동작 확인");
        if(user==null){
            response.sendRedirect("/login");
            return false;
        }
        return true;*/

        // 3. Spring Security를 이용한 사용자 인증 상태 확인
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 사용자가 인증되어 있지 않은 경우 로그인 페이지로 리다이렉트
        if (authentication == null || !authentication.isAuthenticated()) {
            response.sendRedirect("/login");
            return false;
        }

        // 사용자가 인증되어 있으면 요청을 허용
        return true;
    }
}
