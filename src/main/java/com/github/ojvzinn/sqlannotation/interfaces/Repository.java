package com.github.ojvzinn.sqlannotation.interfaces;

public interface Repository<T> {

    T findByKey(Object key);

}
