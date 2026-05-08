package com.javaclass.game.service;

import com.javaclass.game.dao.GameItemAttributeDao;
import com.javaclass.game.dao.ItemDao;
import com.javaclass.game.dao.ItemPriceDao;
import com.javaclass.game.dto.ItemAttributeResult;
import com.javaclass.game.dto.ItemCatalogResult;
import com.javaclass.game.model.GameItemAttribute;
import com.javaclass.game.model.Item;
import com.javaclass.game.model.ItemPrice;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GameItemCatalogService {

    private final ItemDao itemDao;
    private final ItemPriceDao itemPriceDao;
    private final GameItemAttributeDao gameItemAttributeDao;

    public GameItemCatalogService(ItemDao itemDao, ItemPriceDao itemPriceDao, GameItemAttributeDao gameItemAttributeDao) {
        this.itemDao = itemDao;
        this.itemPriceDao = itemPriceDao;
        this.gameItemAttributeDao = gameItemAttributeDao;
    }

    public List<ItemCatalogResult> listItems() {
        return itemDao.findAll().stream().map(this::toResult).toList();
    }

    public ItemCatalogResult toResult(Item item) {
        ItemPrice price = itemPriceDao.findById(item.getId()).orElse(null);
        List<ItemAttributeResult> attributes = gameItemAttributeDao.findByItemId(item.getId()).stream()
            .map(this::toAttributeResult)
            .toList();
        return ItemCatalogResult.builder()
            .itemId(item.getId())
            .name(item.getName())
            .description(item.getDescription())
            .type(item.getType())
            .rare(item.getRare())
            .maxAmount(item.getMaxAmount())
            .modelPath(item.getModelPath())
            .buyPrice(price != null ? price.getBuyPrice() : 0L)
            .sellPrice(price != null ? price.getSellPrice() : 0L)
            .attributes(attributes)
            .build();
    }

    private ItemAttributeResult toAttributeResult(GameItemAttribute attribute) {
        return ItemAttributeResult.builder()
            .effectType(attribute.getEffectType())
            .value(attribute.getValue())
            .duration(attribute.getDuration())
            .build();
    }
}
