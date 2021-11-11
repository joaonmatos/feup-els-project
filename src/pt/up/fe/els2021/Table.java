package pt.up.fe.els2021;

import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public record Table(List<String> columnNames, List<List<String>> columns) {
    public int getSize() {
        if (columns.isEmpty())
            return 0;

        return columns.get(0).size();
    }

    public Stream<List<String>> rowStream() {
        var size = getSize();
        if (size == 0)
            return Stream.empty();

        return IntStream.range(0, size).mapToObj(this::getRow);
    }

    public List<String> getRow(int index) {
        if (index < 0 || index >= getSize())
            return null;

        return columns.stream().map(col -> col.get(index)).toList();
    }
}
