package com.github.ojvzinn.sqlannotation.entity;

import com.github.ojvzinn.sqlannotation.SQL;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.AllArgsConstructor;
import lombok.NonNull;

@AllArgsConstructor
public class MySQLEntity extends SQL {

    private String host;
    private int port;
    private String database;
    private String user;
    private String password;

    @Override
    public void init(HikariEntity entity) {
        try {
            HikariConfig config = entity.getConfig();
            config.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database);
            config.setUsername(this.user);
            config.setPassword(this.password);
            setDataSource(new HikariDataSource(config));
        } catch (Exception ex) {
            throw new RuntimeException("Error on create connection: " + ex);
        }
    }

    @Override
    public void destroy() {
        this.host = null;
        this.database = null;
        this.user = null;
        this.password = null;
        getDataSource().close();
    }


}
