package pt.up.fe.els2021;

import java.io.StringReader;
import java.util.HashMap;

public class Interpreter {
    private HashMap<String, Table> state = new HashMap<>();

    public void interpretCommand(String command) throws Exception {
        var stateCopy = new HashMap<>(state);
        var parser = new TablyParser();
        parser.setExistingVariables(stateCopy.keySet());
        var program = parser.parse(new StringReader(command));
        program.run(stateCopy);
        state = stateCopy;
    }
}
