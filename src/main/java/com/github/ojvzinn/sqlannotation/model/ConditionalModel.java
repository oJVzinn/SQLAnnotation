package com.github.ojvzinn.sqlannotation.model;

import com.github.ojvzinn.sqlannotation.enums.ConnectiveType;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;

import java.util.Set;

@RequiredArgsConstructor
@Getter
public class ConditionalModel {

    @NonNull
    private ConnectiveType type;

    private final SelectJoinModel selectJoinModel;

    private final JSONObject conditions = new JSONObject();

    public ConditionalModel appendConditional(String column, Object value) {
        conditions.put((selectJoinModel != null ? selectJoinModel.getEntityTableReference() + "." : "") + column + " = ?", value);
        return this;
    }

    public String build() {
        StringBuilder sql = new StringBuilder();
        Set<String> keys = this.conditions.keySet();
        int i = 0;
        for (String conditional : keys) {
            sql.append(" ").append(conditional);
            if (i + 1 != keys.size()) sql.append(" ").append(this.type.name());
            i++;
        }

        return sql.toString();
    }

}
