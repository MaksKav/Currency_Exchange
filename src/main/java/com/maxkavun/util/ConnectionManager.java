package com.maxkavun.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class ConnectionManager {

    private static final String URL_KEY = "db.url";
    private static final int MAX_POOL_SIZE = Integer.parseInt(PropertiesUtil.get("db.pool.maxSize"));
    private static final int MIN_IDLE = Integer.parseInt(PropertiesUtil.get("db.pool.minIdle"));
    private static final long IDLE_TIMEOUT = Long.parseLong(PropertiesUtil.get("db.pool.idleTimeout"));
    private static final long MAX_LIFETIME = Long.parseLong(PropertiesUtil.get("db.pool.maxLifetime"));
    private static final long CONNECTION_TIMEOUT = Long.parseLong(PropertiesUtil.get("db.pool.connectionTimeout"));

    private static final HikariDataSource dataSource;

    static {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(PropertiesUtil.get(URL_KEY));
        config.setMaximumPoolSize(MAX_POOL_SIZE);
        config.setMinimumIdle(MIN_IDLE);
        config.setIdleTimeout(IDLE_TIMEOUT);
        config.setMaxLifetime(MAX_LIFETIME);
        config.setConnectionTimeout(CONNECTION_TIMEOUT);
        config.setDriverClassName("org.sqlite.JDBC");

        dataSource = new HikariDataSource(config);
    }

    private ConnectionManager() {}


    public static Connection getConnection() {
        try {
            Class.forName("org.sqlite.JDBC");
            return dataSource.getConnection();
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException("Failed to get connection from HikariCP pool ", e);
        }
    }
}
