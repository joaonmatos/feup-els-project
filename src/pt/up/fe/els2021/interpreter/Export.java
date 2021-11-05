package pt.up.fe.els2021.interpreter;

import java.util.Map;

import pt.up.fe.els2021.model.Table;
import pt.up.fe.els2021.model.interfaces.TableExporter;

public record Export(String source, TableExporter target) implements Command {

    @Override
    public void apply(Map<String, Table> programState) {
        target.exportTable(programState.get(source));
    }

}
