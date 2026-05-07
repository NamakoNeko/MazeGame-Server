package com.javaclass.game.service;

import com.javaclass.game.constants.GameApiDefiner;
import com.javaclass.game.dao.HotkeyDao;
import com.javaclass.game.dao.PlayerDao;
import com.javaclass.game.dao.PlayerItemDao;
import com.javaclass.game.dto.HotkeyRequest;
import com.javaclass.game.dto.HotkeyResult;
import com.javaclass.game.model.Hotkey;
import com.javaclass.game.model.Player;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GameHotkeyService {

    private final HotkeyDao hotkeyDao;
    private final PlayerDao playerDao;
    private final PlayerItemDao playerItemDao;

    public GameHotkeyService(HotkeyDao hotkeyDao, PlayerDao playerDao, PlayerItemDao playerItemDao) {
        this.hotkeyDao = hotkeyDao;
        this.playerDao = playerDao;
        this.playerItemDao = playerItemDao;
    }

    public List<HotkeyResult> list(String accountId) {
        Player player = getPlayer(accountId);
        return hotkeyDao.findByPlayerIdOrderByKeyIndexAsc(player.getId()).stream()
            .map(this::toResult)
            .toList();
    }

    @Transactional
    public HotkeyResult set(String accountId, Integer keyIndex, HotkeyRequest request) {
        validateKeyIndex(keyIndex);
        Player player = getPlayer(accountId);
        if (request.getPlayerItemId() != null) {
            playerItemDao.findById(request.getPlayerItemId())
                .filter(item -> item.getPlayerId().equals(player.getId()))
                .orElseThrow(() -> new IllegalArgumentException(GameApiDefiner.ERROR_ITEM_NOT_FOUND));
        }
        Hotkey hotkey = hotkeyDao.findByPlayerIdAndKeyIndex(player.getId(), keyIndex)
            .orElseGet(() -> {
                Hotkey created = new Hotkey();
                created.setPlayerId(player.getId());
                created.setKeyIndex(keyIndex);
                return created;
            });
        hotkey.setPlayerItemId(request.getPlayerItemId());
        return toResult(hotkeyDao.save(hotkey));
    }

    @Transactional
    public void clear(String accountId, Integer keyIndex) {
        validateKeyIndex(keyIndex);
        Player player = getPlayer(accountId);
        hotkeyDao.findByPlayerIdAndKeyIndex(player.getId(), keyIndex).ifPresent(hotkeyDao::delete);
    }

    private Player getPlayer(String accountId) {
        return playerDao.findByAccountId(accountId)
            .orElseThrow(() -> new IllegalArgumentException(GameApiDefiner.ERROR_PLAYER_NOT_FOUND));
    }

    private static void validateKeyIndex(Integer keyIndex) {
        if (keyIndex == null || keyIndex < 1 || keyIndex > 9) {
            throw new IllegalArgumentException(GameApiDefiner.ERROR_KEY_INDEX_INVALID);
        }
    }

    private HotkeyResult toResult(Hotkey hotkey) {
        return HotkeyResult.builder()
            .keyIndex(hotkey.getKeyIndex())
            .playerItemId(hotkey.getPlayerItemId())
            .build();
    }
}
