package pt.up.fe.els2021;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;

import java.io.File;

public class Main {

    public static void main(String[] args) throws Exception {
        if (args.length != 1)
            System.exit(1);

        var config = new File(args[0]);
        var jsonMapper = JsonMapper.builder()
                .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
                .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS, true).build();

        var program = jsonMapper.readValue(config, Program.class);
        program.run();
    }
}