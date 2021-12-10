package pt.up.fe.els2021.functions;

import com.fasterxml.jackson.annotation.JsonProperty;
import pt.up.fe.els2021.Table;

import java.util.ArrayList;
import java.util.List;

public record MoveColumnFunction(String column, @JsonProperty("toIndex") int index) implements TableFunction {
    @Override
    public Table apply(Table table) throws Exception {
        var colIndex = table.columnNames().indexOf(column);
        if (colIndex < 0) {
            throw new Exception("MoveColumnFunction: did not find a column named " + column);
        }
        if (colIndex == index) {
            return table;
        }

        var newColumnNames = new ArrayList<String>();
        var newColumns = new ArrayList<List<String>>();

        var oldIndex = 0;
        var newIndex = 0;
        while (oldIndex < table.columnNames().size()) {
            if (oldIndex == colIndex) {
                // ignore old instance of moved column
                oldIndex++;
            } else if (newIndex == index) {
                // move column to new place
                newIndex++;
                newColumnNames.add(table.columnNames().get(colIndex));
                newColumns.add(table.columns().get(colIndex));
            } else {
                newColumnNames.add(table.columnNames().get(oldIndex));
                newColumns.add(table.columns().get(oldIndex));
                oldIndex++;
                newIndex++;
            }
        }

        return new Table(newColumnNames, newColumns);
    }
}
