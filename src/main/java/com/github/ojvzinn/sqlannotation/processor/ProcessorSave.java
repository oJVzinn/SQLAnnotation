package com.github.ojvzinn.sqlannotation.processor;

import com.github.ojvzinn.sqlannotation.SQL;
import com.github.ojvzinn.sqlannotation.SQLAnnotation;
import com.github.ojvzinn.sqlannotation.model.ConditionalModel;
import com.github.ojvzinn.sqlannotation.enums.ConnectiveType;
import com.github.ojvzinn.sqlannotation.interfaces.Processor;
import com.github.ojvzinn.sqlannotation.utils.SQLUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ProcessorSave implements Processor {

    @Override
    public Object process(Method method, Object[] args, Class<?> entity) {
        Object entitySave = args[0];
        Field primarykeyField = SQLUtils.findPrimaryKey(entitySave.getClass());
        SQL sqlDB = SQLAnnotation.getConfig().getSQLDataBase();
        try {
            primarykeyField.setAccessible(true);
            Object primarykey = primarykeyField.get(entitySave);
            boolean exists = primarykey != null && (sqlDB.getSelectModule().findByKey(entitySave.getClass(), primarykey) != null);
            if (exists) {
                sqlDB.getUpdateModule().update(entitySave, new ConditionalModel(ConnectiveType.NONE).appendConditional(primarykeyField.getName(), primarykey));
                return null;
            }

            sqlDB.getInsertModule().insert(entitySave);
        } catch (Exception e) {
            throw new RuntimeException("There is no primary key column in your table", e);
        }

        return null;
    }

}
