package pt.up.fe.els2021.combinators;

import pt.up.fe.els2021.Table;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public final class MergeCombinator implements TableCombinator {

    @Override
    public Table combine(Table a, Table b) {
        var tableAColumns = new LinkedHashMap<String, Integer>();
        var i = 0;
        for (var col : a.columnNames())
            tableAColumns.put(col, i++);

        var tableARowCount = a.rowCount();
        var tableBRowCount = b.rowCount();

        var newColumnNames = new ArrayList<>(a.columnNames());
        var newColumns = new ArrayList<>(a.columns());
        for (var colIx = 0; colIx < b.columns().size(); colIx++) {
            var columnName = b.columnNames().get(colIx);
            var column = b.columns().get(colIx);

            if (tableAColumns.containsKey(columnName)) {
                // column in a and b
                var newColIx = tableAColumns.get(columnName);
                newColumns.get(newColIx).addAll(column);
            } else {
                // column in b but not in a
                newColumnNames.add(columnName);
                var newCol = new ArrayList<String>();
                for (var ignored = 0; ignored < tableARowCount; ignored++)
                    newCol.add(null);
                newCol.addAll(column);
                newColumns.add(newCol);
            }
        }

        // deal with columns in a but not in b
        for (var col : newColumns) {
            if (col.size() == tableARowCount) {
                for (var ignored = 0; ignored < tableBRowCount; ignored++)
                    col.add(null);
            }
        }

        return new Table(newColumnNames, newColumns);
    }
}
