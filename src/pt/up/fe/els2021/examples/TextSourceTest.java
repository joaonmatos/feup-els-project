package pt.up.fe.els2021.examples;

import pt.up.fe.els2021.internal.DSL.Combinators;
import pt.up.fe.els2021.internal.DSL.Exporters;
import pt.up.fe.els2021.internal.DSL.Functions;
import pt.up.fe.els2021.internal.DSL.Sources;
import pt.up.fe.els2021.sources.TableSource;

import static pt.up.fe.els2021.internal.DSL.combine;

public class TextSourceTest {

    public static void main(String[] args) throws Exception {

        var gprof = Sources.text(
                        "%", "name", 7, " "
                ).withFile("**/examples/checkpoint2/*/gprof.txt")
                .withInclude(TableSource.Include.FOLDER, "Folder")
                .importTable();
        gprof = Functions.select()
                .column("% time")
                .column("name")
                .build().apply(gprof);

        var combined = combine(Combinators.join("Folder"))
                .withTable(gprof)
                .getResult();

        //var result = Functions.move("Folder", 0).apply(combined);

        Exporters.csv("out1.csv").exportTable(combined);

    }
}
