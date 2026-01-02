package com.github.ojvzinn.sqlannotation.processor;

import com.github.ojvzinn.sqlannotation.SQL;
import com.github.ojvzinn.sqlannotation.SQLAnnotation;
import com.github.ojvzinn.sqlannotation.model.ConditionalModel;
import com.github.ojvzinn.sqlannotation.enums.ConnectiveType;
import com.github.ojvzinn.sqlannotation.interfaces.Processor;
import com.github.ojvzinn.sqlannotation.model.SelectJoinModel;
import com.github.ojvzinn.sqlannotation.utils.SQLUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ProcessorSave implements Processor {

    @Override
    public Object process(Method method, Object[] args, Class<?> entity) {
        Object entitySave = args[0];
        Field primarykeyField = SQLUtils.findPrimaryKey(entitySave.getClass());
        SQL sqlDB = SQLAnnotation.getConfig().getSQLDataBase();
        SelectJoinModel joinModel = SQLUtils.getSelectJoinModel(entity);
        try {
            primarykeyField.setAccessible(true);
            Object primaryKey = primarykeyField.get(entitySave);
            boolean exists = primaryKey != null && (sqlDB.getSelectModule().findByKey(entitySave.getClass(), joinModel, primaryKey) != null);
            if (exists) {
                sqlDB.getUpdateModule().update(entitySave, new ConditionalModel(ConnectiveType.NONE, joinModel)
                        .appendConditional(primarykeyField.getName(), primaryKey));
                return null;
            }

            sqlDB.getInsertModule().insert(entitySave);
        } catch (Exception e) {
            throw new RuntimeException("There is no primary key column in your table", e);
        }

        return null;
    }

}
