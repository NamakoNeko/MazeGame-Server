package com.javaclass.game.dao;

import com.javaclass.game.model.PlayerEquipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerEquipmentDao extends JpaRepository<PlayerEquipment, Long> {
}
