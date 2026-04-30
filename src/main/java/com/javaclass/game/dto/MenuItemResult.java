package com.javaclass.game.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@Getter
@AllArgsConstructor
public class MenuItemResult {

    private final String menuKey;
    private final String menuName;
    private final String path;
    private final boolean enabled;
    private final Map<String, Boolean> operations;
}