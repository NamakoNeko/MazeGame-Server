package com.javaclass.game.controller;

import com.javaclass.game.dto.ApiResponse;
import com.javaclass.game.dto.ItemDto;
import com.javaclass.game.model.Item;
import com.javaclass.game.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/items")
public class ItemController {

    @Autowired
    private ItemService itemService;

    public ItemController() {
    }

    @GetMapping
    public ApiResponse<Page<Item>> getAllItems(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.success(itemService.getItems(keyword, type, page, size));
    }

    @GetMapping("/{id}")
    public ApiResponse<Item> getItemById(@PathVariable Long id) {
        return ApiResponse.success(itemService.getItemById(id));
    }

    @PostMapping
    public ApiResponse<Item> createItem(@RequestBody Item item) {
        return ApiResponse.success(itemService.createItem(item));
    }

    @PutMapping("/{id}")
    public ApiResponse<Item> updateItem(@PathVariable Long id, @RequestBody Item item) {
        return ApiResponse.success(itemService.updateItem(id, item));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteItem(@PathVariable Long id) {
        itemService.deleteItem(id);
        return ApiResponse.success("刪除成功");
    }
}