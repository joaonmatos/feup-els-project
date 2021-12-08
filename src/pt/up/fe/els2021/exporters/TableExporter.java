package pt.up.fe.els2021.exporters;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import pt.up.fe.els2021.Table;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({@JsonSubTypes.Type(value = CsvExporter.class, name = "csv")})
public sealed interface TableExporter permits CsvExporter {
    void exportTable(Table table) throws Exception;
}
