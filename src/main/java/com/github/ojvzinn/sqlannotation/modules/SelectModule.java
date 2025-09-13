package com.github.ojvzinn.sqlannotation.modules;

import com.github.ojvzinn.sqlannotation.SQL;
import com.github.ojvzinn.sqlannotation.annotations.Entity;
import com.github.ojvzinn.sqlannotation.entity.ConditionalEntity;
import com.github.ojvzinn.sqlannotation.entity.SQLTimerEntity;
import com.github.ojvzinn.sqlannotation.enums.ConnectiveType;
import com.github.ojvzinn.sqlannotation.utils.SQLUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.*;

public class SelectModule extends Module {

    public SelectModule(SQL instance) {
        super(instance);
    }

    public <T> T findByConditionals(Class<T> entity, ConditionalEntity conditionals) {
        Entity tableName = SQLUtils.checkIfClassValid(entity);
        JSONArray resultAll = select(tableName.name(), conditionals);
        if (resultAll.isEmpty()) return null;
        return SQLUtils.loadClass(entity, (JSONObject) resultAll.get(0));
    }

    public <T> T findByKey(Class<T> entity, Object key) {
        ConditionalEntity conditional = new ConditionalEntity(ConnectiveType.NONE);
        conditional.appendConditional(SQLUtils.findPrimaryKey(entity).getName(), key);
        JSONArray resultAll = findResult(entity, conditional);
        if (resultAll.isEmpty()) return null;
        return SQLUtils.loadClass(entity, (JSONObject) resultAll.get(0));
    }

    public JSONArray findResult(Class<?> entity, ConditionalEntity conditionals) {
        return select(SQLUtils.checkIfClassValid(entity).name(), conditionals);
    }

    public JSONArray findAll(Class<?> entity) {
        Entity tableName = SQLUtils.checkIfClassValid(entity);
        SQLTimerEntity timer = new SQLTimerEntity(System.currentTimeMillis());
        JSONArray result;
        StringBuilder sql = new StringBuilder().append("SELECT * FROM ").append(tableName.name());
        try (Connection connection = getInstance().getDataSource().getConnection()) {
            result = selectQuery(sql.toString(), connection.prepareStatement(sql.toString()), timer);
        } catch (SQLException e) {
            throw new RuntimeException("An error occurred while fetching all records", e);
        }

        return result;
    }

    public JSONArray select(String table, ConditionalEntity conditionals) {
        JSONArray result;
        SQLTimerEntity timer = new SQLTimerEntity(System.currentTimeMillis());
        StringBuilder sql = new StringBuilder().append("SELECT * FROM ").append(table).append(" WHERE").append(conditionals.build());
        try (Connection connection = getInstance().getDataSource().getConnection()) {
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

    private JSONArray selectQuery(String sql, PreparedStatement statement, SQLTimerEntity timer) {
        JSONArray result = new JSONArray();
        try (ResultSet resultSet = statement.executeQuery()) {
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            while (resultSet.next()) {
                JSONObject row = new JSONObject();
                for (int i = 1; i <= columnCount; i++) {
                    row.put(metaData.getColumnLabel(i), resultSet.getObject(i));
                }

                result.put(row);
            }
        } catch (SQLException e) {
            throw new RuntimeException("An error occurred while fetching all records", e);
        }

        SQLUtils.loggingQuery(timer, sql);
        return result;
    }

}
