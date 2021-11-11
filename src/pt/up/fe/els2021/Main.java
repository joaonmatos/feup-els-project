package pt.up.fe.els2021;

import java.io.File;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Main {

	public static void main(String[] args) throws Exception {
		if (args.length != 1 && args.length != 2)
			System.exit(1);

		var config = new File(args[0]);
		var jsonMapper = new ObjectMapper();

		var program = jsonMapper.readValue(config, Program.class);
		System.out.println(program);
		program.run();

		if (args.length == 2)
			jsonMapper.writeValue(new File(args[1]), program);
	}
}