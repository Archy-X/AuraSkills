package dev.aurelium.auraskills.common.storage.sql.pool;

import com.zaxxer.hikari.HikariConfig;
import dev.aurelium.auraskills.common.AuraSkillsPlugin;
import dev.aurelium.auraskills.common.storage.sql.DatabaseCredentials;
import dev.aurelium.auraskills.common.storage.sql.dialect.MySqlDialect;
import dev.aurelium.auraskills.common.storage.sql.dialect.SqlDialect;

import java.util.concurrent.TimeUnit;

public class MySqlConnectionPool extends ConnectionPool {

    private final SqlDialect dialect = new MySqlDialect();

    public MySqlConnectionPool(AuraSkillsPlugin plugin, DatabaseCredentials credentials) {
        super(plugin, credentials);
    }

    @Override
    public void configure(HikariConfig config, DatabaseCredentials credentials) {
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");
        config.setJdbcUrl("jdbc:mysql://" + credentials.host() + ":" + credentials.port() + "/" + credentials.database() + "?useSSL=" + credentials.ssl());
        config.setUsername(credentials.username());
        config.setPassword(credentials.password());
        // Connector/J reads socketTimeout in milliseconds
        config.addDataSourceProperty("socketTimeout", String.valueOf(TimeUnit.SECONDS.toMillis(30)));
    }

    @Override
    public SqlDialect getDialect() {
        return dialect;
    }

}
