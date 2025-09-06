package com.github.ojvzinn.sqlannotation.interfaces;

import java.lang.reflect.Method;

public interface Processor {

    Object process(Method method, Object[] args, Class<?> entity);

}
