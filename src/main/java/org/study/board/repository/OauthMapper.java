package org.study.board.repository;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.study.board.dto.SnsUser;

@Mapper
public interface OauthMapper {
    SnsUser findSnsUserBySIST(@Param("snsId") String snsId, @Param("snsType") String snsType);
    void insertSnsUser(SnsUser user);
}
