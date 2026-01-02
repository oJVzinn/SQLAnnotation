package com.github.ojvzinn.sqlannotation.interfaces;

import com.github.ojvzinn.sqlannotation.model.ConditionalModel;
import com.github.ojvzinn.sqlannotation.model.OrderModel;
import org.json.JSONArray;

public interface Repository<T> {

    T findByKey(Object key);
    JSONArray findAll();
    JSONArray findAll(OrderModel order);
    JSONArray findAllByConditionals(ConditionalModel conditionals);
    JSONArray findAllByCondition(ConditionalModel conditionals, OrderModel order);
    void save(T entity);
    void deleteRows();
    void deleteByKey(Object key);
    void deleteAllByConditionals(ConditionalModel conditionals);
    void deleteAll();

}
