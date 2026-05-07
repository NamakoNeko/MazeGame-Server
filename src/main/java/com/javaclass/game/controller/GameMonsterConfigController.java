package com.javaclass.game.controller;

import com.javaclass.game.constants.GameApiDefiner;
import com.javaclass.game.service.GameMonsterConfigService;
import com.javaclass.game.utility.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(GameApiDefiner.MONSTER_BASE_URL)
public class GameMonsterConfigController {

    private final GameMonsterConfigService gameMonsterConfigService;

    public GameMonsterConfigController(GameMonsterConfigService gameMonsterConfigService) {
        this.gameMonsterConfigService = gameMonsterConfigService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<?>> list() {
        return ResponseEntity.ok(ApiResponse.success(gameMonsterConfigService.list()));
    }
}
