package org.study.board.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.study.board.handler.CustomPermissionEvaluator;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.Optional;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Autowired
    private CustomPermissionEvaluator customPermissionEvaluator;

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
        /*Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 사용자가 인증되어 있지 않은 경우 로그인 페이지로 리다이렉트
        if (authentication == null || !authentication.isAuthenticated()) {
            response.sendRedirect("/login");
            return false;
        }

        // 사용자가 인증되어 있으면 요청을 허용
        return true;*/

        // 4. DB에 저장된 접근 권한 확인
        /*Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // URL 경로와 권한 타입 매핑
        String requestURI = request.getRequestURI();
        String permissionKey = null;
        String permissionType = null;

        if (requestURI.startsWith("/admin/user-list") || requestURI.startsWith("/admin/user-info")) {
            permissionKey = "USER_LIST";
            permissionType = requestURI.contains("updateUser") || requestURI.contains("user-auth") ? "WRITE" : "READ";
        } else if (requestURI.startsWith("/0/board") || requestURI.startsWith("/0/write") || requestURI.startsWith("/0/downloadFile")) {
            permissionKey = "NOTICE_BOARD";
            if (requestURI.contains("write")) {
                permissionType = "WRITE";
            } else if (requestURI.contains("downloadFile")) {
                permissionType = "DOWNLOAD";
            } else {
                permissionType = "READ";
            }
        } else if (requestURI.startsWith("/1/board") || requestURI.startsWith("/1/write") || requestURI.startsWith("/1/downloadFile")) {
            permissionKey = "QNA_BOARD";
            if (requestURI.contains("write")) {
                permissionType = "WRITE";
            } else if (requestURI.contains("downloadFile")) {
                permissionType = "DOWNLOAD";
            } else {
                permissionType = "READ";
            }
        }

        if (permissionKey != null && permissionType != null) {
            if (!customPermissionEvaluator.hasPermission(authentication, permissionKey, permissionType)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
                return false;
            }
        }

        return true;*/

        // mapper 쿼리로 권한 확인
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied: User not authenticated");
            return false;
        }

        String requestURI = request.getRequestURI();
        String permissionKey = getPermissionKey(requestURI);
        String permissionType = getPermissionType(requestURI);

        if (permissionKey != null && permissionType != null) {
            if (!customPermissionEvaluator.hasPermission(authentication, permissionKey, permissionType)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied: Insufficient permissions");
                return false;
            }
        }

        return true;
    }

    private String getPermissionKey(String requestURI) {
        if (requestURI.startsWith("/admin/user-list") || requestURI.startsWith("/admin/user-info")) {
            return "USER_LIST";
        } else if (requestURI.startsWith("/0")) {
            return "NOTICE_BOARD";
        } else if (requestURI.startsWith("/1")) {
            return "QNA_BOARD";
        } else {
            return null;
        }
    }

    private String getPermissionType(String requestURI) {
        if (requestURI.contains("write")) {
            return "WRITE";
        } else if (requestURI.contains("downloadFile")) {
            return "DOWNLOAD";
        } else {
            return "READ";
        }
    }
}
