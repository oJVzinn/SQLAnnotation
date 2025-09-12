package com.github.ojvzinn.sqlannotation.modules;

import com.github.ojvzinn.sqlannotation.SQL;
import com.github.ojvzinn.sqlannotation.annotations.Entity;
import com.github.ojvzinn.sqlannotation.annotations.PrimaryKey;
import com.github.ojvzinn.sqlannotation.entity.ConditionalEntity;
import com.github.ojvzinn.sqlannotation.enums.ConnectiveType;
import com.github.ojvzinn.sqlannotation.utils.SQLUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

public class DeleteModule extends Module {

    public DeleteModule(SQL instance) {
        super(instance);
    }

    public void drop(Class<?> entity) {
        Entity tableName = SQLUtils.checkIfClassValid(entity);
        try (Connection connection = getInstance().getDataSource().getConnection()) {
            Statement statement = connection.createStatement();
            String sql = "DROP TABLE " + tableName.name();
            statement.execute(sql);
            SQLUtils.loggingQuery(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void truncate(Class<?> entity) {
        Entity tableName = SQLUtils.checkIfClassValid(entity);
        try (Connection connection = getInstance().getDataSource().getConnection()) {
            Statement statement = connection.createStatement();
            String sql = "TRUNCATE TABLE " + tableName.name();
            statement.execute(sql);
            SQLUtils.loggingQuery(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteByKey(Class<?> entity, Object key) {
        ConditionalEntity conditional = new ConditionalEntity(ConnectiveType.NONE);
        conditional.appendConditional(SQLUtils.findPrimaryKey(entity).getName(), key);
        delete(entity, conditional);
    }

    public void delete(Object entity, ConditionalEntity conditional) {
        Entity tableName = SQLUtils.checkIfClassValid(entity.getClass());
        String SQL = "DELETE FROM " + tableName.name() + " WHERE" + conditional.build();
        SQLUtils.loggingQuery(SQL);
        try (Connection connection = getInstance().getDataSource().getConnection()) {
            PreparedStatement statement = connection.prepareStatement(SQL);
            int i = 1;
            for (String key : conditional.getConditions().keySet()) {
                statement.setObject(i, conditional.getConditions().get(key));
                i++;
            }

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
