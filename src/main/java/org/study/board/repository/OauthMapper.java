package org.study.board.repository;

import org.apache.ibatis.annotations.Param;
import org.study.board.dto.SnsUser;

public interface OauthMapper {
    SnsUser selectSnsUserBySnsId(@Param("snsId") String snsId);
    void insertSnsUser(SnsUser user);
}
