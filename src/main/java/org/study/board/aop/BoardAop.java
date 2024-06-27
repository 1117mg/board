package org.study.board.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.study.board.dto.Board;

@Slf4j
@Aspect
@Component
public class BoardAop {

    @Around("execution(* org.study.board.service.BoardService.*(..))")
    //@Around("execution(* org.study.board.controller..*.*(..))")
    //@Around("execution(* org.study.board..*.*(..))")
    public Object logging(ProceedingJoinPoint pjp) throws Throwable {
        log.info("start -"+ pjp.getSignature().getDeclaringTypeName()+"/"+pjp.getSignature().getName());
        Object result=pjp.proceed();
        log.info("finished -"+ pjp.getSignature().getDeclaringTypeName()+"/"+pjp.getSignature().getName());
        return result;
    }

    @Before("execution(* org.study.board.controller.*.*(..)) && args(model, ..)")
    public void addUserToModel(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            String username = authentication.getName();
            model.addAttribute("user", username);
        } else {
            // 보안 컨텍스트가 없는 경우에 대한 처리를 여기에 추가합니다.
            // 예를 들어, 로깅 또는 디폴트 사용자를 설정하는 등의 방법으로 처리할 수 있습니다.
            model.addAttribute("user", "anonymousUser");
        }
    }

    @Before("execution(* org.study.board.controller.*.insertBoard(..))")
    public void checkUserAndInsertBoard(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        for (Object arg : args) {
            if (arg instanceof Board) {
                Board board = (Board) arg;
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                String username = authentication.getName();
                board.setWriter(username);
            }
        }
    }


}
