package com.example.userservice.vo;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class RequestUser {

    @NotNull(message = "이메일을 입력하세요")
    @Size(min = 8, message = "이메일은 8글자 이상이여야 합니다.")
    @Email
    private String email;

    @NotNull(message = "이름을 입력하세요")
    @Size(min = 2, message = "이름은 2글자 이상이여야 합니다.")
    private String name;

    @NotNull(message = "비밀번호를 입력하세요")
    @Size(min = 6, message = "비밀번호는 6글자 이상이여야 합니다..")
    private String pwd;
}
