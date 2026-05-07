package com.javaclass.game.controller;

import com.javaclass.game.constants.GameApiDefiner;
import com.javaclass.game.service.GameItemCatalogService;
import com.javaclass.game.utility.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(GameApiDefiner.ITEM_BASE_URL)
public class GameItemCatalogController {

    private final GameItemCatalogService gameItemCatalogService;

    public GameItemCatalogController(GameItemCatalogService gameItemCatalogService) {
        this.gameItemCatalogService = gameItemCatalogService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<?>> listItems() {
        return ResponseEntity.ok(ApiResponse.success(gameItemCatalogService.listItems()));
    }
}
