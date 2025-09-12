package com.github.ojvzinn.sqlannotation.interfaces;

import com.github.ojvzinn.sqlannotation.entity.ConditionalEntity;
import org.json.JSONArray;

public interface Repository<T> {

    T findByKey(Object key);
    JSONArray findAll();
    JSONArray findAllByConditionals(ConditionalEntity conditionals);
    void save(T entity);
    void deleteRows();
    void deleteByKey(T entity);
    void deleteAllByConditionals(ConditionalEntity conditionals);
    void deleteAll();

}
