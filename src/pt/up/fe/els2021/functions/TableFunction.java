package pt.up.fe.els2021.functions;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import pt.up.fe.els2021.Table;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "function")
@JsonSubTypes({
        @JsonSubTypes.Type(value = ExcludeFunction.class, name = "exclude"),
        @JsonSubTypes.Type(value = OrderByFunction.class, name = "sort"),
        @JsonSubTypes.Type(value = RenameFunction.class, name = "rename"),
        @JsonSubTypes.Type(value = SelectFunction.class, name = "select"),
        @JsonSubTypes.Type(value = TrimFunction.class, name = "trim")
})
public sealed interface TableFunction permits ExcludeFunction, OrderByFunction, RenameFunction, SelectFunction, TrimFunction {
    Table apply(Table table) throws Exception;

    sealed abstract class Builder permits ExcludeFunction.Builder, OrderByFunction.Builder, SelectFunction.Builder, TrimFunction.Builder {
        public abstract TableFunction build();
    }
}
