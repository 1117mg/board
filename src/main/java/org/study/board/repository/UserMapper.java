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
}
