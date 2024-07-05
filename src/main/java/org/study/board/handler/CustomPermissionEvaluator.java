package org.study.board.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.study.board.repository.AdminMapper;

@Component
public class CustomPermissionEvaluator {

    @Autowired
    private AdminMapper mapper;

    public boolean hasPermission(Authentication authentication, String permissionKey, String permissionType) {
        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails)) {
            return false;
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Long userIdx = ((org.study.board.dto.User) userDetails).getIdx();
        Integer ctgNo = getCtgNoByPermissionKey(permissionKey);

        if (ctgNo == null) {
            return false;
        }

        // 디버깅 로그 추가
        System.out.println("Checking permission for userIdx: " + userIdx + ", ctgNo: " + ctgNo + ", permissionType: " + permissionType);

        return mapper.checkUserPermission(userIdx, ctgNo, permissionType) > 0;
    }

    private Integer getCtgNoByPermissionKey(String permissionKey) {
        switch (permissionKey) {
            case "USER_LIST":
                return 1; // USER_LIST에 해당하는 ctg_no를 반환
            case "NOTICE_BOARD":
                return 2; // NOTICE_BOARD에 해당하는 ctg_no를 반환
            case "QNA_BOARD":
                return 3; // QNA_BOARD에 해당하는 ctg_no를 반환
            default:
                return null;
        }
    }
}
