package com.javaclass.game.dao;

import com.javaclass.game.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameItemDao extends JpaRepository<Item, Long> {
}