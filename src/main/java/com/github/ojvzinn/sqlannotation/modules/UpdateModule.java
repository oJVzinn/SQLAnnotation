package com.github.ojvzinn.sqlannotation.modules;

import com.github.ojvzinn.sqlannotation.SQL;
import com.github.ojvzinn.sqlannotation.annotations.Entity;
import com.github.ojvzinn.sqlannotation.entity.ConditionalEntity;
import com.github.ojvzinn.sqlannotation.entity.SQLTimerEntity;
import com.github.ojvzinn.sqlannotation.enums.ClassType;
import com.github.ojvzinn.sqlannotation.utils.SQLUtils;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class UpdateModule extends Module {

    public UpdateModule(SQL instance) {
        super(instance);
    }

    public void update(Object entity, ConditionalEntity conditionals) {
        Entity tableName = SQLUtils.checkIfClassValid(entity.getClass());
        SQLTimerEntity timer = new SQLTimerEntity(System.currentTimeMillis());
        String SQL = "UPDATE " + tableName.name() + " SET " + makeColumns(entity) + " WHERE" + conditionals.build();
        try (Connection connection = getInstance().getDataSource().getConnection()) {
            PreparedStatement statement = connection.prepareStatement(SQL);
            int i = 1;
            for (String key : conditionals.getConditions().keySet()) {
                statement.setObject(i, conditionals.getConditions().get(key));
                i++;
            }

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("An error occurred while updating the entity", e);
        }

        SQLUtils.loggingQuery(timer, SQL);
    }

    private String makeColumns(Object entity) {
        List<Field> columnsFields = SQLUtils.listFieldColumns(entity.getClass());
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < columnsFields.size(); i++) {
            Field field = columnsFields.get(i);
            field.setAccessible(true);
            try {
                Object value = field.get(entity);
                ClassType type = ClassType.getType(value.getClass());
                sb.append(field.getName()).append(" = ").append(type == ClassType.VARCHAR || type == ClassType.TEXT ? ("'" + value + "'") : value);
            } catch (Exception e)  {
                throw new RuntimeException("An error occurred while loading columns", e);
            }

            if (i + 1 < columnsFields.size()) sb.append(", ");
        }

        return sb.toString();
    }
}
