package org.study.board.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.study.board.controller.UserController;
import org.study.board.dto.Category;
import org.study.board.dto.JoinForm;
import org.study.board.dto.SnsUser;
import org.study.board.dto.User;
import org.study.board.repository.OauthMapper;
import org.study.board.repository.UserMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    @Autowired
    private UserMapper mapper;
    @Autowired
    private OauthMapper oauthMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User findById(long idx){return mapper.findById(idx);}

    public void loginWithToken(User user, String token){
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(user,token, new ArrayList<>());
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }

    public void join(JoinForm form) {
        User user = new User();
        user.setUserId(form.getLoginId());
        user.setUsername(form.getUsername());
        user.setPassword(passwordEncoder.encode(form.getPassword()));  // 비밀번호 암호화
        user.setRole(form.getLoginId().equals("admin") ? "ADMIN" : "USER");
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

    public User handleSnsLogin(String snsId, String snsType, String snsName) {
        // sns_id로 sns_user 정보 조회
        SnsUser snsUser = oauthMapper.selectSnsUserBySnsId(snsId);

        if (snsUser == null) {
            // sns_user가 존재하지 않으면, 해당 sns_id와 연동된 일반 사용자도 없음
            // 새로운 사용자 생성
            User newUser = new User();
            newUser.setUserId(snsId);
            newUser.setUsername(snsName);
            newUser.setPassword(passwordEncoder.encode("default_password")); // 임시 비밀번호 설정
            newUser.setRole("USER");
            mapper.save(newUser);

            User savedUser = mapper.findByLoginId(snsId);

            // sns_user 저장
            snsUser = SnsUser.builder()
                    .idx(savedUser.getIdx())
                    .snsId(snsId)
                    .snsType(snsType)
                    .snsName(snsName)
                    .snsConnectDate(new java.sql.Timestamp(System.currentTimeMillis()).toString())
                    .build();
            oauthMapper.insertSnsUser(snsUser);

            return newUser;
        } else {
            // sns_user가 존재하면, 연동된 일반 사용자 정보를 반환
            return findById(snsUser.getIdx());
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

}
