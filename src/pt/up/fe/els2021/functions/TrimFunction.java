package pt.up.fe.els2021.functions;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.OptBoolean;
import pt.up.fe.els2021.Table;

import java.util.ArrayList;
import java.util.List;

public record TrimFunction(@JacksonInject(value = "0", useInput = OptBoolean.TRUE) int offset,
                           @JacksonInject(value = "-1", useInput = OptBoolean.TRUE) int limit) implements TableFunction {

    @Override
    public Table apply(Table table) throws Exception {
        var end = limit < 0 ? table.rowCount() : Integer.min(offset + limit, table.rowCount());
        var newColumns = new ArrayList<List<String>>();
        for (var col : table.columns()) {
            newColumns.add(col.subList(offset, end));
        }
        return new Table(table.columnNames(), newColumns);
    }

    public static final class Builder extends TableFunction.Builder {
        private int offset = 0;
        private int limit = -1;

        @Override
        public TableFunction build() {
            return new TrimFunction(offset, limit);
        }

        public Builder withOffset(int offset) {
            this.offset = offset;
            return this;
        }

        public Builder withLimit(int limit) {
            this.limit = limit;
            return this;
        }
    }
}
