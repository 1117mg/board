package org.study.board.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.study.board.dto.Category;
import org.study.board.dto.User;
import org.study.board.dto.UserCtgAuth;
import org.study.board.repository.AdminMapper;

import java.util.List;

@Service
public class AdminService {

    @Autowired
    private AdminMapper mapper;

    public List<UserCtgAuth> getUserAuth(long idx) {
        return mapper.findAuthByUserId(idx);
    }

    public void updateUserAuth(List<UserCtgAuth> auths,long idx) {
        mapper.deleteAuth(idx); // 모든 권한을 일괄 삭제
        mapper.saveAuth(auths); // 모든 사용자의 모든 카테고리 권한을 일괄 삽입
    }

    public List<Category> getAllCategories() {
        return mapper.findAllCategories();
    }

    public List<User> getAllUsers() {
        return mapper.findAllUsers();
    }

    public void updateUser(User user){
        mapper.updateUser(user);}

    @Transactional
    public void deleteUser(User user){
        mapper.backupUser(user);
        mapper.deleteUser(user);
    }
}
