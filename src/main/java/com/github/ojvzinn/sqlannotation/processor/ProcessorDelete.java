package com.github.ojvzinn.sqlannotation.processor;

import com.github.ojvzinn.sqlannotation.SQLAnnotation;
import com.github.ojvzinn.sqlannotation.entity.ConditionalEntity;
import com.github.ojvzinn.sqlannotation.enums.ConnectiveType;
import com.github.ojvzinn.sqlannotation.enums.DeleteType;
import com.github.ojvzinn.sqlannotation.interfaces.Processor;

import java.lang.reflect.Method;

public class ProcessorDelete implements Processor {

    @Override
    public Object process(Method method, Object[] args, Class<?> entity) {
        String type = method.getName().split("delete")[1];
        DeleteType deleteType = DeleteType.findByType(type);
        if (deleteType == null) {
            throw new RuntimeException("Type " + type + " not found");
        }

        switch (deleteType) {
            case ROWS: {
                SQLAnnotation.getConfig().getSQLDataBase().getDeleteModule().truncate(entity);
                return null;
            }

            case BY_KEY: {
                String name = type.split(deleteType.getType())[1];
                if (name.equals("Key")) {
                    SQLAnnotation.getConfig().getSQLDataBase().getDeleteModule().deleteByKey(entity, args[0]);
                    return null;
                }

                ConditionalEntity conditional = new ConditionalEntity(ConnectiveType.NONE);
                conditional.appendConditional(name, args[0]);
                SQLAnnotation.getConfig().getSQLDataBase().getDeleteModule().deleteByConditionals(entity, conditional);
                return null;
            }

            case ALL: {
                SQLAnnotation.getConfig().getSQLDataBase().getDeleteModule().deleteAll(entity);
                return null;
            }

            case BY_CONDITIONALS: {
                if (args[0] instanceof ConditionalEntity) {
                    SQLAnnotation.getConfig().getSQLDataBase().getDeleteModule().deleteByConditionals(entity, (ConditionalEntity) args[0]);
                    return null;
                }

                String name = type.split(deleteType.getType())[1];
                String[] conditionals = name.split("And");
                ConditionalEntity conditional = new ConditionalEntity(ConnectiveType.AND);
                for (int i = 0; i < conditionals.length; i++) {
                    conditional.appendConditional(conditionals[i], args[i]);
                }

                SQLAnnotation.getConfig().getSQLDataBase().getDeleteModule().deleteByConditionals(entity, conditional);
                return null;
            }
        }

        return null;
    }

}
