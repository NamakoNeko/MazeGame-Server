package com.javaclass.game.dao;

import com.javaclass.game.model.PlayerEquipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlayerEquipmentDao extends JpaRepository<PlayerEquipment, Long> {

    Optional<PlayerEquipment> findByAccountId(String accountId);
}