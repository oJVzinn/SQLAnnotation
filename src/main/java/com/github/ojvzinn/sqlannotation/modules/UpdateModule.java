package com.github.ojvzinn.sqlannotation.modules;

import com.github.ojvzinn.sqlannotation.SQL;
import com.github.ojvzinn.sqlannotation.annotations.Entity;
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

    public void update(Object table) {
        Entity tableName = SQLUtils.checkIfClassValid(table.getClass());
        List<Field> columnsFields = SQLUtils.listFieldColumns(table.getClass());
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < columnsFields.size(); i++) {
            Field field = columnsFields.get(i);
            try {
                sb.append(field.getName()).append(" = ").append(field.get(table));
            } catch (Exception e)  {
                throw new RuntimeException(e);
            }

            if (i + 1 < columnsFields.size()) sb.append(", ");
        }

        try (Connection connection = getInstance().getDataSource().getConnection()) {
            PreparedStatement statement = connection.prepareStatement("UPDATE " + tableName.name() + " SET (" + sb.);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
