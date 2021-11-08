package pt.up.fe.els2021.exporters;

import pt.up.fe.els2021.Table;

public record CsvExporter(String file) implements TableExporter {

    @Override
    public void exportTable(Table table) {
        // TODO Auto-generated method stub

    }

}
