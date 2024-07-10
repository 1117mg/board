package org.study.board.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.study.board.dto.User;
import org.study.board.repository.UserMapper;

import java.util.Collections;

@Slf4j
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserMapper mapper;

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        User user = mapper.findByLoginId(userId);

        if (user == null) {
            throw new UsernameNotFoundException("User not found with username: " + userId);
        }

        /*if (user.isLocked()) {
            throw new LockedException("User account is locked");
        }*/

        // 권한 설정
        /*String rolePrefix = "ROLE_"; // Spring Security 규칙에 따라 권한 이름은 "ROLE_"로 시작해야 함
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(rolePrefix + user.getRole());

        log.info("User: " + username + ", Role: " + authority.getAuthority());

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUserId())
                .password(user.getPassword())
                //.authorities(Collections.singletonList(authority))
                //.accountLocked(user.isLocked())  // locked 상태를 UserDetails에 반영
                .build();*/

        log.info("User: " + userId + ", Role: " + user.getRole());

        return user;
    }
}