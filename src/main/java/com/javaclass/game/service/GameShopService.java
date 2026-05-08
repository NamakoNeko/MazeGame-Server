package com.javaclass.game.service;

import com.javaclass.game.constants.GameApiDefiner;
import com.javaclass.game.dao.ItemDao;
import com.javaclass.game.dao.ItemPriceDao;
import com.javaclass.game.dao.PlayerItemDao;
import com.javaclass.game.dao.PlayerStatsDao;
import com.javaclass.game.dto.BuyItemRequest;
import com.javaclass.game.dto.BuyItemResponse;
import com.javaclass.game.dto.ItemCatalogResult;
import com.javaclass.game.model.Item;
import com.javaclass.game.model.ItemPrice;
import com.javaclass.game.model.PlayerItem;
import com.javaclass.game.model.PlayerStats;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
public class GameShopService {

    private final ItemDao itemDao;
    private final ItemPriceDao itemPriceDao;
    private final PlayerItemDao playerItemDao;
    private final PlayerStatsDao playerStatsDao;
    private final GameItemCatalogService gameItemCatalogService;

    public GameShopService(
        ItemDao itemDao,
        ItemPriceDao itemPriceDao,
        PlayerItemDao playerItemDao,
        PlayerStatsDao playerStatsDao,
        GameItemCatalogService gameItemCatalogService
    ) {
        this.itemDao = itemDao;
        this.itemPriceDao = itemPriceDao;
        this.playerItemDao = playerItemDao;
        this.playerStatsDao = playerStatsDao;
        this.gameItemCatalogService = gameItemCatalogService;
    }

    public List<ItemCatalogResult> getOffers() {
        List<Item> items = itemDao.findAll();
        Collections.shuffle(items);
        return items.stream().limit(6).map(gameItemCatalogService::toResult).toList();
    }

    @Transactional
    public BuyItemResponse buy(Long playerId, BuyItemRequest request) {
        int amount = request.getAmount() == null ? 1 : request.getAmount();
        if (amount <= 0) {
            throw new IllegalArgumentException(GameApiDefiner.ERROR_INVALID_AMOUNT);
        }
        if (request.getLocation() == null || request.getPosition() == null) {
            throw new IllegalArgumentException(GameApiDefiner.ERROR_INVALID_POSITION);
        }

        Item item = itemDao.findById(request.getItemId())
            .orElseThrow(() -> new IllegalArgumentException(GameApiDefiner.ERROR_ITEM_NOT_FOUND));
        ItemPrice price = itemPriceDao.findById(item.getId())
            .orElseThrow(() -> new IllegalArgumentException(GameApiDefiner.ERROR_PRICE_NOT_FOUND));
        PlayerStats stats = playerStatsDao.findById(playerId)
            .orElseThrow(() -> new IllegalArgumentException(GameApiDefiner.ERROR_STATS_NOT_FOUND));

        long total = price.getBuyPrice() * amount;
        long currentMoney = stats.getMoney() == null ? 0L : stats.getMoney();
        if (currentMoney < total) {
            throw new IllegalArgumentException(GameApiDefiner.ERROR_MONEY_NOT_ENOUGH);
        }

        playerItemDao.findByPlayerIdAndLocationAndPosition(playerId, request.getLocation(), request.getPosition())
            .ifPresent(existing -> {
                throw new IllegalArgumentException("target slot is not empty");
            });

        PlayerItem playerItem = new PlayerItem();
        playerItem.setPlayerId(playerId);
        playerItem.setItemId(item.getId());
        playerItem.setLocation(request.getLocation());
        playerItem.setPosition(request.getPosition());
        playerItem.setAmount(amount);
        playerItemDao.save(playerItem);

        stats.setMoney(currentMoney - total);
        playerStatsDao.save(stats);

        return BuyItemResponse.builder()
            .itemId(item.getId())
            .amount(amount)
            .location(request.getLocation())
            .position(request.getPosition())
            .spentMoney(total)
            .money(stats.getMoney())
            .build();
    }
}
