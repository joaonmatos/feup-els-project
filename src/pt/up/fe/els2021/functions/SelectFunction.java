package pt.up.fe.els2021.functions;

import java.util.HashMap;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import pt.up.fe.els2021.Table;

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

    @Override
    public String toString() {
        if (columnNames.isEmpty()) {
            return "Select 0 columns";
        }
        var builder = new StringBuilder("Select ").append(columnNames.size()).append(" columns:\n");
        for (var name : columnNames) {
            builder.append(" - \"").append(name).append("\"\n");
        }
        return builder.toString();
    }
}
