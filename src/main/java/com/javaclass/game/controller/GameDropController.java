package com.javaclass.game.controller;

import com.javaclass.game.constants.GameApiDefiner;
import com.javaclass.game.dto.DropRequest;
import com.javaclass.game.service.GameDropService;
import com.javaclass.game.utility.ApiResponse;
import com.javaclass.game.utility.JwtUtility;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(GameApiDefiner.DROP_BASE_URL)
public class GameDropController {

    private final GameDropService gameDropService;
    private final JwtUtility jwtUtility;

    public GameDropController(GameDropService gameDropService, JwtUtility jwtUtility) {
        this.gameDropService = gameDropService;
        this.jwtUtility = jwtUtility;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<?>> drop(
        @RequestHeader("Authorization") String authorizationHeader,
        @RequestBody DropRequest request
    ) {
        try {
            Long playerId = jwtUtility.extractPlayerId(jwtUtility.extractToken(authorizationHeader));
            return ResponseEntity.ok(ApiResponse.success(gameDropService.rollDrop(playerId, request)));
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest().body(ApiResponse.failure(400, exception.getMessage()));
        }
    }
}
