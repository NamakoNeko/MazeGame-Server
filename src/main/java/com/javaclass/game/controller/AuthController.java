package com.javaclass.game.controller;

import com.javaclass.game.model.LoginResponse;
import com.javaclass.game.service.AuthService;
import com.javaclass.game.utility.ApiResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<?>> login(@RequestBody LoginRequest loginRequest) {
        if (loginRequest.getAccount() == null || loginRequest.getAccount().isBlank()) {
            return ResponseEntity
                .badRequest()
                .body(ApiResponse.failure(400, "帳號為必填欄位"));
        }

        if (loginRequest.getPassword() == null || loginRequest.getPassword().isBlank()) {
            return ResponseEntity
                .badRequest()
                .body(ApiResponse.failure(400, "密碼為必填欄位"));
        }

        try {
            LoginResponse loginResponse = authService.login(
                loginRequest.getAccount(),
                loginRequest.getPassword()
            );
            return ResponseEntity.ok(ApiResponse.success(loginResponse));
        } catch (IllegalArgumentException invalidCredentialsException) {
            return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.failure(401, invalidCredentialsException.getMessage()));
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