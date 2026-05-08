package com.javaclass.game.controller;

import com.javaclass.game.constants.GameApiDefiner;
import com.javaclass.game.dto.RunSettleRequest;
import com.javaclass.game.service.GameRunService;
import com.javaclass.game.utility.ApiResponse;
import com.javaclass.game.utility.JwtUtility;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(GameApiDefiner.RUN_BASE_URL)
public class GameRunController {

    private final GameRunService gameRunService;
    private final JwtUtility jwtUtility;

    public GameRunController(GameRunService gameRunService, JwtUtility jwtUtility) {
        this.gameRunService = gameRunService;
        this.jwtUtility = jwtUtility;
    }

    @PostMapping(GameApiDefiner.SETTLE_PATH)
    public ResponseEntity<ApiResponse<?>> settle(
        @RequestHeader("Authorization") String authorizationHeader,
        @RequestBody RunSettleRequest request
    ) {
        try {
            Long playerId = jwtUtility.extractPlayerId(jwtUtility.extractToken(authorizationHeader));
            return ResponseEntity.ok(ApiResponse.success(gameRunService.settle(playerId, request)));
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest().body(ApiResponse.failure(400, exception.getMessage()));
        }
    }
}
