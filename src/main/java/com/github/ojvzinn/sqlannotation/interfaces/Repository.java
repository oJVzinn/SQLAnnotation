package com.github.ojvzinn.sqlannotation.interfaces;

import com.github.ojvzinn.sqlannotation.model.ConditionalModel;
import com.github.ojvzinn.sqlannotation.model.OrderModel;

import java.util.List;

public interface Repository<T> {

    T findByKey(Object key);
    List<T> findAll();
    List<T> findAll(OrderModel order);
    List<T> findAllByConditionals(ConditionalModel conditionals);
    List<T> findAllByCondition(ConditionalModel conditionals, OrderModel order);
    void save(T entity);
    void deleteRows();
    void deleteByKey(Object key);
    void deleteAllByConditionals(ConditionalModel conditionals);
    void deleteAll();

}
