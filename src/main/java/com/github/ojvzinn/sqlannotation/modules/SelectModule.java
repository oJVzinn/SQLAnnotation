package com.github.ojvzinn.sqlannotation.modules;

import com.github.ojvzinn.sqlannotation.SQL;
import com.github.ojvzinn.sqlannotation.annotations.Entity;
import com.github.ojvzinn.sqlannotation.entity.ConditionalEntity;
import com.github.ojvzinn.sqlannotation.enums.ConnectiveType;
import com.github.ojvzinn.sqlannotation.utils.SQLUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.*;
import java.util.Set;

public class SelectModule extends Module {

    public SelectModule(SQL instance) {
        super(instance);
    }

    public <T> T findByConditionals(Class<T> entity, ConditionalEntity conditionals) {
        Entity tableName = SQLUtils.checkIfClassValid(entity);
        JSONArray resultAll = select(tableName.name(), conditionals);
        if (resultAll.isEmpty()) {
            return null;
        }

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
        JSONArray result;
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT * FROM ").append(tableName.name());
        try (Connection connection = getInstance().getDataSource().getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql.toString());
            result = selectQuery(sql.toString(), statement);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return result;
    }

    public JSONArray select(String table, ConditionalEntity conditionals) {
        JSONArray result;
        StringBuilder sql = new StringBuilder();
        Set<String> keys = conditionals.getConditions().keySet();
        sql.append("SELECT * FROM ").append(table).append(" WHERE");
        int i = 0;
        for (String conditional : keys) {
            sql.append(" ").append(conditional);
            if (i + 1 != keys.size()) sql.append(" ").append(conditionals.getType().name());
            i++;
        }

        try (Connection connection = getInstance().getDataSource().getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql.toString());
            i = 1;
            for (String key : keys) {
                statement.setObject(i, conditionals.getConditions().get(key));
                i++;
            }

            result = selectQuery(sql.toString(), statement);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return result;
    }

    private JSONArray selectQuery(String sql, PreparedStatement statement) {
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
            throw new RuntimeException(e);
        }

        SQLUtils.loggingQuery(sql);
        return result;
    }
}
