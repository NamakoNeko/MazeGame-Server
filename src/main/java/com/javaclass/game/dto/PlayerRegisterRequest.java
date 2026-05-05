package com.javaclass.game.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PlayerRegisterRequest {

    private String accountId;
    private String password;
    private String nickname;
    private String email;
}