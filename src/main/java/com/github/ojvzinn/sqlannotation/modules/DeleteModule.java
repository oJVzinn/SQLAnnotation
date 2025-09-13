package com.github.ojvzinn.sqlannotation.modules;

import com.github.ojvzinn.sqlannotation.SQL;
import com.github.ojvzinn.sqlannotation.annotations.Entity;
import com.github.ojvzinn.sqlannotation.entity.ConditionalEntity;
import com.github.ojvzinn.sqlannotation.entity.SQLTimerEntity;
import com.github.ojvzinn.sqlannotation.enums.ConnectiveType;
import com.github.ojvzinn.sqlannotation.utils.SQLUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DeleteModule extends Module {

    public DeleteModule(SQL instance) {
        super(instance);
    }

    public void drop(Class<?> entity) {
        Entity tableName = SQLUtils.checkIfClassValid(entity);
        SQLTimerEntity timer = new SQLTimerEntity(System.currentTimeMillis());
        String sql = "DROP TABLE " + tableName.name();
        try (Connection connection = getInstance().getDataSource().getConnection()) {
            connection.createStatement().executeUpdate(sql);
        } catch (SQLException e) {
            throw new RuntimeException("An error occurred while deleting an entity's table", e);
        }

        SQLUtils.loggingQuery(timer, sql);
    }

    public void truncate(Class<?> entity) {
        Entity tableName = SQLUtils.checkIfClassValid(entity);
        SQLTimerEntity timer = new SQLTimerEntity(System.currentTimeMillis());
        String sql = "TRUNCATE TABLE " + tableName.name();
        try (Connection connection = getInstance().getDataSource().getConnection()) {
            connection.createStatement().executeUpdate(sql);
        } catch (SQLException e) {
            throw new RuntimeException("An error occurred while resetting an entity", e);
        }

        SQLUtils.loggingQuery(timer, sql);
    }

    public void deleteByKey(Class<?> entity, Object key) {
        ConditionalEntity conditional = new ConditionalEntity(ConnectiveType.NONE);
        conditional.appendConditional(SQLUtils.findPrimaryKey(entity).getName(), key);
        Entity tableName = SQLUtils.checkIfClassValid(entity);
        delete(tableName.name(), conditional);
    }

    public void deleteAll(Class<?> entity) {
        Entity tableName = SQLUtils.checkIfClassValid(entity);
        SQLTimerEntity timer = new SQLTimerEntity(System.currentTimeMillis());
        String sql = "DELETE FROM " + tableName.name();
        try (Connection connection = getInstance().getDataSource().getConnection()) {
            connection.createStatement().executeUpdate(sql);
        } catch (SQLException e) {
            throw new RuntimeException("An error occurred while deleting entity records", e);
        }

        SQLUtils.loggingQuery(timer, sql);
    }

    public void deleteByConditionals(Class<?> entity, ConditionalEntity conditionals) {
        Entity tableName = SQLUtils.checkIfClassValid(entity);
        delete(tableName.name(), conditionals);
    }

    public void delete(String table, ConditionalEntity conditional) {
        SQLTimerEntity timer = new SQLTimerEntity(System.currentTimeMillis());
        String SQL = "DELETE FROM " + table + " WHERE" + conditional.build();
        try (Connection connection = getInstance().getDataSource().getConnection()) {
            PreparedStatement statement = connection.prepareStatement(SQL);
            int i = 1;
            for (String key : conditional.getConditions().keySet()) {
                statement.setObject(i, conditional.getConditions().get(key));
                i++;
            }

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("An error occurred while deleting a record", e);
        }

        SQLUtils.loggingQuery(timer, SQL);
    }

}
