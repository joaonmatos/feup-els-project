package pt.up.fe.els2021.exporters;

import com.opencsv.CSVWriter;
import pt.up.fe.els2021.Table;

import java.io.FileWriter;

public record CsvExporter(String file) implements TableExporter {

    @Override
    public void exportTable(Table table) throws Exception {
        var writer = new CSVWriter(new FileWriter(file));
        writer.writeNext(table.columnNames().toArray(new String[0])); // Write header into csv

        table.rowStream().forEachOrdered(row -> writer.writeNext((row.toArray(new String[0]))));

        writer.close();
    }
}
