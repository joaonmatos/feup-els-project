package pt.up.fe.els2021;

import java.util.HashMap;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public record Program(@JsonValue List<Command> commands) implements Runnable {
    @Override
    public void run() {
        var state = new HashMap<String, Table>();
        for (var command : commands) {
            command.apply(state);
        }
    }

    @JsonCreator
    public static Program newProgram(List<Command> commands) {
        return new Program(commands);
    }
}
