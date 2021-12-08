package pt.up.fe.els2021.commands;

import pt.up.fe.els2021.Table;
import pt.up.fe.els2021.functions.TableFunction;

import java.util.List;
import java.util.Map;

public record TableTransformation(String source, String target,
                                  List<TableFunction> functions) implements Command {

    @Override
    public void apply(Map<String, Table> programState) throws Exception {
        var table = programState.get(source);
        for (var function : functions) {
            table = function.apply(table);
        }
        programState.put(target, table);
    }
}
