package org.study.board.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.study.board.controller.UserController;
import org.study.board.dto.JoinForm;
import org.study.board.dto.User;
import org.study.board.repository.UserMapper;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    @Autowired
    private UserMapper mapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User findById(long idx){return mapper.findById(idx);}

    public List<User> getAllUsers() {
        return mapper.findAllUsers();
    }


    public void join(JoinForm form) {
        User user = new User();
        user.setUserId(form.getLoginId());
        user.setUsername(form.getUsername());
        user.setPassword(passwordEncoder.encode(form.getPassword()));  // 비밀번호 암호화
        user.setRole(form.getLoginId().equals("admin") ? "ROLE_ADMIN" : "ROLE_USER");
        mapper.save(user);
    }

    public boolean checkLoginIdDuplicate(String loginId) {
        log.info("Checking loginId in service: {}", loginId);
        int count = mapper.existsByLoginId(loginId);
        log.info("Count result: {}", count);
        return count > 0;
    }

    public User login(String userId, String password) {
        Optional<User> userOptional = Optional.ofNullable(mapper.findByLoginId(userId));
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (passwordEncoder.matches(password, user.getPassword())) {  // 비밀번호 확인
                return user;
            } else {
                throw new RuntimeException("비밀번호가 일치하지 않습니다.");
            }
        } else {
            throw new RuntimeException("사용자를 찾을 수 없습니다.");
        }
    }

    public void updateStatus(User user){
        mapper.updateStatus(user);
    }

    public User getLoginUser(String userId) {
        if(userId == null) return null;

        Optional<User> optionalUser = Optional.ofNullable(mapper.findByLoginId(userId));
        return optionalUser.orElse(null);
    }

    public void updateUser(User user){
        mapper.updateUser(user);}
}
