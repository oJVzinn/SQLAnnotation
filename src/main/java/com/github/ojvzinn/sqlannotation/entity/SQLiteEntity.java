package com.github.ojvzinn.sqlannotation.entity;

import com.github.ojvzinn.sqlannotation.SQL;

import java.util.LinkedHashMap;

public class SQLiteEntity extends SQL {

    @Override
    public void init(HikariEntity entity) {

    }

    @Override
    public void destroy() {

    }

    public String makeSQLCreateTable(String table, LinkedHashMap<String, Object> columns) {
        return "v";
    }

    @Override
    public String makeSQLCheckColumn(String table, LinkedHashMap<String, Object> column) {
        return "";
    }

}
