package com.javaclass.game.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreateAdminAccountRequest {

    private String account;
    private String password;
    private String role;
}
