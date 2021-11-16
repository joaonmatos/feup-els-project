package pt.up.fe.els2021.commands;

import java.util.Map;

import pt.up.fe.els2021.Command;
import pt.up.fe.els2021.Table;
import pt.up.fe.els2021.exporters.TableExporter;

public record TableExport(String source, TableExporter target) implements Command {

    @Override
    public void apply(Map<String, Table> programState) throws Exception {
        target.exportTable(programState.get(source));
    }

    @Override
    public String toString() {
        return "Export {\n" +
                "  Exported table: \"" + source + "\"\n" +
                "  Exporter:" + target.toString().indent(2) +
                "}";
    }
}
