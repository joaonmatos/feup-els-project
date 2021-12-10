package pt.up.fe.els2021.sources;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import pt.up.fe.els2021.Table;
import pt.up.fe.els2021.combinators.MergeCombinator;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "adapter")
@JsonSubTypes({
        @JsonSubTypes.Type(value = JsonSource.class, name = "json"),
        @JsonSubTypes.Type(value = XmlSource.class, name = "xml"),
        @JsonSubTypes.Type(value = TextSource.class, name = "text")
})
public sealed abstract class TableSource permits JsonSource, XmlSource, TextSource {
    private final Map<Include, String> includes;
    private final List<String> files;

    protected TableSource(Map<Include, String> includes, List<String> files) {
        this.includes = includes != null ? includes : Collections.emptyMap();
        this.files = files;
    }

    public Table importTable() throws Exception {
        var files = getFiles();
        var tables = new ArrayList<Table>();
        for (var file : files) {
            var content = Files.readString(Path.of(file), StandardCharsets.UTF_8);
            var table = getFileTable(content);
            Table addIncludes = addIncludes(includesForFile(file), table);
            tables.add(addIncludes);
        }

        var combinator = new MergeCombinator();
        return tables.stream().reduce(combinator::combine).get();
    }

    abstract protected Table getFileTable(String fileContent) throws Exception;

    private List<String> getFiles() throws Exception {
        var currentWorkingDir = Paths.get("").toAbsolutePath();
        var paths = new ArrayList<String>();
        for (var file : files) {
            var globMatcher = FileSystems.getDefault().getPathMatcher("glob:" + file);

            Files.walkFileTree(currentWorkingDir, new SimpleFileVisitor<>() {

                @Override
                public FileVisitResult visitFile(Path path,
                                                 BasicFileAttributes attrs) {
                    if (globMatcher.matches(path)) {
                        paths.add(path.toAbsolutePath().toString());
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) {
                    return FileVisitResult.CONTINUE;
                }
            });
        }
        return paths.stream().distinct().toList();
    }

    Map<String, String> includesForFile(String file) {
        var map = new HashMap<String, String>();
        var path = Paths.get(file);
        var fileName = path.getFileName().toString();
        var folder = path.getParent().getFileName().toString();

        if (includes.containsKey(Include.FILENAME)) {
            map.put(includes.get(Include.FILENAME), fileName);
        }
        if (includes.containsKey(Include.FOLDER)) {
            map.put(includes.get(Include.FOLDER), folder);
        }
        if (includes.containsKey(Include.PATH)) {
            map.put(includes.get(Include.PATH), file);
        }

        return map;
    }

    private Table addIncludes(Map<String, String> includes, Table table) {
        var rowCount = table.rowCount();
        var newColumnNames = new ArrayList<>(table.columnNames());
        var newColumns = new ArrayList<>(table.columns());
        for (var entry : includes.entrySet()) {
            var columnName = entry.getKey();
            var columnValue = entry.getValue();
            newColumnNames.add(columnName);
            var column = new ArrayList<String>();
            for (var ignored = 0; ignored < rowCount; ignored++)
                column.add(columnValue);
            newColumns.add(column);
        }
        return new Table(newColumnNames, newColumns);
    }

    public enum Include {
        FOLDER,
        FILENAME,
        PATH
    }

    public static sealed abstract class Builder permits JsonSource.Builder, XmlSource.Builder, TextSource.Builder {
        protected final List<String> files = new ArrayList<>();
        protected final Map<Include, String> includes = new HashMap<>();

        public abstract TableSource build();

        public Builder withFile(String file) {
            files.add(file);
            return this;
        }

        public Builder withInclude(Include include, String columnName) {
            includes.put(include, columnName);
            return this;
        }

        public Table importTable() throws Exception {
            return build().importTable();
        }
    }

}
