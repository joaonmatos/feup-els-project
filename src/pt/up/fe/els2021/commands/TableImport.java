package pt.up.fe.els2021.commands;

import pt.up.fe.els2021.Table;
import pt.up.fe.els2021.sources.TableSource;

import java.util.Map;

public record TableImport(TableSource source, String target) implements Command {

    @Override
    public void apply(Map<String, Table> programState) throws Exception {
        programState.put(target, source.importTable());
    }
}
