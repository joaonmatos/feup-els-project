package pt.up.fe.els2021.functions;

import com.fasterxml.jackson.annotation.JsonProperty;

import pt.up.fe.els2021.Table;

public record RenameFunction(@JsonProperty("from") String columnName, @JsonProperty("to") String newColumnName)
        implements TableFunction {

    @Override
    public Table apply(Table table) {
        var newColumnNames = table.columnNames().stream().map(name -> {
            if (name.equals(columnName))
                return newColumnName;
            else
                return name;
        }).toList();
        return new Table(newColumnNames, table.columns());
    }

}
