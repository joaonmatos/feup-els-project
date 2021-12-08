package pt.up.fe.els2021;

import com.fasterxml.jackson.annotation.JsonCreator;
import pt.up.fe.els2021.commands.Command;

import java.util.HashMap;
import java.util.List;

public final class Program {
    private final List<Command> commands;

    @JsonCreator
    Program(List<Command> commands) {
        this.commands = commands;
    }

    public void run() throws Exception {
        var state = new HashMap<String, Table>();
        for (var command : commands) {
            command.apply(state);
        }
    }


}
