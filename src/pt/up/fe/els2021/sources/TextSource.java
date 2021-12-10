package pt.up.fe.els2021.sources;

import java.io.*;
import java.util.*;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import pt.up.fe.els2021.Table;

public final class TextSource extends TableSource {

    private final String startToken;
    private final String endToken;
    private final int height;
    private final int width;
    private final int headersize;
    // private final ArrayList<String> columns; includes
    private final String separator;

    @JsonCreator
    public TextSource(
            @JsonProperty("includes") Map<Include, String> includes,
            @JsonProperty("files") List<String> files,
            @JsonProperty("startToken") String beg,
            @JsonProperty("endToken") String end,
            @JsonProperty("height") int height,
            @JsonProperty("width") int width,
            @JsonProperty("headersize") int headersize,
            @JsonProperty("separator") String separator) {

        super(includes, files);

        this.startToken = beg;
        this.endToken = end;
        this.height = height;
        this.width = width;
        this.headersize = headersize;
        this.separator = separator;
    }

    @Override
    protected Table getFileTable(String fileContent) throws Exception {
        return parser(fileContent);
    }

    private Table parser(String fileContent) throws Exception {
        try (var inputString = new StringReader(fileContent); var reader = new BufferedReader(inputString)) {
            var iterator = reader.lines().iterator();
            var file = new ArrayList<List<String>>();
            var firstLine = false;
            var ending = false;

            while (iterator.hasNext()) {
                var line = iterator.next();

                if (!firstLine) {
                    if (line.trim().startsWith(this.startToken)) {
                        firstLine = true;
                        file.add(Arrays.asList(line.trim().replaceAll(" +", " ").split(separator)));
                    }
                } else {
                    ending = line.endsWith(this.endToken);
                    file.add(Arrays.asList(line.trim().replaceAll(" +", " ").split(separator)));

                    if (ending) {
                        if (checkTable(file, height, width, headersize)) {
                            return getColumns(file, headersize);
                        } else {
                            firstLine = false;
                        }
                    }
                }
            }
            throw new Exception("TextSource: did not find end of table");
        }
    }

    public boolean checkTable(ArrayList<List<String>> table, int height, int width, int header) {
        if (table.size() < header + 1 || table.size() > height + header) {
            return false;
        }

        for (var line : table) {
            if (line.size() > width) {
                return false;
            }
        }
        return true;
    }

    public Table getColumns(ArrayList<List<String>> table, int headerSize) {

        var fileHeaders = table.subList(0, headerSize);
        var header = new ArrayList<>(fileHeaders.get(0));

        // merge header
        for (int i = 1; i < headerSize; i++) {
            int tempHeaderSize = header.size();
            for (int j = 0; j < fileHeaders.get(i).size(); j++) {
                if (tempHeaderSize > j) {
                    String temp = header.get(j);
                    header.set(j, temp + " " + fileHeaders.get(i).get(j));
                } else {
                    header.add(fileHeaders.get(i).get(j));
                }
            }
        }

        ArrayList<List<String>> newTable = new ArrayList<>() {
            {
                add(header);
            }
        };

        newTable.addAll(table.subList(2, table.size()));

        var flipped = flipTable(newTable.subList(1, newTable.size()));

        return new Table(newTable.get(0), flipped);

    }

    public List<List<String>> flipTable(List<List<String>> list) {

        var newTable = new ArrayList<List<String>>();
        for (int i = 0; i < list.get(0).size(); i++) {
            newTable.add(new ArrayList<>());
        }

        for (int i = 0; i < list.size(); i++) {
            for (int j = 0; j < list.get(i).size(); j++) {
                newTable.get(j).add(list.get(i).get(j));
            }
        }

        return newTable;
    }

    public static final class Builder extends TableSource.Builder {
        private final String startToken;
        private final String endToken;
        private final int height;
        private final int width;
        private final int headersize;
        // private final ArrayList<String> columns;
        private final String separator;

        public Builder(String beg, String end, int height, int width, int headersize, String separator) {
            this.startToken = beg;
            this.endToken = end;
            this.height = height;
            this.width = width;
            this.headersize = headersize;
            // this.columns = columns;
            this.separator = separator;
        }

        @Override
        public TableSource build() {
            return new TextSource(includes, files, startToken, endToken, height, width, headersize, separator);
        }
    }

}
