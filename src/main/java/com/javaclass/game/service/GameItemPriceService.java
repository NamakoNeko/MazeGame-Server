package com.javaclass.game.service;

import com.javaclass.game.dto.ItemPriceResult;
import com.javaclass.game.model.ItemPrice;
import com.javaclass.game.dao.ItemPriceDao;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GameItemPriceService {

    private final ItemPriceDao itemPriceDao;

    public GameItemPriceService(ItemPriceDao itemPriceDao) {
        this.itemPriceDao = itemPriceDao;
    }

    public List<ItemPriceResult> list() {
        return itemPriceDao.findAll().stream().map(this::toResult).toList();
    }

    private ItemPriceResult toResult(ItemPrice price) {
        return ItemPriceResult.builder()
            .itemId(price.getItemId())
            .buyPrice(price.getBuyPrice())
            .sellPrice(price.getSellPrice())
            .build();
    }
}
