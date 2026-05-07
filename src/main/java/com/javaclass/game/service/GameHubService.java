package com.javaclass.game.service;

import com.javaclass.game.constants.GameApiDefiner;
import com.javaclass.game.dao.PlayerDao;
import com.javaclass.game.dto.HubInteractionResponse;
import org.springframework.stereotype.Service;

@Service
public class GameHubService {

    private final PlayerDao playerDao;

    public GameHubService(PlayerDao playerDao) {
        this.playerDao = playerDao;
    }

    public HubInteractionResponse saveBed(String accountId) {
        ensurePlayer(accountId);
        return HubInteractionResponse.builder()
            .action("BED_SAVE")
            .allowed(true)
            .message("saved")
            .build();
    }

    public HubInteractionResponse validatePortal(String accountId) {
        ensurePlayer(accountId);
        return HubInteractionResponse.builder()
            .action("PORTAL_VALIDATE")
            .allowed(true)
            .message("allowed")
            .build();
    }

    public HubInteractionResponse openStorage(String accountId) {
        ensurePlayer(accountId);
        return HubInteractionResponse.builder()
            .action("STORAGE_OPEN")
            .allowed(true)
            .message("allowed")
            .build();
    }

    private void ensurePlayer(String accountId) {
        playerDao.findByAccountId(accountId)
            .orElseThrow(() -> new IllegalArgumentException(GameApiDefiner.ERROR_PLAYER_NOT_FOUND));
    }
}
