package org.study.board.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {

    private Long idx;
    private String userId;
    private String password;
    private String username;
    private Timestamp regdate;
    private String role;
    private int failedAttempts;
    private boolean locked;

    // 로그인 실패 횟수 증가
    public void incrementFailedAttempts() {
        this.failedAttempts++;
    }

}