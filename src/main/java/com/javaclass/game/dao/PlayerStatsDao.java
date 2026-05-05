package com.javaclass.game.dao;

import com.javaclass.game.model.PlayerStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerStatsDao extends JpaRepository<PlayerStats, Long> {
    // 這裡通常使用繼承的 findById(playerId) 即可完成大部份操作
}