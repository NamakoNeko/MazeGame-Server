package com.javaclass.game.dao;

import com.javaclass.game.model.Hotkey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HotkeyDao extends JpaRepository<Hotkey, Long> {

    List<Hotkey> findByPlayerIdOrderByKeyIndexAsc(Long playerId);

    Optional<Hotkey> findByPlayerIdAndKeyIndex(Long playerId, Integer keyIndex);
}
