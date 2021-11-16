package pt.up.fe.els2021.commands;

import java.util.List;
import java.util.Map;

import pt.up.fe.els2021.Command;
import pt.up.fe.els2021.Table;
import pt.up.fe.els2021.functions.TableFunction;

public record TableTransformation(String source, String target, List<TableFunction> functions) implements Command {

    @Override
    public void apply(Map<String, Table> programState) throws Exception {
        var table = programState.get(source);
        for (var function : functions) {
            table = function.apply(table);
        }
        programState.put(target, table);
    }

    @Override
    public String toString() {
        if (functions.isEmpty()) {
            return "Transformation { \"" + target + "\" is \"" + source + "\" }";
        }

        var builder = new StringBuilder("Transformation {\n");
        builder.append("  \"").append(target).append("\" is \"").append(source)
                .append("\" after applying ").append(functions.size()).append(" functions:\n");
        for (var function : functions) {
            var strFunction = function.toString();
            builder.append(" -").append(strFunction.indent(2));
        }
        return builder.append("}").toString();
    }
}
