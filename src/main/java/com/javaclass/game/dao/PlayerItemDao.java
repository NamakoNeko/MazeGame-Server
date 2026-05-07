package com.javaclass.game.dao;

import com.javaclass.game.model.PlayerItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlayerItemDao extends JpaRepository<PlayerItem, Long> {

    List<PlayerItem> findByPlayerId(Long playerId);

    Optional<PlayerItem> findByPlayerIdAndItemIdAndLocation(Long playerId, Long itemId, Integer location);

    Optional<PlayerItem> findByPlayerIdAndLocationAndPosition(Long playerId, Integer location, Integer position);
}
