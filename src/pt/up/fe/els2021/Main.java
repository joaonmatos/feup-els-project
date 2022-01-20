package pt.up.fe.els2021;

import java.io.File;
import java.io.FileReader;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws Exception {
        if (args.length > 1)
            System.exit(1);

        if (args.length == 1) {
            var config = new File(args[0]);
            var reader = new FileReader(config);

            var parser = new TablyParser();
            var program = parser.parse(reader);
            program.run();
        }

        var input = new Scanner(System.in);
        var currentCommand = new StringBuilder();
        var interpreter = new Interpreter();
        System.out.print("Tably Interpreter for ELS2021\n > ");
        while (input.hasNextLine()) {
            var line = input.nextLine();
            var complete = true;
            if (line.equals(".exit")) {
                System.exit(0);
            }
            if (line.endsWith("\\")) {
                complete = false;
                line = line.substring(0, line.length() - 1);
            }
            currentCommand.append(line);
            if (complete) {
                try {
                    interpreter.interpretCommand(currentCommand.toString());
                    System.out.println("OK");
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                } finally {
                    currentCommand = new StringBuilder();
                }
            }
            System.out.print(" > ");
        }
        System.exit(0);
    }
}