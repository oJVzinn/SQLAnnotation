package com.github.ojvzinn.sqlannotation.interfaces;

import com.github.ojvzinn.sqlannotation.model.ConditionalModel;
import org.json.JSONArray;

public interface Repository<T> {

    T findByKey(Object key);
    JSONArray findAll();
    JSONArray findAllByConditionals(ConditionalModel conditionals);
    void save(T entity);
    void deleteRows();
    void deleteByKey(Object key);
    void deleteAllByConditionals(ConditionalModel conditionals);
    void deleteAll();

}
