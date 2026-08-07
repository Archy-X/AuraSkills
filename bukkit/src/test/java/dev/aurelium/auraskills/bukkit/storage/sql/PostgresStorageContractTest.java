package dev.aurelium.auraskills.bukkit.storage.sql;

import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.common.storage.sql.DatabaseCredentials;
import dev.aurelium.auraskills.common.storage.sql.pool.ConnectionPool;
import dev.aurelium.auraskills.common.storage.sql.pool.PostgresConnectionPool;
import org.junit.jupiter.api.condition.EnabledIf;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@EnabledIf("dev.aurelium.auraskills.bukkit.storage.sql.DockerAvailable#isAvailable")
class PostgresStorageContractTest extends SqlStorageContractTest {

    @Container
    private static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16-alpine");

    @Override
    protected JdbcDatabaseContainer<?> container() {
        return POSTGRES;
    }

    @Override
    protected ConnectionPool createPool(AuraSkills plugin, DatabaseCredentials credentials) {
        return new PostgresConnectionPool(plugin, credentials);
    }

}
