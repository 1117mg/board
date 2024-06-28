package org.study.board.repository;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.study.board.dto.UserCtgAuth;

import java.util.List;

@Mapper
public interface AdminMapper {
    List<UserCtgAuth> findAuthByUserId(@Param("idx") long idx);
    void deleteAuthByUserId(@Param("idx") long idx);
    void saveAuth(@Param("idx") long idx, @Param("auth") UserCtgAuth auth);
}
