package pt.up.fe.els2021;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public final class Program {

    private final List<Command> commands;

    @JsonCreator
    Program(List<Command> commands) {
        this.commands = commands;
    }

    @JsonValue
    public List<Command> commands() {
        return commands;
    }

    public void run() throws Exception {
        var state = new HashMap<String, Table>();
        for (var command : commands) {
            command.apply(state);
        }
    }

    @Override
    public String toString() {
        if (commands.isEmpty()) {
            return "Program { Empty }";
        }
        var builder = new StringBuilder("Program {\n");
        builder.append("  ").append(commands.size()).append(" commands:\n");
        for (var command : commands) {
            var strCommand = command.toString();
            builder.append(" -").append(strCommand.indent(2));
        }
        return builder.append("}").toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null)
            return false;
        if (o instanceof Program p) {
            return Objects.equals(commands, p.commands);
        } else
            return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(commands);
    }

}
