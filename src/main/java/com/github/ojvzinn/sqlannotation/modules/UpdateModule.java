package com.github.ojvzinn.sqlannotation.modules;

import com.github.ojvzinn.sqlannotation.SQL;
import com.github.ojvzinn.sqlannotation.annotations.Entity;
import com.github.ojvzinn.sqlannotation.model.ConditionalModel;
import com.github.ojvzinn.sqlannotation.model.SQLTimerModel;
import com.github.ojvzinn.sqlannotation.utils.SQLUtils;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class UpdateModule extends Module {

    public UpdateModule(SQL instance) {
        super(instance);
    }

    public void update(Object entity, ConditionalModel conditionals) {
        Entity tableName = SQLUtils.checkIfClassValid(entity.getClass());
        SQLTimerModel timer = new SQLTimerModel(System.currentTimeMillis());
        StringBuilder columnsBuilder = new StringBuilder();
        LinkedList<Object> updateValues = loadUpdateValues(columnsBuilder, entity);
        String sql = "UPDATE " + tableName.name() + " SET " + columnsBuilder + " WHERE" + conditionals.build();
        try (Connection connection = getInstance().getDataSource().getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql);
            int index = 1;
            for (Object value : updateValues) {
                statement.setObject(index++, value);
            }
            for (String key : conditionals.getConditions().keySet()) {
                statement.setObject(index++, conditionals.getConditions().get(key));
            }

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("An error occurred while updating the entity", e);
        }

        SQLUtils.loggingQuery(timer, sql);
    }

    private LinkedList<Object> loadUpdateValues(StringBuilder columnsBuilder, Object entity) {
        List<Field> columnsFields = SQLUtils.listFieldColumns(entity.getClass(), false);
        LinkedList<Object> values = new LinkedList<>();
        for (int i = 0; i < columnsFields.size(); i++) {
            Field field = columnsFields.get(i);
            field.setAccessible(true);
            try {
                Object value = field.get(entity);
                if (value != null && SQLUtils.isJoinField(field, value)) {
                    value = SQLUtils.getValueJoinField(field, value);
                }
                columnsBuilder.append(field.getName()).append(" = ?");
                values.add(value);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("An error occurred while loading field values", e);
            }

            if (i + 1 < columnsFields.size()) {
                columnsBuilder.append(", ");
            }
        }

        return values;
    }
}
