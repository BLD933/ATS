package com.bld.ats.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.net.URI;

@Configuration
public class DataSourceConfig {

    @Bean
    @ConditionalOnExpression("'${DATABASE_URL:}' != ''")
    public DataSource dataSource() {
        String databaseUrl = System.getenv("DATABASE_URL");
        URI uri = URI.create(databaseUrl.replace("postgresql://", "http://"));

        String[] userInfo = uri.getUserInfo().split(":", 2);
        String username = userInfo[0];
        String password = userInfo.length > 1 ? userInfo[1] : "";

        int port = uri.getPort() == -1 ? 5432 : uri.getPort();
        String jdbcUrl = "jdbc:postgresql://" + uri.getHost() + ":" + port + uri.getPath();

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(username);
        config.setPassword(password);
        config.setDriverClassName("org.postgresql.Driver");
        config.setConnectionTimeout(10000);
        config.setMaximumPoolSize(2);
        config.setMinimumIdle(1);
        config.setValidationTimeout(5000);
        config.setLeakDetectionThreshold(15000);
        config.setKeepaliveTime(30000);
        return new HikariDataSource(config);
    }
}
