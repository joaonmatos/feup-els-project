package pt.up.fe.els2021.interpreter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pt.up.fe.els2021.model.Table;

public class Program {
    private Map<String, Table> state = new HashMap<>();

    public void run(Command command) {
        command.apply(state);
    }

    public void run(List<Command> commands) {
        for (var command : commands)
            run(command);
    }
}
