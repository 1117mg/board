package org.study.board.repository;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.study.board.dto.SnsUser;

@Mapper
public interface OauthMapper {
    SnsUser findSnsUserBySNST(@Param("snsName") String snsName, @Param("snsType") String snsType);
    void insertSnsUser(SnsUser user);
}
