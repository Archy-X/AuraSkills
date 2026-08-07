package dev.aurelium.auraskills.bukkit.storage.sql;

import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.common.storage.sql.DatabaseCredentials;
import dev.aurelium.auraskills.common.storage.sql.pool.ConnectionPool;
import dev.aurelium.auraskills.common.storage.sql.pool.MySqlConnectionPool;
import org.junit.jupiter.api.condition.EnabledIf;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@EnabledIf("dev.aurelium.auraskills.bukkit.storage.sql.DockerAvailable#isAvailable")
class MySqlStorageContractTest extends SqlStorageContractTest {

    @Container
    private static final MySQLContainer<?> MYSQL = new MySQLContainer<>("mysql:8.4");

    @Override
    protected JdbcDatabaseContainer<?> container() {
        return MYSQL;
    }

    @Override
    protected ConnectionPool createPool(AuraSkills plugin, DatabaseCredentials credentials) {
        return new MySqlConnectionPool(plugin, credentials);
    }

}
