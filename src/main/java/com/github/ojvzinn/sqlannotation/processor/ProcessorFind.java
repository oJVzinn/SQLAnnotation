package com.github.ojvzinn.sqlannotation.processor;

import com.github.ojvzinn.sqlannotation.SQLAnnotation;
import com.github.ojvzinn.sqlannotation.entity.ConditionalEntity;
import com.github.ojvzinn.sqlannotation.enums.ConnectiveType;
import com.github.ojvzinn.sqlannotation.enums.FindType;
import com.github.ojvzinn.sqlannotation.interfaces.Processor;

import java.lang.reflect.Method;

public class ProcessorFind implements Processor {

    @Override
    public Object process(Method method, Object[] args, Class<?> entity) {
        String type = method.getName().split("find")[1];
        FindType findType = FindType.findByType(type);
        if (findType == null) {
            throw new RuntimeException("Type " + type + " not found");
        }

        switch (findType) {
            case BY_KEY: {
                String name = type.split(findType.getType())[1];
                if (name.equals("Key")) {
                    return SQLAnnotation.getConfig().getSQLDataBase().getSelectModule().findByKey(entity, args[0]);
                }

                ConditionalEntity conditional = new ConditionalEntity(ConnectiveType.NONE);
                conditional.appendConditional(name, args[0]);
                return SQLAnnotation.getConfig().getSQLDataBase().getSelectModule().findResult(entity, conditional);
            }

            case ALL: {
                return SQLAnnotation.getConfig().getSQLDataBase().getSelectModule().findAll(entity);
            }

            case BY_CONDITIONALS: {
                if (args[0] instanceof ConditionalEntity) {
                    return SQLAnnotation.getConfig().getSQLDataBase().getSelectModule().findResult(entity, (ConditionalEntity) args[0]);
                }

                String name = type.split(findType.getType())[1];
                String[] conditionals = name.split("And");
                ConditionalEntity conditional = new ConditionalEntity(ConnectiveType.AND);
                for (int i = 0; i < conditionals.length; i++) {
                    conditional.appendConditional(conditionals[i], args[i]);
                }

                return SQLAnnotation.getConfig().getSQLDataBase().getSelectModule().findResult(entity, conditional);
            }
        }

        return null;
    }

}
