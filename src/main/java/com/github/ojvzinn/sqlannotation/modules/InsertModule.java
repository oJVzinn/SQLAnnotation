package com.github.ojvzinn.sqlannotation.modules;

import com.github.ojvzinn.sqlannotation.SQL;
import com.github.ojvzinn.sqlannotation.annotations.Entity;
import com.github.ojvzinn.sqlannotation.annotations.Join;
import com.github.ojvzinn.sqlannotation.model.SQLTimerModel;
import com.github.ojvzinn.sqlannotation.utils.SQLUtils;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

public class InsertModule extends Module {

    public InsertModule(SQL instance) {
        super(instance);
    }

    public void insert(Object entity) {
        Entity tableName = SQLUtils.checkIfClassValid(entity.getClass());
        SQLTimerModel timer = new SQLTimerModel(System.currentTimeMillis());
        StringBuilder columns = new StringBuilder();
        StringBuilder valuesReplace = new StringBuilder();
        LinkedList<Object> values = loadValues(columns, valuesReplace, entity);
        String SQL = "INSERT INTO " + tableName.name() + "(" + columns + ") VALUES (" + valuesReplace + ")";
        try (Connection connection = getInstance().getDataSource().getConnection()) {
            PreparedStatement statement = connection.prepareStatement(SQL);
            for (int i = 1; i <= values.size(); i++) {
                statement.setObject(i, values.get(i - 1));
            }

            statement.execute();
        } catch (SQLException e) {
            throw new RuntimeException("An error occurred while inserting the entity", e);
        }

        SQLUtils.loggingQuery(timer, SQL);
    }

    private LinkedList<Object> loadValues(StringBuilder columns, StringBuilder valuesReplace, Object entity) {
        List<Field> columnsFields = SQLUtils.listFieldColumns(entity.getClass());
        LinkedList<Object> values = new LinkedList<>();
        for (int i = 0; i < columnsFields.size(); i++) {
            Field field = columnsFields.get(i);
            field.setAccessible(true);
            try {
                Object value = field.get(entity);
                Join join = field.getAnnotation(Join.class);
                if (value.getClass().getAnnotation(Entity.class) != null && join != null) {
                    Field joinParameter = value.getClass().getDeclaredField(join.column());
                    value = joinParameter.get(value);
                }
                columns.append(field.getName());
                valuesReplace.append("?");
                values.add(value);
            } catch (Exception e)  {
                throw new RuntimeException(e);
            }

            if (i + 1 < columnsFields.size()) {
                columns.append(", ");
                valuesReplace.append(", ");
            }
        }

        return values;
    }

}
