package com.javaclass.game.service;

import com.javaclass.game.dao.ItemDao;
import com.javaclass.game.model.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class ItemService {

    @Autowired
    private ItemDao itemDao;

    public Page<Item> getItems(String keyword, String type, int page, int size) {
        return itemDao.findByCriteria(keyword, type, PageRequest.of(page, size));
    }

    public Item getItemById(Long id) {
        return itemDao.findById(id).orElseThrow(() -> new RuntimeException("道具不存在"));
    }

    public Item createItem(Item item) {
        return itemDao.save(item);
    }

    public Item updateItem(Long id, Item updatedItem) {
        Item existingItem = getItemById(id);
        existingItem.setName(updatedItem.getName());
        existingItem.setType(updatedItem.getType());
        existingItem.setDescription(updatedItem.getDescription());
        // 更新屬性
        existingItem.setAttack(updatedItem.getAttack());
        existingItem.setDefense(updatedItem.getDefense());
        existingItem.setHp(updatedItem.getHp());
        existingItem.setDuration(updatedItem.getDuration());
        
        return itemDao.save(existingItem);
    }

    public void deleteItem(Long id) {
    	itemDao.deleteById(id);
    }
}