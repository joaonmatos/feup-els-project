package pt.up.fe.els2021.functions;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

import pt.up.fe.els2021.Table;

@JsonTypeInfo(use = Id.NAME, include = As.PROPERTY, property = "function")
@JsonSubTypes({ @Type(value = RenameFunction.class, name = "rename"),
        @Type(value = SelectFunction.class, name = "select") })
public interface TableFunction {
    Table apply(Table table) throws Exception;
}
