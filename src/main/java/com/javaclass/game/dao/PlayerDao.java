package com.javaclass.game.dao;

import com.javaclass.game.model.Player;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerDao extends JpaRepository<Player, Long> {
	Optional<Player> findByAccountId(String account);
    
}