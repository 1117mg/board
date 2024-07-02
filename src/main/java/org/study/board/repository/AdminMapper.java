package org.study.board.repository;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.study.board.dto.UserCtgAuth;

import java.util.List;

@Mapper
public interface AdminMapper {
    List<UserCtgAuth> findAuthByUserId(@Param("idx") long idx);
    void deleteAuth();
    void saveAuth(@Param("auths") List<UserCtgAuth> auths);
}
