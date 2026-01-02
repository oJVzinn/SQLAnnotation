package com.github.ojvzinn.sqlannotation.processor;

import com.github.ojvzinn.sqlannotation.annotations.Entity;
import com.github.ojvzinn.sqlannotation.enums.ProcessorType;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;

public class RepositoryProcessor {

    public Object processRepository(Class<?> repository) {
        Class<?> finalEntity = getEntityClass(repository);
        return Proxy.newProxyInstance(
                repository.getClassLoader(),
                new Class<?>[]{repository},
                (proxy, method, methodArgs) -> {
                    try {
                        ProcessorType processorType = ProcessorType.findByKey(method.getName());
                        if (processorType != null)
                            return processorType.getProcessor().process(method, methodArgs, finalEntity);
                    } catch (Exception e) {
                        throw new RuntimeException("An error occurred while processing the repository function", e);
                    }

                    return null;
                }
        );
    }

    private Class<?> getClassFromType(Type type) {
        if (type instanceof Class<?>) {
            return (Class<?>) type;
        } else if (type instanceof ParameterizedType) {
            return (Class<?>) ((ParameterizedType) type).getRawType();
        }

        return null;
    }

    private Class<?> getEntityClass(Class<?> repository) {
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
