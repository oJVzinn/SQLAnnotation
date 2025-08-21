package com.github.ojvzinn.sqlannotation;

import com.github.ojvzinn.sqlannotation.annotations.Table;
import com.github.ojvzinn.sqlannotation.entity.ColumnEntity;
import com.github.ojvzinn.sqlannotation.entity.HikariEntity;
import com.github.ojvzinn.sqlannotation.utils.SQLUtils;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.*;

@Setter
@Getter
public abstract class SQL {

    private HikariDataSource dataSource = null;

    public void scanTable(Class<?> tableClass) {
        Table tableName = SQLUtils.checkIfClassValid(tableClass);
        LinkedHashMap<String, ColumnEntity> columns = new LinkedHashMap<>();
        boolean containsPrimaryKey = false;
        for (Field field : SQLUtils.listFieldColumns(tableClass)) {
            ColumnEntity column = SQLUtils.makeColumn(field);
            if (column.isPrimaryKey()) {
                if (containsPrimaryKey) {
                    continue;
                }

                containsPrimaryKey = true;
            }

            columns.put(column.getName(), column);
        }

        if (!containsPrimaryKey) {
            throw new RuntimeException("The table must contain a primary key");
        }

        try (Connection connection = dataSource.getConnection()) {
            Statement statement = connection.createStatement();
            String sql = makeSQLCreateTable(tableName.name(), columns);
            statement.execute(sql);
            SQLUtils.loggingQuery(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void dropTable(Class<?> tableClass) {
        Table tableName = SQLUtils.checkIfClassValid(tableClass);
        try (Connection connection = dataSource.getConnection()) {
            Statement statement = connection.createStatement();
            String sql = "DROP TABLE " + tableName.name();
            statement.execute(sql);
            SQLUtils.loggingQuery(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void insert(Object table) {
        Table tableName = SQLUtils.checkIfClassValid(table.getClass());
        List<Field> columnsFields = SQLUtils.listFieldColumns(table.getClass());
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < columnsFields.size(); i++) {
            Field field = columnsFields.get(i);
            try {
                sb.append("(").append(field.getName()).append(", ").append(field.get(tableName)).append(")");
            } catch (Exception e)  {
                throw new RuntimeException(e);
            }

            if (i + 1 < columnsFields.size()) sb.append(", ");
        }

        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO " + tableName.name() + " VALUES(");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void checkColumn(Class<?> tableClass, String fieldColumn) {
        Table tableName = SQLUtils.checkIfClassValid(tableClass);
        Field field;
        try {
            field = tableClass.getDeclaredField(fieldColumn);
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

    public <T> T findByConditionals(Class<T> tableClass, Map<String, Object> conditionals) {
        Table tableName = SQLUtils.checkIfClassValid(tableClass);
        Map<String, Object> finalConditionals = new HashMap<>();
        for (String column : conditionals.keySet()) finalConditionals.put(column + " = ?", conditionals.get(column));
        List<Map<String, Object>> resultAll = select(tableName.name(), finalConditionals);
        if (resultAll.isEmpty()) {
            return null;
        }

        return SQLUtils.loadClass(tableClass, resultAll.stream().findFirst().get());
    }

    public <T> T findByKey(Class<T> tableClass, Object key) {
        Map<String, Object> conditional = new HashMap<>();
        conditional.put(SQLUtils.makeColumn(SQLUtils.findPrimaryKey(tableClass)).getName() + " = ?", key);
        List<Map<String, Object>> resultAll = findResult(tableClass, conditional);

        if (resultAll.isEmpty()) return null;

        return SQLUtils.loadClass(tableClass, resultAll.stream().findFirst().get());
    }

    private List<Map<String, Object>> findResult(Class<?> tableClass, Map<String, Object> conditionals) {
        return select(SQLUtils.checkIfClassValid(tableClass).name(), conditionals);
    }

    private List<Map<String, Object>> select(String table, Map<String, Object> conditionals) {
        List<Map<String, Object>> result = new ArrayList<>();
        LinkedList<String> conditionalsKey = new LinkedList<>(conditionals.keySet());
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT * FROM ").append(table).append(" WHERE");
        for (int i = 0; i < conditionalsKey.size(); i++) {
            String conditional = conditionalsKey.get(i);
            sql.append(" ").append(conditional);
            if (i + 1 != conditionals.size()) sql.append(" AND");
        }

        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql.toString());
            for (int i = 0; i < conditionals.size(); i++) statement.setObject(i + 1, conditionals.get(conditionalsKey.get(i)));
            try (ResultSet resultSet = statement.executeQuery()) {
                ResultSetMetaData metaData = resultSet.getMetaData();
                int columnCount = metaData.getColumnCount();
                while (resultSet.next()) {
                    Map<String, Object> row = new HashMap<>();
                    for (int i = 1; i <= columnCount; i++) {
                        row.put(metaData.getColumnLabel(i), resultSet.getObject(i));
                    }

                    result.add(row);
                }
            }

            SQLUtils.loggingQuery(sql.toString());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return result;
    }

    public abstract void init(HikariEntity entity);

    public abstract void destroy();

    public abstract String makeSQLCreateTable(String table, Map<String, ColumnEntity> columns);

    public abstract String makeSQLCheckColumn(String table, String column, String type);

}
