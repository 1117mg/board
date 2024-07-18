package org.study.board.repository;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.study.board.dto.UserBackup;
import org.study.board.dto.User;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface UserMapper {
    User findById(Long idx);
    User findByLoginId(String userId);
    User findByName(String username);
    User findByPhoneNo(String phoneNo);
    void save(User user);
    int existsByLoginId(@Param("loginId") String loginId);
    void updateStatus(User user); // 계정잠금 상태
    List<User> findLockedUsers(); // 잠긴 계정을 찾는 메서드
    UserBackup findRecentBackup(@Param("phoneNo") String phoneNo, @Param("backupDate") LocalDateTime backupDate);
    void deleteOldBackups();
}
