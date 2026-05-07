package com.javaclass.game.dao;

import com.javaclass.game.model.PlayerItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlayerItemDao extends JpaRepository<PlayerItem, Long> {

    List<PlayerItem> findByAccountId(String accountId);

    Optional<PlayerItem> findByAccountIdAndItemIdAndLocation(String accountId, Long itemId, Integer location);

    Optional<PlayerItem> findByAccountIdAndLocationAndPosition(String accountId, Integer location, Integer position);
}