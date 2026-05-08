package com.javaclass.game.service;

import com.javaclass.game.constants.GameApiDefiner;
import com.javaclass.game.dao.HotkeyDao;
import com.javaclass.game.dao.PlayerItemDao;
import com.javaclass.game.dto.HotkeyRequest;
import com.javaclass.game.dto.HotkeyResult;
import com.javaclass.game.model.Hotkey;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GameHotkeyService {

    private final HotkeyDao hotkeyDao;
    private final PlayerItemDao playerItemDao;

    public GameHotkeyService(HotkeyDao hotkeyDao, PlayerItemDao playerItemDao) {
        this.hotkeyDao = hotkeyDao;
        this.playerItemDao = playerItemDao;
    }

    public List<HotkeyResult> list(Long playerId) {
        return hotkeyDao.findByPlayerIdOrderByKeyIndexAsc(playerId).stream()
            .map(this::toResult)
            .toList();
    }

    @Transactional
    public HotkeyResult set(Long playerId, Integer keyIndex, HotkeyRequest request) {
        validateKeyIndex(keyIndex);
        if (request.getPlayerItemId() != null) {
            playerItemDao.findById(request.getPlayerItemId())
                .filter(item -> item.getPlayerId().equals(playerId))
                .orElseThrow(() -> new IllegalArgumentException(GameApiDefiner.ERROR_ITEM_NOT_FOUND));
        }
        Hotkey hotkey = hotkeyDao.findByPlayerIdAndKeyIndex(playerId, keyIndex)
            .orElseGet(() -> {
                Hotkey created = new Hotkey();
                created.setPlayerId(playerId);
                created.setKeyIndex(keyIndex);
                return created;
            });
        hotkey.setPlayerItemId(request.getPlayerItemId());
        return toResult(hotkeyDao.save(hotkey));
    }

    @Transactional
    public void clear(Long playerId, Integer keyIndex) {
        validateKeyIndex(keyIndex);
        hotkeyDao.findByPlayerIdAndKeyIndex(playerId, keyIndex).ifPresent(hotkeyDao::delete);
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
