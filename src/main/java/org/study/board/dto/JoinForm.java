package org.study.board.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
public class JoinForm {

    @NotBlank
    private String loginId;

    @NotBlank
    private String password;
    private String passwordCheck;

    @NotBlank
    private String username;

}