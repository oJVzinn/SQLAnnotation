package com.github.ojvzinn.sqlannotation;

import com.github.ojvzinn.sqlannotation.annotations.Entity;
import com.github.ojvzinn.sqlannotation.entity.ColumnEntity;
import com.github.ojvzinn.sqlannotation.entity.HikariEntity;
import com.github.ojvzinn.sqlannotation.modules.*;
import com.github.ojvzinn.sqlannotation.utils.SQLUtils;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedHashMap;

@Setter
@Getter
public abstract class SQL {

    private HikariDataSource dataSource = null;
    private CreateModule createModule = new CreateModule(this);
    private DropModule dropModule = new DropModule(this);
    private InsertModule insertModule = new InsertModule(this);
    private SelectModule selectModule = new SelectModule(this);
    private UpdateModule updateModule = new UpdateModule(this);

    public void checkColumn(Class<?> entity, String fieldColumn) {
        Entity tableName = SQLUtils.checkIfClassValid(entity);
        Field field;
        try {
            field = entity.getDeclaredField(fieldColumn);
        } catch (Exception e) {
            throw new RuntimeException("There is no column with that name in your table.");
        }

        ColumnEntity column = SQLUtils.makeColumn(field);
        try (Connection connection = dataSource.getConnection()) {
            Statement statement = connection.createStatement();
            String sql = makeSQLCheckColumn(tableName.name(), column.getName(), column.makeType());
            statement.execute(sql);
            SQLUtils.loggingQuery(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public abstract void init(HikariEntity entity);

    public abstract void destroy();

    public abstract String makeSQLCreateTable(String table, LinkedHashMap<String, Object> columns);

    public abstract String makeSQLCheckColumn(String table, String column, String type);

}
