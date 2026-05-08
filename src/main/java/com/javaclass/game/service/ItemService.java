package com.javaclass.game.service;

import com.javaclass.game.dao.GameItemAttributeDao;
import com.javaclass.game.dao.ItemDao;
import com.javaclass.game.dto.ItemResult;
import com.javaclass.game.dto.UpsertItemRequest;
import com.javaclass.game.model.GameItemAttribute;
import com.javaclass.game.model.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ItemService {

    private final ItemDao itemDao;
    private final GameItemAttributeDao gameItemAttributeDao;
    private final OperationLogService operationLogService;

    public ItemService(
        ItemDao itemDao,
        GameItemAttributeDao gameItemAttributeDao,
        OperationLogService operationLogService
    ) {
        this.itemDao = itemDao;
        this.gameItemAttributeDao = gameItemAttributeDao;
        this.operationLogService = operationLogService;
    }

    public Page<ItemResult> getItemList(String keyword, Pageable pageable) {
        Page<Item> itemPage = itemDao.findByKeyword(keyword, pageable);
        List<ItemResult> resultList = itemPage.getContent().stream()
            .map(this::toResult)
            .toList();
        return new PageImpl<>(resultList, pageable, itemPage.getTotalElements());
    }

    public ItemResult getItem(Long itemId) {
        return toResult(findItem(itemId));
    }

    @Transactional
    public ItemResult createItem(UpsertItemRequest request) {
        Item item = new Item();
        applyItemFields(item, request);
        Item savedItem = itemDao.save(item);
        saveAttributes(savedItem.getId(), request);
        operationLogService.record("CREATE_ITEM", "ITEM", String.valueOf(savedItem.getId()), savedItem.getName());
        return toResult(savedItem);
    }

    @Transactional
    public ItemResult updateItem(Long itemId, UpsertItemRequest request) {
        Item item = findItem(itemId);
        applyItemFields(item, request);
        Item savedItem = itemDao.save(item);
        saveAttributes(savedItem.getId(), request);
        operationLogService.record("UPDATE_ITEM", "ITEM", String.valueOf(savedItem.getId()), savedItem.getName());
        return toResult(savedItem);
    }

    @Transactional
    public void deleteItem(Long itemId) {
        Item item = findItem(itemId);
        List<GameItemAttribute> attributeList = gameItemAttributeDao.findByItemId(itemId);
        if (!attributeList.isEmpty()) {
            gameItemAttributeDao.deleteAll(attributeList);
        }
        itemDao.delete(item);
        operationLogService.record("DELETE_ITEM", "ITEM", String.valueOf(itemId), item.getName());
    }

    private Item findItem(Long itemId) {
        return itemDao.findById(itemId)
            .orElseThrow(() -> new IllegalArgumentException("道具不存在"));
    }

    private void applyItemFields(Item item, UpsertItemRequest request) {
        item.setName(request.getName().trim());
        item.setDescription(request.getDescription());
        item.setType(request.getType());
        item.setRare(request.getRare());
        item.setMaxAmount(request.getMaxAmount());
    }

    private void saveAttributes(Long itemId, UpsertItemRequest request) {
        List<GameItemAttribute> existingList = gameItemAttributeDao.findByItemId(itemId);
        if (!existingList.isEmpty()) {
            gameItemAttributeDao.deleteAll(existingList);
        }

        if (request.getHp() != null && request.getHp() != 0) {
            gameItemAttributeDao.save(buildAttribute(itemId, "HP", request.getHp(), request.getDuration()));
        }
        if (request.getAtk() != null && request.getAtk() != 0) {
            gameItemAttributeDao.save(buildAttribute(itemId, "ATK", request.getAtk(), request.getDuration()));
        }
        if (request.getDef() != null && request.getDef() != 0) {
            gameItemAttributeDao.save(buildAttribute(itemId, "DEF", request.getDef(), request.getDuration()));
        }
    }

    private GameItemAttribute buildAttribute(Long itemId, String effectType, Integer value, Integer duration) {
        GameItemAttribute attribute = new GameItemAttribute();
        attribute.setItemId(itemId);
        attribute.setEffectType(effectType);
        attribute.setValue(value);
        attribute.setDuration(duration);
        return attribute;
    }

    private ItemResult toResult(Item item) {
        List<GameItemAttribute> attributeList = gameItemAttributeDao.findByItemId(item.getId());

        Integer hp = null, atk = null, def = null, duration = null;
        for (GameItemAttribute attribute : attributeList) {
            switch (attribute.getEffectType()) {
                case "HP"  -> { hp  = attribute.getValue(); duration = attribute.getDuration(); }
                case "ATK" -> { atk = attribute.getValue(); duration = attribute.getDuration(); }
                case "DEF" -> { def = attribute.getValue(); duration = attribute.getDuration(); }
            }
        }

        String effect = buildEffectText(attributeList);

        return ItemResult.builder()
            .id(item.getId())
            .name(item.getName())
            .description(item.getDescription())
            .effect(effect)
            .type(item.getType())
            .rare(item.getRare())
            .maxAmount(item.getMaxAmount())
            .hp(hp)
            .atk(atk)
            .def(def)
            .duration(duration)
            .build();
    }

    private String buildEffectText(List<GameItemAttribute> attributeList) {
        if (attributeList.isEmpty()) {
            return "";
        }

        StringBuilder effectBuilder = new StringBuilder();
        for (GameItemAttribute attribute : attributeList) {
            int value = attribute.getValue() == null ? 0 : attribute.getValue();
            String sign = value >= 0 ? "+" : "-";
            effectBuilder
                .append(attribute.getEffectType())
                .append(sign)
                .append(Math.abs(value));

            if (attribute.getDuration() != null && attribute.getDuration() > 0) {
                effectBuilder.append("(").append(attribute.getDuration()).append("s)");
            }

            effectBuilder.append(" ");
        }

        return effectBuilder.toString().trim();
    }
}