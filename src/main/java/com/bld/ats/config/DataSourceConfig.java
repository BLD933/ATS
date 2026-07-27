package com.bld.ats.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import javax.sql.DataSource;
import java.net.URI;

@Configuration
public class DataSourceConfig {

    private static final Logger log = LoggerFactory.getLogger(DataSourceConfig.class);

    @Bean
    @Lazy
    public DataSource dataSource() {
        String databaseUrl = System.getenv("DATABASE_URL");

        if (databaseUrl != null && !databaseUrl.isEmpty()) {
            try {
                URI uri = URI.create(databaseUrl.replace("postgresql://", "http://"));

                String[] userInfo = uri.getUserInfo().split(":", 2);
                String username = userInfo[0];
                String password = userInfo.length > 1 ? userInfo[1] : "";

                int port = uri.getPort() == -1 ? 5432 : uri.getPort();
                String jdbcUrl = "jdbc:postgresql://" + uri.getHost() + ":" + port + uri.getPath();

                log.info("Connecting to PostgreSQL at {}:{}", uri.getHost(), port);

                HikariConfig config = new HikariConfig();
                config.setJdbcUrl(jdbcUrl);
                config.setUsername(username);
                config.setPassword(password);
                config.setDriverClassName("org.postgresql.Driver");
                config.setConnectionTimeout(5000);
                config.setMaximumPoolSize(1);
                config.setMinimumIdle(0);
                config.setIdleTimeout(10000);
                config.setMaxLifetime(30000);
                return new HikariDataSource(config);
            } catch (Exception e) {
                log.warn("Failed to configure PostgreSQL, falling back to H2: {}", e.getMessage());
            }
        }

        log.info("Using H2 in-memory database");
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:h2:mem:ats;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH");
        config.setDriverClassName("org.h2.Driver");
        config.setMaximumPoolSize(1);
        config.setMinimumIdle(0);
        return new HikariDataSource(config);
    }
}
