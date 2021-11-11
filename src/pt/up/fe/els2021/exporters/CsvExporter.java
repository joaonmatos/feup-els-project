package pt.up.fe.els2021.exporters;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.opencsv.CSVWriter;

import pt.up.fe.els2021.Table;

public record CsvExporter(String file) implements TableExporter {

    @Override
    public void exportTable(Table table) {
        try {
            System.out.println("x");
            var writer = new CSVWriter(new FileWriter(file));
            writer.writeNext(table.columnNames().toArray(new String[0])); // Write header into csv

            table.rowStream().forEachOrdered(row -> writer.writeNext((row.toArray(new String[0]))));

            writer.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}
