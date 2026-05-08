package com.javaclass.game.service;

import com.javaclass.game.constants.GameApiDefiner;
import com.javaclass.game.constants.PlayerItemDefiner;
import com.javaclass.game.dao.ItemDao;
import com.javaclass.game.dao.PlayerItemDao;
import com.javaclass.game.dto.DropRequest;
import com.javaclass.game.dto.PlayerItemResult;
import com.javaclass.game.model.Item;
import com.javaclass.game.model.PlayerItem;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class GameDropService {

    private static final int DEFAULT_INVENTORY_SIZE = 50;

    private final ItemDao itemDao;
    private final PlayerItemDao playerItemDao;
    private final PlayerItemService playerItemService;

    public GameDropService(ItemDao itemDao, PlayerItemDao playerItemDao, PlayerItemService playerItemService) {
        this.itemDao = itemDao;
        this.playerItemDao = playerItemDao;
        this.playerItemService = playerItemService;
    }

    @Transactional
    public PlayerItemResult rollDrop(Long playerId, DropRequest request) {
        List<Item> pool = itemDao.findAll();
        if (pool.isEmpty()) {
            throw new IllegalArgumentException(GameApiDefiner.ERROR_DROP_POOL_EMPTY);
        }

        Item item = pool.get(ThreadLocalRandom.current().nextInt(pool.size()));
        int amount = Math.max(1, request.getAmount() == null ? 1 : request.getAmount());
        int location = request.getTargetLocation() == null ? PlayerItemDefiner.LOCATION_INVENTORY : request.getTargetLocation();
        int position = request.getTargetPosition() == null ? findFirstEmptyPosition(playerId, location) : request.getTargetPosition();

        Optional<PlayerItem> existing = playerItemDao.findByPlayerIdAndLocationAndPosition(playerId, location, position);
        PlayerItem playerItem;
        if (existing.isPresent()) {
            playerItem = existing.get();
            if (!playerItem.getItemId().equals(item.getId())) {
                throw new IllegalArgumentException(GameApiDefiner.ERROR_TARGET_SLOT_OCCUPIED);
            }
            int maxAmount = item.getMaxAmount() == null ? amount : item.getMaxAmount();
            int nextAmount = playerItem.getAmount() + amount;
            if (nextAmount > maxAmount) {
                throw new IllegalArgumentException(PlayerItemDefiner.ERROR_MAX_AMOUNT_EXCEEDED);
            }
            playerItem.setAmount(nextAmount);
        } else {
            playerItem = new PlayerItem();
            playerItem.setPlayerId(playerId);
            playerItem.setItemId(item.getId());
            playerItem.setLocation(location);
            playerItem.setPosition(position);
            playerItem.setAmount(amount);
        }

        PlayerItem saved = playerItemDao.save(playerItem);
        return playerItemService.toPlayerItemResult(saved);
    }

    private int findFirstEmptyPosition(Long playerId, Integer location) {
        for (int i = 0; i < DEFAULT_INVENTORY_SIZE; i++) {
            if (playerItemDao.findByPlayerIdAndLocationAndPosition(playerId, location, i).isEmpty()) {
                return i;
            }
        }
        throw new IllegalArgumentException(GameApiDefiner.ERROR_TARGET_SLOT_OCCUPIED);
    }
}
