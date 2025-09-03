package com.github.ojvzinn.sqlannotation;

import com.github.ojvzinn.sqlannotation.annotations.Entity;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class RepositoryProcessor {

    protected static Object processRepository(Class<?> repository) {
        Class<?> finalEntity = getEntityClass(repository);
        return Proxy.newProxyInstance(
                repository.getClassLoader(),
                new Class<?>[]{repository},
                (proxy, method, methodArgs) -> {
                    String name = method.getName();
                    if (name.startsWith("findBy")) {
                        return processFind(method, methodArgs, finalEntity);
                    }

                    return null;
                }
        );
    }

    private static Object processFind(Method method, Object[] args, Class<?> entity) {
        Map<String, Object> conditionals = new HashMap<>();
        String name = method.getName().split("findBy")[1];
        for (Object arg : args) {
            conditionals.put(name, arg);
        }

        return SQLAnnotation.getConfig().getSQLDataBase().getSelectModule().findByConditionals(entity, conditionals);
    }

    private static Class<?> getClassFromType(Type type) {
        if (type instanceof Class<?>) {
            return (Class<?>) type;
        } else if (type instanceof ParameterizedType) {
            return (Class<?>) ((ParameterizedType) type).getRawType();
        }
        return null;
    }

    private static Class<?> getEntityClass(Class<?> repository) {
        Class<?> entity = null;
        for (Type type : repository.getGenericInterfaces()) {
            if (type instanceof ParameterizedType) {
                ParameterizedType paramType = (ParameterizedType) type;
                for (Type arg : paramType.getActualTypeArguments()) {
                    Class<?> typeClass = getClassFromType(arg);
                    if (typeClass != null && typeClass.getAnnotation(Entity.class) != null) {
                        entity = typeClass;
                        break;
                    }
                }
            }
        }

        return entity;
    }
}
