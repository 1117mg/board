package org.study.board.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.study.board.dto.UserCtgAuth;
import org.study.board.repository.AdminMapper;
import org.study.board.service.CustomUserDetailsService;

import java.util.List;

@Component
public class CustomPermissionEvaluator {

    @Autowired
    private AdminMapper mapper;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    public boolean hasPermission(Authentication authentication, String permissionKey, String permissionType) {
        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails)) {
            return false;
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        // 사용자 서비스를 통해 사용자 정보 가져오기
        UserDetails userDetailsFromService = customUserDetailsService.loadUserByUsername(userDetails.getUsername());
        Long userIdx = ((org.study.board.dto.User) userDetailsFromService).getIdx();

        List<UserCtgAuth> userCtgAuthList = mapper.findAuthByUserId(userIdx);

        for (UserCtgAuth userCtgAuth : userCtgAuthList) {
            if (("USER_LIST".equals(permissionKey) && userCtgAuth.getCtgNo() == 1) ||
                    ("NOTICE_BOARD".equals(permissionKey) && userCtgAuth.getCtgNo() == 4) ||
                    ("QNA_BOARD".equals(permissionKey) && userCtgAuth.getCtgNo() == 5)) {

                if ("READ".equals(permissionType) && userCtgAuth.isCanRead()) {
                    return true;
                } else if ("WRITE".equals(permissionType) && userCtgAuth.isCanWrite()) {
                    return true;
                }
            }
        }

        return false;
    }
}