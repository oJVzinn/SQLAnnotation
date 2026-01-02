package com.github.ojvzinn.sqlannotation.processor;

import com.github.ojvzinn.sqlannotation.SQLAnnotation;
import com.github.ojvzinn.sqlannotation.model.ConditionalModel;
import com.github.ojvzinn.sqlannotation.enums.ConnectiveType;
import com.github.ojvzinn.sqlannotation.enums.FindType;
import com.github.ojvzinn.sqlannotation.interfaces.Processor;
import com.github.ojvzinn.sqlannotation.model.OrderModel;
import com.github.ojvzinn.sqlannotation.model.SelectJoinModel;
import com.github.ojvzinn.sqlannotation.utils.SQLUtils;

import java.lang.reflect.Method;

public class ProcessorFind implements Processor {

    @Override
    public Object process(Method method, Object[] args, Class<?> entity) {
        String type = method.getName().split("find")[1];
        FindType findType = FindType.findByType(type);
        SelectJoinModel joinModel = SQLUtils.getSelectJoinModel(entity);
        if (findType == null) throw new RuntimeException("Type " + type + " not found");
        OrderModel order = extractOrderModel(args);
        switch (findType) {
            case BY_KEY: {
                String name = type.split(findType.getType())[1];
                if (name.equals("Key")) {
                    return SQLAnnotation.getConfig().getSQLDataBase().getSelectModule().findByKey(entity, joinModel, args[0]);
                }

                ConditionalModel conditional = new ConditionalModel(ConnectiveType.NONE, joinModel);
                conditional.appendConditional(name, args[0]);
                return SQLAnnotation.getConfig().getSQLDataBase().getSelectModule().findByConditionals(entity, joinModel, conditional, order);
            }

            case ALL: {
                return SQLAnnotation.getConfig().getSQLDataBase().getSelectModule().findAll(entity, order);
            }

            case BY_CONDITIONALS: {
                if (args[0] instanceof ConditionalModel)
                    return SQLAnnotation.getConfig().getSQLDataBase()
                            .getSelectModule()
                            .findResult(entity, joinModel, (ConditionalModel) args[0], order);

                String name = type.split(findType.getType())[1];
                String[] conditionals = name.split("And");
                ConditionalModel conditional = new ConditionalModel(ConnectiveType.AND, joinModel);
                for (int i = 0; i < conditionals.length; i++) conditional.appendConditional(conditionals[i], args[i]);
                return SQLAnnotation.getConfig().getSQLDataBase().getSelectModule().findResult(entity, joinModel, conditional, order);
            }
        }

        return null;
    }

    private OrderModel extractOrderModel(Object[] args) {
        OrderModel order = null;
        for (Object arg : args)
            if (arg instanceof OrderModel) {
                order = (OrderModel) arg;
                break;
            }

        return order;
    }
}
