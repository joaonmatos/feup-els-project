package pt.up.fe.els2021.functions;

import com.fasterxml.jackson.annotation.JsonProperty;
import pt.up.fe.els2021.Table;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public record SelectFunction(@JsonProperty("columns") List<String> columnNames) implements TableFunction {

    @Override
    public Table apply(Table table) {
        var columnIndices = new HashMap<String, Integer>();
        for (int i = 0; i < table.columnNames().size(); i++) {
            var name = table.columnNames().get(i);
            columnIndices.put(name, i);
        }

        var newColumns = columnNames.stream().map(columnName -> table.columns().get(columnIndices.get(columnName)))
                .toList();

        return new Table(columnNames, newColumns);
    }

    public static final class Builder extends TableFunction.Builder {
        List<String> columns = new ArrayList<>();

        @Override
        public TableFunction build() {
            return new SelectFunction(columns);
        }

        public Builder column(String column) {
            columns.add(column);
            return this;
        }

    }
}
