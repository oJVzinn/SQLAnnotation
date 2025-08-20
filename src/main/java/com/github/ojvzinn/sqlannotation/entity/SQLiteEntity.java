package com.github.ojvzinn.sqlannotation.entity;

import com.github.ojvzinn.sqlannotation.SQL;
import lombok.NonNull;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class SQLiteEntity extends SQL {

    @Override
    public void init(HikariEntity entity) {

    }

    @Override
    public void destroy() {

    }

    public String makeSQLCreateTable(String table, Map<String, ColumnEntity> columns) {
        return "v";
    }

    @Override
    public String makeSQLCheckColumn(String table, String column, String type) {
        return "";
    }

}
