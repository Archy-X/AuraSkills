package dev.aurelium.auraskills.common.storage.sql.pool;

import com.zaxxer.hikari.HikariConfig;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.storage.sql.DatabaseCredentials;
import dev.aurelium.auraskills.common.storage.sql.dialect.PostgresDialect;
import dev.aurelium.auraskills.common.storage.sql.dialect.SqlDialect;

public class PostgresConnectionPool extends ConnectionPool {

    private final SqlDialect dialect = new PostgresDialect();

    public PostgresConnectionPool(AuraSkillsPlugin plugin, DatabaseCredentials credentials) {
        super(plugin, credentials);
    }

    @Override
    public void configure(HikariConfig config, DatabaseCredentials credentials) {
        config.setDriverClassName("org.postgresql.Driver");
        // Note: reWriteBatchedInserts is deliberately left off. It merges a JDBC batch into a
        // single multi-row INSERT, which makes Postgres reject batches that touch the same
        // ON CONFLICT key twice instead of applying them in order like MySQL does.
        config.setJdbcUrl("jdbc:postgresql://" + credentials.host() + ":" + credentials.port() + "/"
                + credentials.database() + "?sslmode=" + (credentials.ssl() ? "require" : "disable"));
        config.setUsername(credentials.username());
        config.setPassword(credentials.password());
        // pgjdbc reads socketTimeout in seconds, unlike Connector/J which reads milliseconds
        config.addDataSourceProperty("socketTimeout", "30");
    }

    @Override
    public SqlDialect getDialect() {
        return dialect;
    }

}
