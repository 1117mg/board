package org.study.board.repository;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.study.board.dto.User;

import java.util.*;

@Mapper
public interface UserMapper {
    User findById(Long idx);
    User findByLoginId(String userId);

}
