package dev.aurelium.auraskills.bukkit.storage;

import dev.aurelium.auraskills.api.skill.Skill;
import dev.aurelium.auraskills.api.skill.Skills;
import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.common.storage.sql.DatabaseCredentials;
import dev.aurelium.auraskills.common.storage.sql.TableCreator;
import dev.aurelium.auraskills.common.storage.sql.pool.ConnectionPool;
import dev.aurelium.auraskills.common.storage.sql.pool.MySqlConnectionPool;
import dev.aurelium.auraskills.common.util.TestSession;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static dev.aurelium.auraskills.common.storage.sql.SqlStorageProvider.TABLE_PREFIX;

public class DataFakerTest {

    private static final int BATCH_SIZE = 1000;
    private ConnectionPool pool;

    @BeforeEach
    void setUp() {
        ServerMock server = MockBukkit.mock();
        AuraSkills plugin = MockBukkit.load(AuraSkills.class, TestSession.create());

        DatabaseCredentials credentials = new DatabaseCredentials(
                getenvOrDefault("MYSQL_HOST", "localhost"),
                Integer.parseInt(getenvOrDefault(System.getenv("MYSQL_PORT"), "3306")),
                getenvOrDefault("MYSQL_DATABASE", "auraskills"),
                getenvOrDefault("MYSQL_USERNAME", "root"),
                getenvOrDefault("MYSQL_PASSWORD", ""),
                Boolean.parseBoolean(getenvOrDefault("MYSQL_SSL", "false")));

        pool = new MySqlConnectionPool(plugin, credentials);
        pool.enable();

        TableCreator tableCreator = new TableCreator(plugin, pool, TABLE_PREFIX);
        tableCreator.createTables();

        server.getScheduler().performOneTick();
    }

    private String getenvOrDefault(String name, String def) {
        String val = System.getenv(name);
        return (val != null && !val.isBlank()) ? val : def;
    }

    @AfterEach
    void tearDown() {
        pool.disable();
    }

    @ParameterizedTest
    @CsvSource("100, 1")
    @Disabled
    void populateUsersTable(int users, int startUserId) {
        Random random = ThreadLocalRandom.current();
        String[] locales = new String[]{"en", "es", "fr", "zh-CN", "pl", "de"};

        try (Connection connection = pool.getConnection()) {
            connection.setAutoCommit(false);
            String usersQuery = "INSERT INTO " + TABLE_PREFIX + "users (user_id, player_uuid, locale, mana) VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE locale=?, mana=?";
            try (PreparedStatement statement = connection.prepareStatement(usersQuery)) {
                int count = 0;
                for (int i = startUserId; i < users + startUserId; i++) {
                    statement.setInt(1, i);
                    statement.setString(2, UUID.randomUUID().toString());
                    if (random.nextDouble() < 0.1) {
                        String locale = locales[random.nextInt(6)];
                        statement.setString(3, locale);
                        statement.setString(5, locale);
                    } else {
                        statement.setNull(3, Types.VARCHAR);
                        statement.setNull(5, Types.VARCHAR);
                    }
                    int mana = random.nextInt(4, 20) + 1;
                    statement.setDouble(4, mana);
                    statement.setDouble(6, mana);

                    statement.addBatch();

                    if (++count % BATCH_SIZE == 0) {
                        statement.executeBatch();
                        statement.clearBatch();
                    }
                }
                statement.executeBatch();
                connection.commit();
            }
            String skillLevelsQuery = "INSERT INTO " + TABLE_PREFIX + "skill_levels (user_id, skill_name, skill_level, skill_xp) VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE skill_level=?, skill_xp=?";
            try (PreparedStatement statement = connection.prepareStatement(skillLevelsQuery)) {
                int count = 0;
                for (int i = startUserId; i < users + startUserId; i++) {
                    statement.setInt(1, i);
                    for (Skill skill : Skills.values()) {
                        if (!skill.isEnabled()) continue;

                        statement.setString(2, skill.getId().toString());
                        int level = random.nextInt(101);
                        statement.setInt(3, level);
                        statement.setInt(5, level);
                        double xp = random.nextDouble(100) * level * level + 100;
                        statement.setDouble(4, xp);
                        statement.setDouble(6, xp);

                        statement.addBatch();

                        if (++count % BATCH_SIZE == 0) {
                            statement.executeBatch();
                            statement.clearBatch();
                        }
                    }
                }
                statement.executeBatch();
                connection.commit();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
