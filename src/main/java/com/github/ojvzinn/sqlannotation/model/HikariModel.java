package com.github.ojvzinn.sqlannotation.model;

import com.zaxxer.hikari.HikariConfig;
import lombok.Getter;

@Getter
public class HikariModel {

    private final HikariConfig config = new HikariConfig();

    public HikariModel() {
        this.config.addDataSourceProperty("cachePrepStmts", "true");
        this.config.addDataSourceProperty("useServerPrepStmts", "true");
        this.config.addDataSourceProperty("useLocalSessionState", "true");
        this.config.addDataSourceProperty("cacheResultSetMetadata", "true");
        this.config.addDataSourceProperty("cacheServerConfiguration", "true");
        this.config.addDataSourceProperty("elideSetAutoCommits", "true");
        this.config.addDataSourceProperty("autoReconnect", "true");
        this.config.setAutoCommit(true);
        this.config.setMinimumIdle(10);
        this.config.setMaximumPoolSize(100);
        this.config.setConnectionTimeout(5000);
        this.config.setIdleTimeout(5000);
        this.config.setValidationTimeout(3000);
        this.config.setMaxLifetime(27000);
    }

}
