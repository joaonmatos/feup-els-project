package pt.up.fe.els2021.commands;

import java.util.Map;

import pt.up.fe.els2021.Command;
import pt.up.fe.els2021.Table;
import pt.up.fe.els2021.sources.TableSource;

public record TableImport(TableSource source, String target) implements Command {

    @Override
    public void apply(Map<String, Table> programState) {
        programState.put(target, source.getTable());
    }
}
