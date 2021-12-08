package pt.up.fe.els2021.functions;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.OptBoolean;
import pt.up.fe.els2021.Table;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeMap;

public record OrderByFunction(@JsonProperty("by") String key,
                              @JacksonInject(value = "STRING", useInput = OptBoolean.TRUE) @JsonProperty("as") SortType type,
                              @JacksonInject(value = "ASCENDING", useInput = OptBoolean.TRUE) SortDirection direction
) implements TableFunction {

    @Override
    public Table apply(Table table) throws Exception {
        var keyIndex = getKeyIndex(table);

        var newColumns = new ArrayList<List<String>>();
        for (var i = 0; i < table.columns().size(); i++) {
            var column = table.columns().get(i);
            var newColumn = new ArrayList<String>();
            for (var entry : keyIndex.entrySet()) {
                var rows = entry.getValue();
                for (var row : rows) {
                    newColumn.add(column.get(row));
                }
            }
            newColumns.add(newColumn);
        }
        return new Table(table.columnNames(), newColumns);
    }

    private TreeMap<String, List<Integer>> getKeyIndex(Table table) {
        int columnIndex = getColumnIndex(table);
        var comparator = getComparator();

        var keyIndex = new TreeMap<String, List<Integer>>(comparator);
        var rowCount = table.rowCount();
        for (var row = 0; row < rowCount; row++) {
            var key = table.columns().get(columnIndex).get(row);
            if (!keyIndex.containsKey(key))
                keyIndex.put(key, new ArrayList<>());

            keyIndex.get(key).add(row);
        }
        return keyIndex;
    }

    private Comparator<String> getComparator() {
        Comparator<String> comparator =
                type == SortType.STRING
                        ? String::compareTo
                        : Comparator.comparingDouble(Double::parseDouble);
        if (direction == SortDirection.DESCENDING)
            comparator = comparator.reversed();
        return comparator;
    }

    private int getColumnIndex(Table table) {
        var keyColumnIndex = -1;
        for (var i = 0; i < table.columns().size(); i++) {
            if (table.columnNames().get(i).equals(this.key)) {
                keyColumnIndex = i;
                break;
            }
        }
        return keyColumnIndex;
    }

    public enum SortType {
        STRING,
        NUMBER
    }

    public enum SortDirection {
        ASCENDING,
        DESCENDING
    }

    public static final class Builder extends TableFunction.Builder {
        private final String key;
        private SortType type = SortType.STRING;
        private SortDirection direction = SortDirection.ASCENDING;

        public Builder(String key) {
            this.key = key;
        }

        @Override
        public TableFunction build() {
            return new OrderByFunction(key, type, direction);
        }

        public Builder sortAs(SortType type) {
            this.type = type;
            return this;
        }

        public Builder withDirection(SortDirection direction) {
            this.direction = direction;
            return this;
        }
    }


}
