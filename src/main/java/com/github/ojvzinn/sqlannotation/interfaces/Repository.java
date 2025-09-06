package com.github.ojvzinn.sqlannotation.interfaces;

import com.github.ojvzinn.sqlannotation.entity.ConditionalEntity;
import org.json.JSONArray;

public interface Repository<T> {

    T findByKey(Object key);
    JSONArray findAll();
    JSONArray findAllByConditionals(ConditionalEntity conditionals);

}
