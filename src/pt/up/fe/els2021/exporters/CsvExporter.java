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
        CSVWriter writer;
        try {
            System.out.println("x");
            writer = new CSVWriter(new FileWriter(file));
            writer.writeNext(table.columnNames().toArray(new String[0])); // Write header into csv

            List<List<String>> lines = new ArrayList<>(); 

            // Convert columns to rows :C
            for (int i = 0; i < table.columns().get(0).size(); i++) {
                List<String> aux = new ArrayList<>();
                for (List<String> column : table.columns()) {
                    aux.add(column.get(i));
                }
                lines.add(aux);
            }

            // Write rows to csv
            for (List<String> row : lines) {
                writer.writeNext(row.toArray(new String[0]));
            }
            writer.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
       

    }

}
