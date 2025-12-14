package com.github.ojvzinn.sqlannotation.model;

import com.github.ojvzinn.sqlannotation.enums.OrderType;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;

import java.util.Set;

@RequiredArgsConstructor
public class OrderModel {

    private final SelectJoinModel selectJoinModel;

    private final JSONObject order = new JSONObject();

    public OrderModel appendAppendOrder(OrderType orderType, String column) {
        order.put((selectJoinModel != null ? selectJoinModel.getTableReference() + "." : "") + column, orderType.name());
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
