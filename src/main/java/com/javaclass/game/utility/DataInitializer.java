package com.javaclass.game.utility;

import com.javaclass.game.constants.MenuPermissionDefiner.RoleLevel;
import com.javaclass.game.dao.AdminDao;
import com.javaclass.game.dao.ItemDao;
import com.javaclass.game.dao.PlayerDao;
import com.javaclass.game.model.Admin;
import com.javaclass.game.model.Item;
import com.javaclass.game.model.Player;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DataInitializer implements ApplicationRunner {

    private static final String DEFAULT_ROLE = RoleLevel.SuperAdmin.name();

    @Value("${init.admin.account}")
    private String initAdminAccount;

    @Value("${init.admin.password}")
    private String initAdminPassword;

    private final AdminDao adminDao;
    private final ItemDao itemDao;
    private final PlayerDao playerDao;
    private final BCryptPasswordEncoder passwordEncoder;

    public DataInitializer(
        AdminDao adminDao,
        ItemDao itemDao,
        PlayerDao playerDao,
        BCryptPasswordEncoder passwordEncoder
    ) {
        this.adminDao = adminDao;
        this.itemDao = itemDao;
        this.playerDao = playerDao;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        boolean isDefaultAccountMissing = adminDao.findByAccount(initAdminAccount).isEmpty();
        if (isDefaultAccountMissing) {
            createDefaultAdminAccount();
        }

        if (itemDao.count() == 0) {
            createSampleItems();
        }

        if (playerDao.count() == 0) {
            createSamplePlayers();
        }
    }

    private void createDefaultAdminAccount() {
        Admin defaultAdmin = new Admin();
        defaultAdmin.setAccount(initAdminAccount);
        defaultAdmin.setPassword(passwordEncoder.encode(initAdminPassword));
        defaultAdmin.setRole(DEFAULT_ROLE);
        defaultAdmin.setCreatedAt(LocalDateTime.now());

        adminDao.save(defaultAdmin);
    }

    private void createSampleItems() {
        itemDao.save(createItem("治療藥水", "恢復少量生命值", "HP +50", 1, "COMMON", 99));
        itemDao.save(createItem("鐵劍", "新手常用的近戰武器", "ATK +8", 2, "COMMON", 1));
        itemDao.save(createItem("秘銀護符", "帶有微弱魔力的護符", "DEF +5", 3, "RARE", 1));
    }

    private Item createItem(String name, String description, String effect, Integer type, String rare, Integer maxAmount) {
        Item item = new Item();
        item.setName(name);
        item.setDescription(description);
        item.setEffect(effect);
        item.setType(type);
        item.setRare(rare);
        item.setMaxAmount(maxAmount);
        return item;
    }

    private void createSamplePlayers() {
        playerDao.save(createPlayer("hero001", "測試勇者", "hero001@example.com", 12));
        playerDao.save(createPlayer("mage002", "見習法師", "mage002@example.com", 7));
        playerDao.save(createPlayer("rogue003", "夜行者", "rogue003@example.com", 19));
    }

    private Player createPlayer(String accountId, String nickname, String email, Integer level) {
        Player player = new Player();
        player.setAccountId(accountId);
        player.setPassword(passwordEncoder.encode("password"));
        player.setNickname(nickname);
        player.setEmail(email);
        player.setLevel(level);
        player.setStatus("ACTIVE");
        player.setMoney(1000L);
        player.setCreatedAt(LocalDateTime.now());
        player.setLastLoginAt(LocalDateTime.now());
        return player;
    }
}
