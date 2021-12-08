package pt.up.fe.els2021.sources;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.databind.ObjectMapper;
import pt.up.fe.els2021.Table;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class JsonSource extends TableSource {
    private final String elementPath;

    @JsonCreator
    public JsonSource(
            @JsonProperty("includes") Map<Include, String> includes,
            @JsonProperty("files") List<String> files,
            @JsonProperty("path") String elementPath
    ) {
        super(includes, files);
        this.elementPath = elementPath;
    }


    @Override
    protected Table getFileTable(String fileContent) throws Exception {
        var mapper = new ObjectMapper();
        var pointer = JsonPointer.compile(elementPath);
        var node = mapper.readTree(fileContent).at(pointer);

        var columnNames = new ArrayList<String>();
        var columns = new ArrayList<List<String>>();

        var fields = node.fields();
        while (fields.hasNext()) {
            var field = fields.next();
            var key = field.getKey();
            var value = field.getValue();
            if (value.isContainerNode()) continue;

            var valueAsString = switch (value.getNodeType()) {
                case STRING -> value.asText();
                case NUMBER -> value.canConvertToExactIntegral() ? "" + value.asLong() : "" + value.asDouble();
                case BOOLEAN -> value.asBoolean() ? "true" : "false";
                case ARRAY, BINARY, MISSING, NULL, OBJECT, POJO -> null;
            };

            if (valueAsString != null) {
                columnNames.add(key);
                columns.add(List.of(valueAsString));
            }
        }

        return new Table(columnNames, columns);
    }

    public static final class Builder extends TableSource.Builder {

        private final String elementPath;

        public Builder(String elementPath) {
            this.elementPath = elementPath;
        }

        @Override
        public TableSource build() {
            return new JsonSource(includes, files, elementPath);
        }
    }
}
