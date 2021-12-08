package pt.up.fe.els2021.functions;

import pt.up.fe.els2021.Table;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public record ExcludeFunction(List<String> excludes) implements TableFunction {


    @Override
    public Table apply(Table table) throws Exception {
        var newColumnNames = new ArrayList<String>();
        var newColumns = new ArrayList<List<String>>();

        var excludes = Set.copyOf(this.excludes);

        for (var i = 0; i < table.columnNames().size(); i++) {
            var colName = table.columnNames().get(i);
            if (excludes.contains(colName)) continue;

            newColumnNames.add(colName);
            newColumns.add(table.columns().get(i));
        }

        return new Table(newColumnNames, newColumns);
    }

    public static final class Builder extends TableFunction.Builder {

        final private List<String> excludes = new ArrayList<>();

        @Override
        public TableFunction build() {
            return new ExcludeFunction(excludes);
        }

        public Builder column(String column) {
            excludes.add(column);
            return this;
        }
    }
}
