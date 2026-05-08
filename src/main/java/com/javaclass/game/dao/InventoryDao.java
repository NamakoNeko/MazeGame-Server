package com.javaclass.game.dao;

import com.javaclass.game.model.Inventory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InventoryDao extends JpaRepository<Inventory, Long> {

    Page<Inventory> findByPlayerId(Long playerId, Pageable pageable);

    Optional<Inventory> findByPlayerIdAndItemId(Long playerId, Long itemId);
}
