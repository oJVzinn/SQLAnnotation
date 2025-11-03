package com.github.ojvzinn.sqlannotation.model;

import com.github.ojvzinn.sqlannotation.enums.OrderType;
import lombok.NoArgsConstructor;
import org.json.JSONObject;

import java.util.Set;

@NoArgsConstructor
public class OrderModel {

    private final JSONObject order = new JSONObject();

    public OrderModel appendAppendOrder(OrderType orderType, String column) {
        order.put(column, orderType.name());
        return this;
    }

    public String build() {
        StringBuilder sql = new StringBuilder();
        Set<String> keys = getConditions().keySet();
        int i = 0;
        for (String conditional : getConditions().keySet()) {
            sql.append(" ").append(conditional);
            if (i + 1 != keys.size()) sql.append(" ").append(getType().name());
            i++;
        }

        return sql.toString();
    }
}
