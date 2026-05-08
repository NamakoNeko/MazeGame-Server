package com.javaclass.game.controller;

import com.javaclass.game.constants.GameApiDefiner;
import com.javaclass.game.dto.BuyItemRequest;
import com.javaclass.game.service.GameShopService;
import com.javaclass.game.utility.ApiResponse;
import com.javaclass.game.utility.JwtUtility;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(GameApiDefiner.SHOP_BASE_URL)
public class GameShopController {

    private final GameShopService gameShopService;
    private final JwtUtility jwtUtility;

    public GameShopController(GameShopService gameShopService, JwtUtility jwtUtility) {
        this.gameShopService = gameShopService;
        this.jwtUtility = jwtUtility;
    }

    @GetMapping(GameApiDefiner.OFFERS_PATH)
    public ResponseEntity<ApiResponse<?>> offers() {
        return ResponseEntity.ok(ApiResponse.success(gameShopService.getOffers()));
    }

    @PostMapping(GameApiDefiner.REFRESH_PATH)
    public ResponseEntity<ApiResponse<?>> refresh() {
        return ResponseEntity.ok(ApiResponse.success(gameShopService.getOffers()));
    }

    @PostMapping(GameApiDefiner.BUY_PATH)
    public ResponseEntity<ApiResponse<?>> buy(
        @RequestHeader("Authorization") String authorizationHeader,
        @RequestBody BuyItemRequest request
    ) {
        try {
            Long playerId = jwtUtility.extractPlayerId(jwtUtility.extractToken(authorizationHeader));
            return ResponseEntity.ok(ApiResponse.success(gameShopService.buy(playerId, request)));
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest().body(ApiResponse.failure(400, exception.getMessage()));
        }
    }
}
