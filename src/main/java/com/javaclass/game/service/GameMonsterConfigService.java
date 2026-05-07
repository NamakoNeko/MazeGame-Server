package com.javaclass.game.service;

import com.javaclass.game.dao.MonsterConfigDao;
import com.javaclass.game.dto.MonsterConfigResult;
import com.javaclass.game.model.MonsterConfig;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GameMonsterConfigService {

    private final MonsterConfigDao monsterConfigDao;

    public GameMonsterConfigService(MonsterConfigDao monsterConfigDao) {
        this.monsterConfigDao = monsterConfigDao;
    }

    public List<MonsterConfigResult> list() {
        return monsterConfigDao.findAll().stream().map(this::toResult).toList();
    }

    private MonsterConfigResult toResult(MonsterConfig config) {
        return MonsterConfigResult.builder()
            .id(config.getId())
            .monsterKey(config.getMonsterKey())
            .modelPath(config.getModelPath())
            .hp(config.getHp())
            .atk(config.getAtk())
            .def(config.getDef())
            .moveSpeed(config.getMoveSpeed())
            .modelScale(config.getModelScale())
            .build();
    }
}
