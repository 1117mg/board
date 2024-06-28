package org.study.board.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

    @Transactional
    public void updateUserAuth(long idx, List<UserCtgAuth> auths) {
        mapper.deleteAuthByUserId(idx);
        for (UserCtgAuth auth : auths) {
            mapper.saveAuth(idx, auth);
        }
    }
}
