package com.javaclass.game.controller;

import com.javaclass.game.constants.GameApiDefiner;
import com.javaclass.game.dto.HotkeyRequest;
import com.javaclass.game.service.GameHotkeyService;
import com.javaclass.game.utility.ApiResponse;
import com.javaclass.game.utility.JwtUtility;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(GameApiDefiner.HOTKEY_BASE_URL)
public class GameHotkeyController {

    private final GameHotkeyService gameHotkeyService;
    private final JwtUtility jwtUtility;

    public GameHotkeyController(GameHotkeyService gameHotkeyService, JwtUtility jwtUtility) {
        this.gameHotkeyService = gameHotkeyService;
        this.jwtUtility = jwtUtility;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<?>> list(@RequestHeader("Authorization") String authorizationHeader) {
        Long playerId = jwtUtility.extractPlayerId(jwtUtility.extractToken(authorizationHeader));
        return ResponseEntity.ok(ApiResponse.success(gameHotkeyService.list(playerId)));
    }

    @PutMapping("/{keyIndex}")
    public ResponseEntity<ApiResponse<?>> set(
        @RequestHeader("Authorization") String authorizationHeader,
        @PathVariable Integer keyIndex,
        @RequestBody HotkeyRequest request
    ) {
        try {
            Long playerId = jwtUtility.extractPlayerId(jwtUtility.extractToken(authorizationHeader));
            return ResponseEntity.ok(ApiResponse.success(gameHotkeyService.set(playerId, keyIndex, request)));
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest().body(ApiResponse.failure(400, exception.getMessage()));
        }
    }

    @DeleteMapping("/{keyIndex}")
    public ResponseEntity<ApiResponse<?>> clear(
        @RequestHeader("Authorization") String authorizationHeader,
        @PathVariable Integer keyIndex
    ) {
        try {
            Long playerId = jwtUtility.extractPlayerId(jwtUtility.extractToken(authorizationHeader));
            gameHotkeyService.clear(playerId, keyIndex);
            return ResponseEntity.ok(ApiResponse.success(null));
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest().body(ApiResponse.failure(400, exception.getMessage()));
        }
    }
}
