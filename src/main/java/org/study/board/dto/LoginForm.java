package org.study.board.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class LoginForm {

    @NotEmpty(message = "로그인 ID를 입력하세요.")
    private String loginId;

    @NotEmpty(message = "비밀번호를 입력하세요.")
    private String password;

    @NotEmpty(message = "전화번호를 입력하세요.")
    private String phoneNo;
}
