package com.github.ojvzinn.sqlannotation.model;

import com.github.ojvzinn.sqlannotation.SQL;

import java.util.LinkedHashMap;

public class SQLiteModel extends SQL {

    @Override
    public void init(HikariModel model) {

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
