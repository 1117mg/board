package org.study.board.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.study.board.dto.User;
import org.study.board.repository.UserMapper;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserMapper mapper;

    public User login(String userId, String password) {
        Optional<User> userOptional = mapper.findByLoginId(userId);
        // 값이 존재하는지 확인
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            // 비밀번호 일치 여부 확인
            if (user.getPassword().equals(password)) {
                return user;
            } else {
                // 비밀번호가 일치하지 않는 경우
                throw new RuntimeException("비밀번호가 일치하지 않습니다.");
            }
        } else {
            // 사용자가 존재하지 않는 경우
            throw new RuntimeException("사용자를 찾을 수 없습니다.");
        }
    }
}
