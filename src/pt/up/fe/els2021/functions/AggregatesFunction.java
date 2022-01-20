package pt.up.fe.els2021.functions;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import pt.up.fe.els2021.Table;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class AggregatesFunction
        implements TableFunction {

    private final Type type;

    @JsonCreator
    public AggregatesFunction(@JsonProperty("type") Type type) {
        this.type = type;
    }

    @Override
    public Table apply(Table table) throws Exception {
        var newColumnNames = new ArrayList<String>();
        var numericColumns = new ArrayList<List<Double>>();
        for (var i = 0; i < table.columns().size(); i++) {
            var parseResult = parseNumeric(table.columns().get(i));
            if (parseResult.isPresent()) {
                newColumnNames.add(table.columnNames().get(i));
                numericColumns.add(parseResult.get());
            }
        }
        var newColumns = numericColumns.stream().map(column -> switch (this.type) {
            case COUNT -> column.size();
            // unchecked because parseNumeric guarantees all columns have at least one valid row
            case MIN -> column.stream().min(Double::compareTo).get();
            case MAX -> column.stream().max(Double::compareTo).get();
            case SUM -> column.stream().reduce(Double::sum).get();
            case AVERAGE -> movingAverage(column);
        }).map(Object::toString).map(List::of).toList();

        return new Table(newColumnNames, newColumns);
    }

    private static double movingAverage(List<Double> column) {
        var count = 0;
        var average = 0.0;

        for (var value : column) {
            average += (value - average) / ++count;
        }

        return average;
    }

    private static Optional<List<Double>> parseNumeric(List<String> column) {
        var parsedColumn = new ArrayList<Double>();
        for (var value : column) {
            // ignore null values for now
            if (value == null) continue;

            try {
                var parsed = Double.parseDouble(value);
                parsedColumn.add(parsed);
            } catch (NumberFormatException e) {
                // columns with non-numeric values get ignored here
                return Optional.empty();
            }
        }
        // at least one value should be not-null
        if (parsedColumn.isEmpty()) return Optional.empty();
        return Optional.of(parsedColumn);
    }

    public enum Type {
        AVERAGE,
        SUM,
        MIN,
        MAX,
        COUNT
    }
}
