package com.javaclass.game.controller;

import com.javaclass.game.constants.GameApiDefiner;
import com.javaclass.game.service.GameItemPriceService;
import com.javaclass.game.utility.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(GameApiDefiner.ITEM_PRICE_BASE_URL)
public class GameItemPriceController {

    private final GameItemPriceService gameItemPriceService;

    public GameItemPriceController(GameItemPriceService gameItemPriceService) {
        this.gameItemPriceService = gameItemPriceService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<?>> list() {
        return ResponseEntity.ok(ApiResponse.success(gameItemPriceService.list()));
    }
}
