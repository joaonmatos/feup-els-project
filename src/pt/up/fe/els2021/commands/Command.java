package pt.up.fe.els2021.commands;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import pt.up.fe.els2021.Table;

import java.util.Map;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "command")
@JsonSubTypes({@JsonSubTypes.Type(value = TableImport.class, name = "import"),
        @JsonSubTypes.Type(value = TableTransformation.class, name = "transform"),
        @JsonSubTypes.Type(value = TableExport.class, name = "export"),
        @JsonSubTypes.Type(value = TableCombination.class, name = "combine")})

public sealed interface Command permits TableCombination, TableExport, TableImport, TableTransformation {
    void apply(Map<String, Table> programState) throws Exception;
}
