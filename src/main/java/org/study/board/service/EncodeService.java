package org.study.board.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class EncodeService {
    // 회원 삭제 전, 백업 테이블에 저장할 회원의 개인정보에 대해 암호화가 필요하다.

    // phone_no 암호화
    public String encryptPhoneNo(String phoneNo) {
        // 대쉬('-')를 제거하고 암호화
        return phoneNo.replaceAll("-", "");
    }

    // username 암호화
    public String encryptUsername(String username) {
        // 문자열을 뒤집어서 암호화
        return new StringBuilder(username).reverse().toString();
    }
}
