package org.study.board.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.study.board.dto.User;
import org.study.board.repository.UserMapper;

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

        log.info("User: " + userId + ", Role: " + user.getRole());

        return user;
    }
}