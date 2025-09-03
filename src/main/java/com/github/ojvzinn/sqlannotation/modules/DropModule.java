package com.github.ojvzinn.sqlannotation.modules;

import com.github.ojvzinn.sqlannotation.SQL;
import com.github.ojvzinn.sqlannotation.annotations.Entity;
import com.github.ojvzinn.sqlannotation.utils.SQLUtils;
import lombok.AllArgsConstructor;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DropModule extends Module {

    public DropModule(SQL instance) {
        super(instance);
    }

    public void dropTable(Class<?> entity) {
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

}
