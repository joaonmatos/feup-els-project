package pt.up.fe.els2021.combinators;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import pt.up.fe.els2021.Table;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "combinator")
@JsonSubTypes({
        @JsonSubTypes.Type(value = InnerJoinCombinator.class, name = "join"),
        @JsonSubTypes.Type(value = MergeCombinator.class, name = "merge")
})
public sealed interface TableCombinator permits InnerJoinCombinator, MergeCombinator {
    Table combine(Table a, Table b);
}
