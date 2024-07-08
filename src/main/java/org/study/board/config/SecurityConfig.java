package org.study.board.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.study.board.handler.CustomAccessDeniedHandler;
import org.study.board.handler.CustomAuthenticationEntryPoint;
import org.study.board.handler.CustomAuthenticationFailureHandler;
import org.study.board.handler.CustomAuthenticationSuccessHandler;
import org.study.board.service.CustomUserDetailsService;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private CustomUserDetailsService userDetailsService;
    @Autowired
    private CustomAccessDeniedHandler accessDeniedHandler;
    @Autowired
    private CustomAuthenticationEntryPoint authenticationEntryPoint;
    @Autowired
    private CustomAuthenticationSuccessHandler authenticationSuccessHandler;
    @Autowired
    private CustomAuthenticationFailureHandler authenticationFailureHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                /*.antMatchers("/join").anonymous() // 회원가입 페이지는 인증되지 않은 사용자만 접근 가능
                .antMatchers("/check-username", "/main", "/0/main", "/1/main","/css/**", "/img/**").permitAll()
                .antMatchers("/user/main").hasRole("ADMIN")
                .antMatchers("/user/info/**").hasRole("ADMIN")
                .antMatchers("/admin/**").hasRole("ADMIN")*/
                /*.antMatchers("/admin/users","/admin/user/**").access("@customPermissionEvaluator.hasPermission(authentication, 'USER_LIST', 'READ')")
                .antMatchers("/admin/users","/admin/updateUser","/admin/user-auth").access("@customPermissionEvaluator.hasPermission(authentication, 'USER_LIST', 'WRITE')")
                .antMatchers("/0/board/**").access("@customPermissionEvaluator.hasPermission(authentication, 'NOTICE_BOARD', 'READ')")
                .antMatchers("/0/write").access("@customPermissionEvaluator.hasPermission(authentication, 'NOTICE_BOARD', 'WRITE')")
                .antMatchers("/0/downloadFile").access("@customPermissionEvaluator.hasPermission(authentication, 'NOTICE_BOARD', 'DOWNLOAD')")
                .antMatchers("/1/board/**").access("@customPermissionEvaluator.hasPermission(authentication, 'QNA_BOARD', 'READ')")
                .antMatchers("/1/write").access("@customPermissionEvaluator.hasPermission(authentication, 'QNA_BOARD', 'WRITE')")
                .antMatchers("/1/downloadFile").access("@customPermissionEvaluator.hasPermission(authentication, 'QNA_BOARD', 'DOWNLOAD')")*/
                .antMatchers("/check-username","/main","/login","/join","/0/main","/1/main","/css/**","/error/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .failureHandler(authenticationFailureHandler)
                .usernameParameter("loginId")  // 로그인 ID 필드 이름 설정
                .passwordParameter("password") // 비밀번호 필드 이름 설정
                .successHandler(authenticationSuccessHandler)
                //.defaultSuccessUrl("/main", true)  // 로그인 성공 후 리다이렉트 설정
                .permitAll()
                .and()
                .logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/main")  // 로그아웃 성공 후 리다이렉트 설정
                .permitAll()
                .and()
                .exceptionHandling()
                .accessDeniedHandler(accessDeniedHandler)  // Custom AccessDeniedHandler 등록
                .authenticationEntryPoint(authenticationEntryPoint)  // Custom AuthenticationEntryPoint 등록
                .and()
                .csrf().disable() // csrf 비활성화
                .rememberMe();
    }
}