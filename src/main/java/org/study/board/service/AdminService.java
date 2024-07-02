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
    public void updateUserAuth(long userIdx, List<UserCtgAuth> auths) {
        mapper.deleteAuthByUserId(userIdx);
        for (UserCtgAuth auth : auths) {
            mapper.saveAuth(userIdx, auth);
        }
    }
}
