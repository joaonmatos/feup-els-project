package pt.up.fe.els2021.commands;

import pt.up.fe.els2021.Table;
import pt.up.fe.els2021.exporters.TableExporter;

import java.util.Map;

public record TableExport(String source, TableExporter target) implements Command {

    @Override
    public void apply(Map<String, Table> programState) throws Exception {
        target.exportTable(programState.get(source));
    }

}
