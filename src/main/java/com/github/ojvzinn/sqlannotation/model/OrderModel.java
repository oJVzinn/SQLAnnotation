package com.github.ojvzinn.sqlannotation.model;

import com.github.ojvzinn.sqlannotation.enums.OrderType;
import lombok.RequiredArgsConstructor;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor
public class OrderModel {

    private final SelectJoinModel selectJoinModel;

    private final Map<String, String> order = new LinkedHashMap<>();

    public OrderModel appendAppendOrder(OrderType orderType, String column) {
        order.put((selectJoinModel != null ? selectJoinModel.getEntityTableReference() + "." : "") + column, orderType.name());
        return this;
    }

    public String build() {
        StringBuilder sql = new StringBuilder();
        Set<String> keys = this.order.keySet();
        int i = 0;
        for (String column : keys) {
            sql.append(" ").append(column).append(" ").append(order.get(column));
            if (i + 1 != keys.size()) sql.append(",");
            i++;
        }

        return sql.toString();
    }
}
