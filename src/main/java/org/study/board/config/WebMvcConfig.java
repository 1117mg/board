package org.study.board.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Autowired
    private AuthInterceptor sessionInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 명시하는 요청 설계 주소에서만 동작하도록

        // localhost:8080/hello <-- 동작x
        // localhost:8080/user/info/{userId} <-- 인터셉터 동작
        registry.addInterceptor(sessionInterceptor)
                .addPathPatterns("/board/**"); // user 아래에 있는것만 가로챔
    }
}
