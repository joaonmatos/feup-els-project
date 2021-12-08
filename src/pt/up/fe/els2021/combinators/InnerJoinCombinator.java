package pt.up.fe.els2021.combinators;

import pt.up.fe.els2021.Table;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public record InnerJoinCombinator(String joinColumn) implements TableCombinator {


    @Override
    public Table combine(Table a, Table b) {
        if (a.rowCount() == 0 || b.rowCount() == 0)
            return new Table(Collections.emptyList(), Collections.emptyList());

        var newColumnNames = getNewColumnNames(a, b);
        var newColumns = new ArrayList<List<String>>();
        for (var ignored : newColumnNames) {
            newColumns.add(new ArrayList<>());
        }

        var aColumnIndex = -1;
        for (int i = 0; i < a.columnNames().size(); i++) {
            if (a.columnNames().get(i).equals(joinColumn)) {
                aColumnIndex = i;
                break;
            }
        }
        var aColumn = a.columns().get(aColumnIndex);
        var bIndex = indexTableByColumn(b, joinColumn);
        for (int i = 0; i < aColumn.size(); i++) {
            var key = aColumn.get(i);
            if (key == null) continue;

            var bRowsIndices = bIndex.get(key);
            if (bRowsIndices != null) {
                var aRow = a.getRow(i).orElseThrow();
                for (var j : bRowsIndices) {
                    var bRow = b.getRow(j).orElseThrow();
                    var sawJoinColumn = false;
                    for (var k = 0; k < newColumns.size(); k++) {
                        if (k < aRow.size()) {
                            newColumns.get(k).add(aRow.get(k));
                        } else {
                            var bColumnIndex = k - aRow.size();
                            if (b.columnNames().get(bColumnIndex).equals(joinColumn)) {
                                sawJoinColumn = true;
                            }

                            newColumns.get(k).add(bRow.get(sawJoinColumn ? bColumnIndex + 1 : bColumnIndex));
                        }
                    }
                }
            }
        }

        return new Table(newColumnNames, newColumns);
    }

    private ArrayList<String> getNewColumnNames(Table a, Table b) {
        var newColumnNames = new ArrayList<>(a.columnNames());
        var bColumnNames = new ArrayList<>(b.columnNames());
        bColumnNames.remove(joinColumn);
        newColumnNames.addAll(bColumnNames);
        return newColumnNames;
    }

    private HashMap<String, List<Integer>> indexTableByColumn(Table table, String columnName) {
        var columnIndex = -1;
        for (int i = 0; i < table.columnNames().size(); i++) {
            if (table.columnNames().get(i).equals(columnName)) {
                columnIndex = i;
                break;
            }
        }

        var column = table.columns().get(columnIndex);
        var rowCount = table.rowCount();

        var result = new HashMap<String, List<Integer>>();
        for (int i = 0; i < rowCount; i++) {
            var element = column.get(i);
            if (element == null) continue;

            if (!result.containsKey(element)) {
                result.put(element, new ArrayList<>());
            }
            result.get(element).add(i);
        }
        return result;
    }
}
