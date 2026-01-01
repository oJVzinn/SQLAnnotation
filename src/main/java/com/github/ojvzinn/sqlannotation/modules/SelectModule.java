package com.github.ojvzinn.sqlannotation.modules;

import com.github.ojvzinn.sqlannotation.SQL;
import com.github.ojvzinn.sqlannotation.annotations.Entity;
import com.github.ojvzinn.sqlannotation.model.ConditionalModel;
import com.github.ojvzinn.sqlannotation.model.OrderModel;
import com.github.ojvzinn.sqlannotation.model.SQLTimerModel;
import com.github.ojvzinn.sqlannotation.enums.ConnectiveType;
import com.github.ojvzinn.sqlannotation.model.SelectJoinModel;
import com.github.ojvzinn.sqlannotation.utils.SQLUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.*;

public class SelectModule extends Module {

    public SelectModule(SQL instance) {
        super(instance);
    }

    public <T> T findByConditionals(Class<T> entity, SelectJoinModel joinModel, ConditionalModel conditionals, OrderModel order) {
        JSONArray resultAll = findResult(entity, joinModel, conditionals, order);
        if (resultAll.isEmpty()) return null;
        return SQLUtils.loadClass(entity, (JSONObject) resultAll.get(0), joinModel);
    }

    public <T> T findByKey(Class<T> entity, SelectJoinModel joinModel, Object key) {
        ConditionalModel conditional = new ConditionalModel(ConnectiveType.NONE, joinModel);
        conditional.appendConditional(SQLUtils.findPrimaryKey(entity).getName(), key);
        JSONArray resultAll = findResult(entity, joinModel, conditional, null);
        if (resultAll.isEmpty()) return null;
        return SQLUtils.loadClass(entity, (JSONObject) resultAll.get(0), joinModel);
    }

    public JSONArray findResult(Class<?> entity, SelectJoinModel joinModel, ConditionalModel conditionals, OrderModel order) {
        return select(SQLUtils.checkIfClassValid(entity).name(), joinModel, conditionals, order);
    }

    public JSONArray findAll(Class<?> entity, OrderModel order) {
        Entity tableName = SQLUtils.checkIfClassValid(entity);
        SQLTimerModel timer = new SQLTimerModel(System.currentTimeMillis());
        JSONArray result;
        StringBuilder sql = new StringBuilder().append("SELECT * FROM ").append(tableName.name());
        if (order != null) sql.append(" ORDER BY").append(order.build());
        try (Connection connection = getInstance().getDataSource().getConnection()) {
            result = selectQuery(sql.toString(), connection.prepareStatement(sql.toString()), timer);
        } catch (SQLException e) {
            throw new RuntimeException("An error occurred while fetching all records", e);
        }

        return result;
    }

    public JSONArray select(String table, SelectJoinModel joinModel, ConditionalModel conditionals, OrderModel order) {
        JSONArray result;
        SQLTimerModel timer = new SQLTimerModel(System.currentTimeMillis());
        StringBuilder sql = new StringBuilder().append("SELECT * FROM ").append(table);
        if (joinModel != null) {
            sql = joinModel.generateSelectQuery();
            sql.append(" FROM ").append(table).append(" AS ").append(joinModel.getTableReference()).append(" ").append(joinModel.makeJoinQuery());
        }

        sql.append(" WHERE").append(conditionals.build());
        if (order != null) sql.append(" ORDER BY").append(order.build());
        try (Connection connection = getInstance().getDataSource().getConnection()) {
            System.out.println(sql);
            PreparedStatement statement = connection.prepareStatement(sql.toString());
            int i = 1;
            for (String key : conditionals.getConditions().keySet()) {
                statement.setObject(i, conditionals.getConditions().get(key));
                i++;
            }

            result = selectQuery(sql.toString(), statement, timer);
        } catch (SQLException e) {
            throw new RuntimeException("An error occurred while fetching a record", e);
        }

        return result;
    }

    private JSONArray selectQuery(String sql, PreparedStatement statement, SQLTimerModel timer) {
        JSONArray result = new JSONArray();
        try (ResultSet resultSet = statement.executeQuery()) {
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            while (resultSet.next()) {
                JSONObject row = new JSONObject();
                for (int i = 1; i <= columnCount; i++) row.put(metaData.getColumnLabel(i), resultSet.getObject(i));
                result.put(row);
            }
        } catch (SQLException e) {
            throw new RuntimeException("An error occurred while fetching all records", e);
        }

        SQLUtils.loggingQuery(timer, sql);
        return result;
    }

}
