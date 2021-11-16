package pt.up.fe.els2021;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public record Table(List<String> columnNames,
                    List<List<String>> columns) {

    public int getSize() {
        if (columns.isEmpty())
            return 0;

        return columns.get(0).size();
    }

    public Stream<List<String>> rowStream() {
        var size = getSize();
        if (size == 0)
            return Stream.empty();

        return IntStream.range(0, size).mapToObj(this::getRow).map(Optional::get);
    }

    public Optional<List<String>> getRow(int index) {
        if (index < 0 || index >= getSize())
            return Optional.empty();

        return Optional.of(columns.stream().map(col -> col.get(index)).toList());
    }

    @Override
    public String toString() {
        if (columnNames.isEmpty()) {
            return "Table { Empty }";
        }

        var builder = new StringBuilder("Table {\n");
        builder.append("  ").append(columnNames.size()).append("columns:\n");
        for (var name : columnNames) {
            builder.append(" - ").append(name).append("\n");
        }
        builder.append("  ").append(getSize()).append(" rows\n");
        builder.append("}");
        return builder.toString();
    }

}
