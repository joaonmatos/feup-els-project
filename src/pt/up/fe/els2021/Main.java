package pt.up.fe.els2021;

import java.util.ArrayList;
import java.util.List;

import pt.up.fe.els2021.interpreter.Command;
import pt.up.fe.els2021.interpreter.Program;

public class Main {

	public static void main(String[] args) {
		// System.out.println("Hello");

		var p = new Program();
		List<Command> commands = new ArrayList<>();
		p.run(commands);
	}
}