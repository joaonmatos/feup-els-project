package pt.up.fe.els2021.functions;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import pt.up.fe.els2021.Table;

import java.util.ArrayList;

public final class AddColumnFunction
        implements TableFunction {
    private final String name;
    private final String value;

    @JsonCreator
    public AddColumnFunction(@JsonProperty("name") String name, @JsonProperty("value") String value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public Table apply(Table table) throws Exception {
        var newColumn = new ArrayList<String>();
        for (var i = 0; i < table.rowCount(); i++) {
            newColumn.add(value);
        }
        var newColumnNames = new ArrayList<>(table.columnNames());
        newColumnNames.add(name);
        var newColumns = new ArrayList<>(table.columns());
        newColumns.add(newColumn);
        return new Table(newColumnNames, newColumns);
    }
}
