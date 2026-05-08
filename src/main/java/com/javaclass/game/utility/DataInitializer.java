package com.javaclass.game.utility;

import com.javaclass.game.constants.MenuPermissionDefiner.RoleLevel;
import com.javaclass.game.dao.AdminDao;
import com.javaclass.game.model.Admin;
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
    private final BCryptPasswordEncoder passwordEncoder;

    public DataInitializer(AdminDao adminDao, BCryptPasswordEncoder passwordEncoder) {
        this.adminDao = adminDao;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        boolean isDefaultAccountMissing = adminDao.findByAccount(initAdminAccount).isEmpty();
        if (isDefaultAccountMissing) {
            createDefaultAdminAccount();
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
}
