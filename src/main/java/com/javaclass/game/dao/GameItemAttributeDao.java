package com.javaclass.game.dao;

import com.javaclass.game.model.GameItemAttribute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameItemAttributeDao extends JpaRepository<GameItemAttribute, Long> {

    List<GameItemAttribute> findByItemId(Long itemId);
}
