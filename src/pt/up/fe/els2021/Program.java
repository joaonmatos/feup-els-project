package pt.up.fe.els2021;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public final class Program {

    private List<Command> commands;

    @JsonCreator
    Program(List<Command> commands) {
        this.commands = commands;
    }

    @JsonValue
    public List<Command> getCommands() {
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
        return "Program[commands=" + commands + "]";
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
