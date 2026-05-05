package com.javaclass.game.dao;

import com.javaclass.game.model.PlayerItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PlayerItemDao extends JpaRepository<PlayerItem, Long> {
	// 核心功能：根據位置（BAG/STORAGE）查詢物品列表
    List<PlayerItem> findByPlayerIdAndLocation(Long playerId, String location);
    
 // 查詢特定玩家穿在身上的所有裝備
    List<PlayerItem> findByPlayerIdAndIsEquippedTrue(Long playerId);
    
}