package com.github.ojvzinn.sqlannotation.modules;

import com.github.ojvzinn.sqlannotation.SQL;
import com.github.ojvzinn.sqlannotation.annotations.Entity;
import com.github.ojvzinn.sqlannotation.model.*;
import com.github.ojvzinn.sqlannotation.enums.ConnectiveType;
import com.github.ojvzinn.sqlannotation.utils.SQLUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SelectModule extends Module {

    public SelectModule(SQL instance) {
        super(instance);
    }

    public <T> T findByConditionals(Class<T> entity, SelectJoinModel joinModel, ConditionalModel conditionals, OrderModel order, LimitModel limit) {
        List<T> resultAll = findResult(entity, joinModel, conditionals, order, limit);
        if (resultAll.isEmpty()) return null;
        return resultAll.get(0);
    }

    public <T> T findByKey(Class<T> entity, SelectJoinModel joinModel, Object key) {
        ConditionalModel conditional = new ConditionalModel(ConnectiveType.NONE, joinModel);
        conditional.appendConditional(SQLUtils.findPrimaryKey(entity).getName(), key);
        List<T> resultAll = findResult(entity, joinModel, conditional, null, null);
        if (resultAll.isEmpty()) return null;
        return resultAll.get(0);
    }

    public <T> List<T> findResult(Class<T> entity, SelectJoinModel joinModel, ConditionalModel conditionals, OrderModel order, LimitModel limit) {
        return select(entity, SQLUtils.checkIfClassValid(entity).name(), joinModel, conditionals, order, limit);
    }

    public <T> List<T> findAll(Class<T> entity, OrderModel order, LimitModel limit) {
        Entity tableName = SQLUtils.checkIfClassValid(entity);
        SQLTimerModel timer = new SQLTimerModel(System.currentTimeMillis());
        List<T> result = new ArrayList<>();
        StringBuilder sql = new StringBuilder().append("SELECT * FROM ").append(tableName.name());
        if (order != null) sql.append(" ORDER BY").append(order.build());
        if (limit != null) sql.append(" ").append(limit.build());
        try (Connection connection = getInstance().getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql.toString())) {
            List<Map<String, Object>> rows = executeQuery(sql.toString(), statement, timer);
            SelectJoinModel joinModel = SQLUtils.getSelectJoinModel(entity);
            for (Map<String, Object> row : rows) {
                result.add(SQLUtils.loadClass(entity, row, joinModel));
            }
        } catch (SQLException e) {
            throw new RuntimeException("An error occurred while fetching all records", e);
        }

        return result;
    }

    public <T> List<T> select(Class<T> entity, String table, SelectJoinModel joinModel, ConditionalModel conditionals, OrderModel order, LimitModel limit) {
        List<T> result = new ArrayList<>();
        SQLTimerModel timer = new SQLTimerModel(System.currentTimeMillis());
        StringBuilder sql = new StringBuilder().append("SELECT * FROM ").append(table);
        if (joinModel != null) {
            sql = joinModel.generateSelectQuery();
            sql.append(" FROM ").append(table).append(" AS ").append(joinModel.getEntityTableReference()).append(" ").append(joinModel.makeJoinQuery());
        }

        sql.append(" WHERE").append(conditionals.build());
        if (order != null) sql.append(" ORDER BY").append(order.build());
        if (limit != null) sql.append(" ").append(limit.build());
        try (Connection connection = getInstance().getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql.toString())) {
            int i = 1;
            for (String key : conditionals.getConditions().keySet()) {
                statement.setObject(i, conditionals.getConditions().get(key));
                i++;
            }

            List<Map<String, Object>> rows = executeQuery(sql.toString(), statement, timer);
            for (Map<String, Object> row : rows) {
                result.add(SQLUtils.loadClass(entity, row, joinModel));
            }
        } catch (SQLException e) {
            throw new RuntimeException("An error occurred while fetching a record", e);
        }

        return result;
    }

    private List<Map<String, Object>> executeQuery(String sql, PreparedStatement statement, SQLTimerModel timer) {
        List<Map<String, Object>> result = new ArrayList<>();
        try (ResultSet resultSet = statement.executeQuery()) {
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            while (resultSet.next()) {
                Map<String, Object> row = new LinkedHashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    row.put(metaData.getColumnLabel(i), resultSet.getObject(i));
                }
                result.add(row);
            }
        } catch (SQLException e) {
            throw new RuntimeException("An error occurred while executing query", e);
        }

        SQLUtils.loggingQuery(timer, sql);
        return result;
    }

}
