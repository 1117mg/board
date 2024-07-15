package org.study.board.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.study.board.dto.JoinForm;
import org.study.board.dto.SnsUser;
import org.study.board.dto.User;
import org.study.board.repository.OauthMapper;
import org.study.board.repository.UserMapper;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
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

    public User findByLoginId(String userId){return mapper.findByLoginId(userId);}

    public User findByName(String username){return mapper.findByName(username);}

    public void loginWithToken(String username, String snsType, String token){
        SnsUser user=oauthMapper.findSnsUserBySNST(username, snsType);
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
        user.setPhoneNo(form.getPhoneNo());
        mapper.save(user);

        // sns 가입자일 경우
        if(form.getSnsType()!=null){
            snsJoin(form.getLoginId(), form.getUsername(), form.getSnsType());
        }
    }

    public void snsJoin(@NotBlank String loginId,@NotBlank String username, String snsType){
        //User user=mapper.findByLoginId(loginId);
        User user=mapper.findByName(username);
        SnsUser snsUser = SnsUser.builder()
                .snsId(loginId)
                .snsType(snsType)
                .snsConnectDate(new java.sql.Timestamp(System.currentTimeMillis()).toString())
                .gno(user.getIdx())
                .snsName(user.getUsername())
                .build();
        oauthMapper.insertSnsUser(snsUser);
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

    public SnsUser findSnsUser(String snsName, String snsType){
        SnsUser snsUser = oauthMapper.findSnsUserBySNST(snsName,snsType);
        return snsUser;
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
