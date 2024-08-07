package org.study.board.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User implements UserDetails, OAuth2User {

    private Long idx;
    private String userId;
    private String password;
    private String username;
    private Timestamp regdate;
    private String role;
    private int failedAttempts;
    private boolean locked;
    private Timestamp lockTime; // 계정 잠금 시간
    private String phoneNo;

    // 구글 로그인
    private String provider;
    private String providerId;

    private Map<String, Object> attributes;

    @Override
    public String getName() {
        return null;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    // 로그인 실패 횟수 증가
    public void incrementFailedAttempts() {
        this.failedAttempts++;
    }

    @Override
    public boolean isAccountNonExpired() {
        // 계정 만료 여부 로직 (예: 만료되지 않았다고 가정)
        return true;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(role));
        return authorities;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !locked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        // 자격 증명 만료 여부 로직 (예: 만료되지 않았다고 가정)
        return true;
    }

    @Override
    public boolean isEnabled() {
        // 계정 활성화 여부 로직 (예: 활성화되었다고 가정)
        return true;
    }

    /*페이징 기능*/
    private Integer pageNo;	    // 현재 페이지 번호
    private int pageSize;	    // 페이지 당 항목 수
    private int pageOffset; 	// 현재 페이지 이전 항목 수
}