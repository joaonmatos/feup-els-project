package pt.up.fe.els2021.functions;

import pt.up.fe.els2021.Program;
import pt.up.fe.els2021.Table;
import pt.up.fe.els2021.combinators.MergeCombinator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public final class GroupByFunction implements TableFunction {

    private final String columnName;
    private final String inputTableName;
    private final String outputTableName;
    private final Program program;

    public GroupByFunction(String columnName, String inputTableName, String outputTableName, Program program) {
        this.columnName = columnName;
        this.inputTableName = inputTableName;
        this.outputTableName = outputTableName;
        this.program = program;
    }


    @Override
    public Table apply(Table table) throws Exception {
        var tables = separateTables(table, columnName);
        var transformedTables = new ArrayList<Table>();
        for (var innerTable : tables) {
            var programState = new HashMap<String, Table>();
            programState.put(inputTableName, innerTable);
            program.run(programState);
            transformedTables.add(programState.get(outputTableName));
        }
        return transformedTables.stream().reduce(
                new Table(Collections.emptyList(), Collections.emptyList()),
                (a, b) -> (new MergeCombinator()).combine(a, b)
        );
    }

    private static List<Table> separateTables(Table table, String columnName) {
        var targetColumnIndex = table.columnNames().indexOf(columnName);
        var map = new HashMap<String, Table>();
        for (var rowIndex = 0; rowIndex < table.rowCount(); rowIndex++) {
            var value = table.columns().get(targetColumnIndex).get(rowIndex);
            if (!map.containsKey(value)) {
                var innerColumns = new ArrayList<List<String>>();
                for (var i = 0; i < table.columns().size(); i++) {
                    innerColumns.add(new ArrayList<>());
                }
                map.put(value, new Table(table.columnNames(), innerColumns));
            }
            var innerTable = map.get(value);
            for (var columnIndex = 0; columnIndex < table.columns().size(); columnIndex++) {
                innerTable.columns().get(columnIndex).add(
                        table.columns().get(columnIndex).get(rowIndex)
                );
            }
        }
        return new ArrayList<>(map.values());
    }
}
