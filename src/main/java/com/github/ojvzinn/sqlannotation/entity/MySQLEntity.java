package com.github.ojvzinn.sqlannotation.entity;

import com.github.ojvzinn.sqlannotation.SQL;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.AllArgsConstructor;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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

    @Override
    public String makeSQLCreateTable(String table, Map<String, ColumnEntity> columns) {
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE IF NOT EXISTS ").append(table).append(" (");
        List<String> columnNames = new ArrayList<>(columns.keySet());
        for (int i = 0; i < columnNames.size(); i++) {
            ColumnEntity entity = columns.get(columnNames.get(i));
            StringBuilder builder = new StringBuilder();
            builder.append("`")
                    .append(entity.getName())
                    .append("`")
                    .append(" ").append(entity.makeType())
                    .append(entity.isPrimaryKey() ? " PRIMARY KEY" : "")
                    .append(entity.isAutoIncrement() ? " AUTO_INCREMENT" : "")
                    .append(entity.isNotNull() ? " NOT NULL" : "");
            sb.append(builder);
            if (i + 1 < columnNames.size()) {
                sb.append(", ");
            }
        }

        return sb.append(")").toString();
    }

    @Override
    public String makeSQLCheckColumn(String table, String column, String type) {
        StringBuilder sb = new StringBuilder();
        sb.append("ALTER TABLE ").append(table).append(" ADD COLUMN IF NOT EXISTS ").append(column).append(" ").append(type);
        return sb.toString();
    }



}
