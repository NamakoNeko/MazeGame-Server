package com.javaclass.game.controller;

import com.javaclass.game.constants.PlayerDefiner;
import com.javaclass.game.dto.PlayerListResult;
import com.javaclass.game.dto.UpdatePlayerRequest;
import com.javaclass.game.dto.UpdatePlayerStatusRequest;
import com.javaclass.game.service.PlayerService;
import com.javaclass.game.utility.ApiResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(PlayerDefiner.PLAYER_BASE_URL)
public class AdminPlayerController {

    private final PlayerService playerService;

    public AdminPlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<?>> getPlayerList(
        @RequestParam(required = false) String keyword,
        @RequestParam(defaultValue = "" + PlayerDefiner.DEFAULT_PAGE)      int page,
        @RequestParam(defaultValue = "" + PlayerDefiner.DEFAULT_PAGE_SIZE) int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<PlayerListResult> playerPage = playerService.getPlayerList(keyword, pageable);
        return ResponseEntity.ok(ApiResponse.success(playerPage));
    }

    @GetMapping(PlayerDefiner.PLAYER_ACCOUNT_ID_PATH)
    public ResponseEntity<ApiResponse<?>> getPlayer(@PathVariable String accountId) {
        try {
            PlayerListResult playerListResult = playerService.getPlayerByAccountId(accountId);
            return ResponseEntity.ok(ApiResponse.success(playerListResult));
        } catch (IllegalArgumentException illegalArgumentException) {
            return ResponseEntity
                .badRequest()
                .body(ApiResponse.failure(404, illegalArgumentException.getMessage()));
        }
    }

    @PutMapping(PlayerDefiner.PLAYER_ID_PATH)
    public ResponseEntity<ApiResponse<?>> updatePlayer(
        @PathVariable Long id,
        @RequestBody UpdatePlayerRequest updatePlayerRequest
    ) {
        if (updatePlayerRequest.getNickname() == null || updatePlayerRequest.getNickname().isBlank()) {
            return ResponseEntity
                .badRequest()
                .body(ApiResponse.failure(400, PlayerDefiner.ERROR_NICKNAME_REQUIRED));
        }

        try {
            playerService.updatePlayer(id, updatePlayerRequest);
            return ResponseEntity.ok(ApiResponse.success(null));
        } catch (IllegalArgumentException illegalArgumentException) {
            return ResponseEntity
                .badRequest()
                .body(ApiResponse.failure(400, illegalArgumentException.getMessage()));
        }
    }

    @PatchMapping(PlayerDefiner.PLAYER_STATUS_PATH)
    @PreAuthorize("hasRole('SuperAdmin') or hasRole('Admin')")
    public ResponseEntity<ApiResponse<?>> updatePlayerStatus(
        @PathVariable String accountId,
        @RequestBody UpdatePlayerStatusRequest updatePlayerStatusRequest
    ) {
        if (updatePlayerStatusRequest.getStatus() == null || updatePlayerStatusRequest.getStatus().isBlank()) {
            return ResponseEntity
                .badRequest()
                .body(ApiResponse.failure(400, PlayerDefiner.ERROR_STATUS_INVALID));
        }

        try {
            playerService.updatePlayerStatus(accountId, updatePlayerStatusRequest);
            return ResponseEntity.ok(ApiResponse.success(null));
        } catch (IllegalArgumentException illegalArgumentException) {
            return ResponseEntity
                .badRequest()
                .body(ApiResponse.failure(400, illegalArgumentException.getMessage()));
        }
    }
}
