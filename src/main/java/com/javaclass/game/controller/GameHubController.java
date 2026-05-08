package com.javaclass.game.controller;

import com.javaclass.game.constants.GameApiDefiner;
import com.javaclass.game.service.GameHubService;
import com.javaclass.game.utility.ApiResponse;
import com.javaclass.game.utility.JwtUtility;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(GameApiDefiner.HUB_BASE_URL)
public class GameHubController {

    private final GameHubService gameHubService;
    private final JwtUtility jwtUtility;

    public GameHubController(GameHubService gameHubService, JwtUtility jwtUtility) {
        this.gameHubService = gameHubService;
        this.jwtUtility = jwtUtility;
    }

    @PostMapping(GameApiDefiner.BED_SAVE_PATH)
    public ResponseEntity<ApiResponse<?>> saveBed(@RequestHeader("Authorization") String authorizationHeader) {
        try {
            Long playerId = jwtUtility.extractPlayerId(jwtUtility.extractToken(authorizationHeader));
            return ResponseEntity.ok(ApiResponse.success(gameHubService.saveBed(playerId)));
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest().body(ApiResponse.failure(400, exception.getMessage()));
        }
    }

    @PostMapping(GameApiDefiner.PORTAL_VALIDATE_PATH)
    public ResponseEntity<ApiResponse<?>> validatePortal(@RequestHeader("Authorization") String authorizationHeader) {
        try {
            Long playerId = jwtUtility.extractPlayerId(jwtUtility.extractToken(authorizationHeader));
            return ResponseEntity.ok(ApiResponse.success(gameHubService.validatePortal(playerId)));
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest().body(ApiResponse.failure(400, exception.getMessage()));
        }
    }

    @PostMapping(GameApiDefiner.STORAGE_OPEN_PATH)
    public ResponseEntity<ApiResponse<?>> openStorage(@RequestHeader("Authorization") String authorizationHeader) {
        try {
            Long playerId = jwtUtility.extractPlayerId(jwtUtility.extractToken(authorizationHeader));
            return ResponseEntity.ok(ApiResponse.success(gameHubService.openStorage(playerId)));
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest().body(ApiResponse.failure(400, exception.getMessage()));
        }
    }
}
