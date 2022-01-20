package pt.up.fe.els2021;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class Main {

    public static void main(String[] args) throws Exception {
        if (args.length != 1)
            System.exit(1);

        var config = new File(args[0]);
        var reader = new FileReader(config);

        var parser = new TablyParser();
        var program = parser.parse(reader);
        program.run();
    }
}