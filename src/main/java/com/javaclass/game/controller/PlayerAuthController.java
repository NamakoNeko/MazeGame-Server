package com.javaclass.game.controller;

import com.javaclass.game.constants.AuthDefiner;
import com.javaclass.game.dto.PlayerLoginResponse;
import com.javaclass.game.dto.PlayerRegisterRequest;
import com.javaclass.game.dto.PlayerRegisterResponse;
import com.javaclass.game.service.PlayerAuthService;
import com.javaclass.game.utility.ApiResponse;
import com.javaclass.game.utility.PlayerBannedException;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(AuthDefiner.PLAYER_AUTH_BASE_URL)
public class PlayerAuthController {

    private final PlayerAuthService playerAuthService;

    public PlayerAuthController(PlayerAuthService playerAuthService) {
        this.playerAuthService = playerAuthService;
    }

    @PostMapping(AuthDefiner.LOGIN_PATH)
    public ResponseEntity<ApiResponse<?>> login(@RequestBody LoginRequest loginRequest) {
        if (loginRequest.getAccount() == null || loginRequest.getAccount().isBlank()) {
            return ResponseEntity
                .badRequest()
                .body(ApiResponse.failure(400, AuthDefiner.ERROR_ACCOUNT_REQUIRED));
        }

        if (loginRequest.getPassword() == null || loginRequest.getPassword().isBlank()) {
            return ResponseEntity
                .badRequest()
                .body(ApiResponse.failure(400, AuthDefiner.ERROR_PASSWORD_REQUIRED));
        }

        try {
            PlayerLoginResponse playerLoginResponse = playerAuthService.login(
                loginRequest.getAccount(),
                loginRequest.getPassword()
            );
            return ResponseEntity.ok(ApiResponse.success(playerLoginResponse));
        } catch (PlayerBannedException playerBannedException) {
            return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.failure(403, playerBannedException.getMessage()));
        } catch (IllegalArgumentException invalidCredentialsException) {
            return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.failure(401, invalidCredentialsException.getMessage()));
        }
    }

    @PostMapping(AuthDefiner.REGISTER_PATH)
    public ResponseEntity<ApiResponse<?>> register(@RequestBody PlayerRegisterRequest registerRequest) {
        if (registerRequest.getAccountId() == null || registerRequest.getAccountId().isBlank()) {
            return ResponseEntity
                .badRequest()
                .body(ApiResponse.failure(400, AuthDefiner.ERROR_ACCOUNT_REQUIRED));
        }

        if (registerRequest.getPassword() == null || registerRequest.getPassword().isBlank()) {
            return ResponseEntity
                .badRequest()
                .body(ApiResponse.failure(400, AuthDefiner.ERROR_PASSWORD_REQUIRED));
        }

        if (registerRequest.getNickname() == null || registerRequest.getNickname().isBlank()) {
            return ResponseEntity
                .badRequest()
                .body(ApiResponse.failure(400, AuthDefiner.ERROR_NICKNAME_REQUIRED));
        }

        if (registerRequest.getEmail() == null || registerRequest.getEmail().isBlank()) {
            return ResponseEntity
                .badRequest()
                .body(ApiResponse.failure(400, AuthDefiner.ERROR_EMAIL_REQUIRED));
        }

        try {
            PlayerRegisterResponse playerRegisterResponse = playerAuthService.register(registerRequest);
            return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.created(playerRegisterResponse));
        } catch (IllegalArgumentException duplicateAccountException) {
            return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ApiResponse.failure(409, duplicateAccountException.getMessage()));
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class LoginRequest {
        private String account;
        private String password;
    }
}