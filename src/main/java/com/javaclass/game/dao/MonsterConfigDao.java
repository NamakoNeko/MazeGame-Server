package com.javaclass.game.dao;

import com.javaclass.game.model.MonsterConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MonsterConfigDao extends JpaRepository<MonsterConfig, Long> {
}
