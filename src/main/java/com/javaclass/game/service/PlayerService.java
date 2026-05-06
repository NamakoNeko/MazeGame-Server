package com.javaclass.game.service;

import com.javaclass.game.constants.PlayerDefiner;
import com.javaclass.game.dao.PlayerDao;
import com.javaclass.game.dto.PlayerListResult;
import com.javaclass.game.dto.UpdatePlayerRequest;
import com.javaclass.game.dto.UpdatePlayerStatusRequest;
import com.javaclass.game.model.Player;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
public class PlayerService {

    private static final Set<String> VALID_STATUS_SET = Set.of(
        PlayerDefiner.STATUS_ACTIVE,
        PlayerDefiner.STATUS_BANNED
    );

    private final PlayerDao playerDao;

    public PlayerService(PlayerDao playerDao) {
        this.playerDao = playerDao;
    }

    public Page<PlayerListResult> getPlayerList(String keyword, Pageable pageable) {
        Page<Player> playerPage = playerDao.findByKeyword(keyword, pageable);

        List<PlayerListResult> playerListResultList = playerPage.getContent().stream()
            .map(this::toPlayerListResult)
            .toList();

        return new PageImpl<>(playerListResultList, pageable, playerPage.getTotalElements());
    }

    public PlayerListResult getPlayer(Long playerId) {
        Player player = playerDao.findById(playerId)
            .orElseThrow(() -> new IllegalArgumentException(PlayerDefiner.ERROR_PLAYER_NOT_FOUND));

        return toPlayerListResult(player);
    }

    public PlayerListResult getPlayerByAccountId(String accountId) {
        Player player = playerDao.findByAccountId(accountId)
            .orElseThrow(() -> new IllegalArgumentException(PlayerDefiner.ERROR_PLAYER_NOT_FOUND));

        return toPlayerListResult(player);
    }

    @Transactional
    public void updatePlayer(Long playerId, UpdatePlayerRequest updatePlayerRequest) {
        Player player = playerDao.findById(playerId)
            .orElseThrow(() -> new IllegalArgumentException(PlayerDefiner.ERROR_PLAYER_NOT_FOUND));

        if (updatePlayerRequest.getNickname() != null && !updatePlayerRequest.getNickname().isBlank()) {
            player.setNickname(updatePlayerRequest.getNickname());
        }

        if (updatePlayerRequest.getLevel() != null) {
            player.setLevel(updatePlayerRequest.getLevel());
        }

        playerDao.save(player);
    }

    @Transactional
    public void updatePlayerStatus(String accountId, UpdatePlayerStatusRequest updatePlayerStatusRequest) {
        boolean isStatusValid = VALID_STATUS_SET.contains(updatePlayerStatusRequest.getStatus());
        if (!isStatusValid) {
            throw new IllegalArgumentException(PlayerDefiner.ERROR_STATUS_INVALID);
        }

        Player player = playerDao.findByAccountId(accountId)
            .orElseThrow(() -> new IllegalArgumentException(PlayerDefiner.ERROR_PLAYER_NOT_FOUND));

        player.setStatus(updatePlayerStatusRequest.getStatus());
        playerDao.save(player);
    }

    private PlayerListResult toPlayerListResult(Player player) {
        return PlayerListResult.builder()
            .id(player.getId())
            .accountId(player.getAccountId())
            .nickname(player.getNickname())
            .level(player.getLevel())
            .status(player.getStatus())
            .createdAt(player.getCreatedAt())
            .lastLoginAt(player.getLastLoginAt())
            .build();
    }
}