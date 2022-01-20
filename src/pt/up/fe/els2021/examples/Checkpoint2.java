package pt.up.fe.els2021.examples;

import pt.up.fe.els2021.sources.TableSource;

import static pt.up.fe.els2021.internal.DSL.*;

public class Checkpoint2 {
    public static void main(String[] args) throws Exception {
        var vitisReports = Sources.xml("//AreaEstimates/Resources[1]")
                .withFile("**/examples/checkpoint2/*/vitis-report.xml")
                .withInclude(TableSource.Include.FOLDER, "Folder")
                .importTable();

        var jsonFilesPath = "**/examples/checkpoint2/*/decision_tree.json";
        var decisionTreeRoot = Sources.json("")
                .withFile(jsonFilesPath)
                .withInclude(TableSource.Include.FOLDER, "Folder")
                .importTable();
        var decisionTreeParams = Sources.json("/params")
                .withFile(jsonFilesPath)
                .withInclude(TableSource.Include.FOLDER, "Folder")
                .importTable();

        var gprof = Sources.text(
                        "%", "matrix_mulv3_tdtdptd",
                7, 2, " "
                ).withFile("**/examples/checkpoint2/*/gprof.txt")
                .withInclude(TableSource.Include.FOLDER, "Folder")
                .importTable();
        gprof = Functions.select()
                .column("Folder")
                .column("% time")
                .column("name")
                .build().apply(gprof);

        var combined = combine(Combinators.join("Folder"))
                .withTable(vitisReports)
                .withTable(decisionTreeRoot)
                .withTable(decisionTreeParams)
                .withTable(gprof)
                .getResult();

        var result = Functions.move("Folder", 0).apply(combined);

        Exporters.csv("out.csv").exportTable(result);
    }
}
