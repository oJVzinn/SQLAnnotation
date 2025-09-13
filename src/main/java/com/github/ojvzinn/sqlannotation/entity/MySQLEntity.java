package com.github.ojvzinn.sqlannotation.entity;

import com.github.ojvzinn.sqlannotation.SQL;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.AllArgsConstructor;

import java.util.LinkedHashMap;

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
    public String makeSQLCreateTable(String table, LinkedHashMap<String, Object> columns) {
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE IF NOT EXISTS ").append(table).append(" (");
        int i = 0;
        for (String key : columns.keySet()) {
            ColumnEntity entity = (ColumnEntity) columns.get(key);
            String builder = "`" +
                    entity.getName() +
                    "`" +
                    " " + entity.makeType() +
                    (entity.isPrimaryKey() ? " PRIMARY KEY" : "") +
                    (entity.isAutoIncrement() ? " AUTO_INCREMENT" : "") +
                    (entity.isNotNull() ? " NOT NULL" : "");
            sb.append(builder);
            if (i + 1 < columns.keySet().size()) {
                sb.append(", ");
            }

            i++;
        }

        return sb.append(")").toString();
    }

    @Override
    public String makeSQLCheckColumn(String table, LinkedHashMap<String, Object> columns) {
        StringBuilder sb = new StringBuilder();
        sb.append("ALTER TABLE ").append(table);
        int i = 0;
        for (String key : columns.keySet()) {
            ColumnEntity entity = (ColumnEntity) columns.get(key);
            String builder = "`" +
                    entity.getName() +
                    "`" +
                    " " + entity.makeType() +
                    (entity.isPrimaryKey() ? " PRIMARY KEY" : "") +
                    (entity.isAutoIncrement() ? " AUTO_INCREMENT" : "") +
                    (entity.isNotNull() ? " NOT NULL" : "");
            sb.append(" ADD COLUMN IF NOT EXISTS ").append(builder);
            if (i + 1 < columns.keySet().size()) {
                sb.append(", ");
            }

            i++;
        }

        return sb.toString();
    }

}
