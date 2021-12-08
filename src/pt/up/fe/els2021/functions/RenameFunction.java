package pt.up.fe.els2021.functions;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import pt.up.fe.els2021.Table;

public final class RenameFunction
        implements TableFunction {
    private final String columnName;
    private final String newColumnName;

    @JsonCreator
    public RenameFunction(@JsonProperty("from") String columnName, @JsonProperty("to") String newColumnName) {
        this.columnName = columnName;
        this.newColumnName = newColumnName;
    }

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
