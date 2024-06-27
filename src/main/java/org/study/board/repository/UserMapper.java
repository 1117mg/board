package org.study.board.repository;

import org.apache.ibatis.annotations.Mapper;
import org.study.board.dto.JoinForm;
import org.study.board.dto.User;

import java.util.List;


@Mapper
public interface UserMapper {
    User findById(Long idx);
    User findByLoginId(String userId);
    void save(User user);
    int existsByLoginId(String loginId);
    List<User> findAllUsers();
    void updateStatus(User user); // 계정잠금 상태
    List<User> findLockedUsers(); // 잠긴 계정을 찾는 메서드
    void updateUser(User user); // 사용자 정보 수정
}
