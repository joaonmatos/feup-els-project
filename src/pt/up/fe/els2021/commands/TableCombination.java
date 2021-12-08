package pt.up.fe.els2021.commands;

import pt.up.fe.els2021.Table;
import pt.up.fe.els2021.combinators.TableCombinator;

import java.util.List;
import java.util.Map;

public record TableCombination(List<String> sources, String target,
                               TableCombinator combinator) implements Command {

    @Override
    public void apply(Map<String, Table> programState) throws Exception {
        var table = sources.stream().map(programState::get).reduce(combinator::combine);
        programState.put(target, table.get());
    }
}
