package org.study.board.repository;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.study.board.dto.Category;
import org.study.board.dto.User;
import org.study.board.dto.UserCtgAuth;

import java.util.List;

@Mapper
public interface AdminMapper {
    List<UserCtgAuth> findAuthByUserId(@Param("idx") long idx);
    void deleteAuth(long idx);
    void saveAuth(@Param("auths") List<UserCtgAuth> auths);
    List<Category> findAllCategories();
    void updateUser(User user); // 사용자 정보 수정
    List<User> findAllUsers();
    // 쿼리문으로 권한 체크
    int checkUserPermission(@Param("userIdx") Long userIdx, @Param("ctgNo") Integer ctgNo, @Param("permissionType") String permissionType);
    void deleteUser(User user); //회원 삭제
    void backupUser(User user);
}
