package com.github.ojvzinn.sqlannotation.processor;

import com.github.ojvzinn.sqlannotation.SQL;
import com.github.ojvzinn.sqlannotation.SQLAnnotation;
import com.github.ojvzinn.sqlannotation.annotations.Entity;
import com.github.ojvzinn.sqlannotation.entity.SQLConfigEntity;
import com.github.ojvzinn.sqlannotation.interfaces.Processor;
import com.github.ojvzinn.sqlannotation.utils.SQLUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ProcessorSave implements Processor {

    @Override
    public Object process(Method method, Object[] args, Class<?> entity) {
        Object entitySave = args[0];
        Entity entityConfiguration = SQLUtils.checkIfClassValid(entitySave.getClass());
        Field primarykeyField = SQLUtils.findPrimaryKey(entitySave.getClass());
        SQL sqlDB = SQLAnnotation.getConfig().getSQLDataBase();
        try {
            boolean exists = (sqlDB.getSelectModule().findByKey(entitySave.getClass(), primarykeyField.get(entitySave)) != null);
            if (exists) {
                sqlDB.get;
                return null;
            }

        } catch (Exception e) {
            throw new RuntimeException("There is no primary key column in your table");
        }

        return null;
    }

}
