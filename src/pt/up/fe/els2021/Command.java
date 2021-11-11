package pt.up.fe.els2021;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

import pt.up.fe.els2021.commands.TableExport;
import pt.up.fe.els2021.commands.TableImport;
import pt.up.fe.els2021.commands.TableTransformation;

@JsonTypeInfo(use = Id.NAME, include = As.PROPERTY, property = "command")
@JsonSubTypes({ @Type(value = TableImport.class, name = "import"),
        @Type(value = TableTransformation.class, name = "transform"),
        @Type(value = TableExport.class, name = "export") })
public interface Command {
    void apply(Map<String, Table> programState) throws Exception;
}
