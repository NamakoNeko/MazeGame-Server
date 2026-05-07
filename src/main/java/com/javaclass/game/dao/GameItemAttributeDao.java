package com.javaclass.game.dao;

import com.javaclass.game.model.GameItemAttribute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GameItemAttributeDao extends JpaRepository<GameItemAttribute, Long> {

    Optional<GameItemAttribute> findByItemId(Long itemId);
}