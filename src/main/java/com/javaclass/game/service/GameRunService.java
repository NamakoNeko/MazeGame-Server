package com.javaclass.game.service;

import com.javaclass.game.constants.GameApiDefiner;
import com.javaclass.game.dao.PlayerStatsDao;
import com.javaclass.game.dto.RunSettleRequest;
import com.javaclass.game.dto.RunSettleResponse;
import com.javaclass.game.model.PlayerStats;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GameRunService {

    private final PlayerStatsDao playerStatsDao;

    public GameRunService(PlayerStatsDao playerStatsDao) {
        this.playerStatsDao = playerStatsDao;
    }

    @Transactional
    public RunSettleResponse settle(Long playerId, RunSettleRequest request) {
        PlayerStats stats = playerStatsDao.findById(playerId)
            .orElseThrow(() -> new IllegalArgumentException(GameApiDefiner.ERROR_STATS_NOT_FOUND));

        boolean success = Boolean.TRUE.equals(request.getSuccess());
        int coinsEarned = Math.max(0, request.getCoinsEarned() == null ? 0 : request.getCoinsEarned());
        if (success && coinsEarned > 0) {
            stats.setMoney((stats.getMoney() == null ? 0L : stats.getMoney()) + coinsEarned);
            playerStatsDao.save(stats);
        }

        return RunSettleResponse.builder()
            .success(success)
            .coinsEarned(success ? coinsEarned : 0)
            .elapsedSeconds(Math.max(0, request.getElapsedSeconds() == null ? 0 : request.getElapsedSeconds()))
            .money(stats.getMoney())
            .build();
    }
}
