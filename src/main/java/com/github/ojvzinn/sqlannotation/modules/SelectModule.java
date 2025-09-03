package com.github.ojvzinn.sqlannotation.modules;

import com.github.ojvzinn.sqlannotation.SQL;
import com.github.ojvzinn.sqlannotation.annotations.Entity;
import com.github.ojvzinn.sqlannotation.utils.SQLUtils;

import java.sql.*;
import java.util.*;

public class SelectModule extends Module {

    public SelectModule(SQL instance) {
        super(instance);
    }

    public <T> T findByConditionals(Class<T> entity, Map<String, Object> conditionals) {
        Entity tableName = SQLUtils.checkIfClassValid(entity);
        Map<String, Object> finalConditionals = new HashMap<>();
        for (String column : conditionals.keySet()) finalConditionals.put(column + " = ?", conditionals.get(column));
        List<Map<String, Object>> resultAll = select(tableName.name(), finalConditionals);
        if (resultAll.isEmpty()) {
            return null;
        }

        return SQLUtils.loadClass(entity, resultAll.stream().findFirst().get());
    }

    public <T> T findByKey(Class<T> entity, Object key) {
        Map<String, Object> conditional = new HashMap<>();
        conditional.put(SQLUtils.makeColumn(SQLUtils.findPrimaryKey(entity)).getName() + " = ?", key);
        List<Map<String, Object>> resultAll = findResult(entity, conditional);

        if (resultAll.isEmpty()) return null;

        return SQLUtils.loadClass(entity, resultAll.stream().findFirst().get());
    }

    public List<Map<String, Object>> findResult(Class<?> entity, Map<String, Object> conditionals) {
        return select(SQLUtils.checkIfClassValid(entity).name(), conditionals);
    }

    public List<Map<String, Object>> select(String table, Map<String, Object> conditionals) {
        List<Map<String, Object>> result = new ArrayList<>();
        LinkedList<String> conditionalsKey = new LinkedList<>(conditionals.keySet());
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT * FROM ").append(table).append(" WHERE");
        for (int i = 0; i < conditionalsKey.size(); i++) {
            String conditional = conditionalsKey.get(i);
            sql.append(" ").append(conditional);
            if (i + 1 != conditionals.size()) sql.append(" AND");
        }

        System.out.println(sql.toString());
        try (Connection connection = getInstance().getDataSource().getConnection()) {
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

}
