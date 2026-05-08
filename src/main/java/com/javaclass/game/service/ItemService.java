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
        gameItemAttributeDao.findByItemId(itemId).ifPresent(gameItemAttributeDao::delete);
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
        item.setEffect(request.getEffect());
        item.setType(request.getType());
        item.setRare(request.getRare());
        item.setMaxAmount(request.getMaxAmount());
    }

    private void saveAttributes(Long itemId, UpsertItemRequest request) {
        GameItemAttribute attributes = gameItemAttributeDao.findByItemId(itemId)
            .orElseGet(GameItemAttribute::new);
        attributes.setItemId(itemId);
        attributes.setHp(request.getHp());
        attributes.setAtk(request.getAtk());
        attributes.setDef(request.getDef());
        attributes.setDuration(request.getDuration());
        gameItemAttributeDao.save(attributes);
    }

    private ItemResult toResult(Item item) {
        GameItemAttribute attributes = gameItemAttributeDao.findByItemId(item.getId()).orElse(null);

        return ItemResult.builder()
            .id(item.getId())
            .name(item.getName())
            .description(item.getDescription())
            .effect(item.getEffect())
            .type(item.getType())
            .rare(item.getRare())
            .maxAmount(item.getMaxAmount())
            .hp(attributes == null ? null : attributes.getHp())
            .atk(attributes == null ? null : attributes.getAtk())
            .def(attributes == null ? null : attributes.getDef())
            .duration(attributes == null ? null : attributes.getDuration())
            .build();
    }
}
