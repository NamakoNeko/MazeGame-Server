package com.javaclass.game.utility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SchemaMigrationRunner implements ApplicationRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(SchemaMigrationRunner.class);
    private static final String INVENTORY_TABLE = "inventory";
    private static final String LEGACY_ACCOUNT_ID_COLUMN = "account_id";

    private final JdbcTemplate jdbcTemplate;

    public SchemaMigrationRunner(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(ApplicationArguments args) {
        dropLegacyInventoryAccountIdColumn();
    }

    private void dropLegacyInventoryAccountIdColumn() {
        if (!columnExists(INVENTORY_TABLE, LEGACY_ACCOUNT_ID_COLUMN)) {
            return;
        }

        dropForeignKeys(INVENTORY_TABLE, LEGACY_ACCOUNT_ID_COLUMN);
        jdbcTemplate.execute("ALTER TABLE " + INVENTORY_TABLE + " DROP COLUMN " + LEGACY_ACCOUNT_ID_COLUMN);
        LOGGER.info("Dropped legacy {}.{} column; inventory is linked by player_id.", INVENTORY_TABLE, LEGACY_ACCOUNT_ID_COLUMN);
    }

    private boolean columnExists(String tableName, String columnName) {
        Integer count = jdbcTemplate.queryForObject(
            """
            SELECT COUNT(*)
            FROM information_schema.columns
            WHERE table_schema = DATABASE()
              AND table_name = ?
              AND column_name = ?
            """,
            Integer.class,
            tableName,
            columnName
        );

        return count != null && count > 0;
    }

    private void dropForeignKeys(String tableName, String columnName) {
        List<String> foreignKeys = jdbcTemplate.queryForList(
            """
            SELECT constraint_name
            FROM information_schema.key_column_usage
            WHERE table_schema = DATABASE()
              AND table_name = ?
              AND column_name = ?
              AND referenced_table_name IS NOT NULL
            """,
            String.class,
            tableName,
            columnName
        );

        for (String foreignKey : foreignKeys) {
            jdbcTemplate.execute("ALTER TABLE " + tableName + " DROP FOREIGN KEY " + foreignKey);
            LOGGER.info("Dropped legacy foreign key {} on {}.{}.", foreignKey, tableName, columnName);
        }
    }
}
