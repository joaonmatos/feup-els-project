package pt.up.fe.els2021.interpreter;

import java.util.Map;

import pt.up.fe.els2021.model.Table;
import pt.up.fe.els2021.model.interfaces.TableSource;

public record Import(TableSource source, String target) implements Command {

    @Override
    public void apply(Map<String, Table> programState) {
        programState.put(target, source.getTable());
    }
}
