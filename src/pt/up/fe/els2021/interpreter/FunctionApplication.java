package pt.up.fe.els2021.interpreter;

import java.util.List;
import java.util.Map;

import pt.up.fe.els2021.model.Table;
import pt.up.fe.els2021.model.interfaces.TableFunction;

public record FunctionApplication(String source, String target, List<TableFunction> functions) implements Command {

    @Override
    public void apply(Map<String, Table> programState) {
        var table = programState.get(source);
        for (var function : functions) {
            table = function.apply(table);
        }
        programState.put(target, table);
    }

}
