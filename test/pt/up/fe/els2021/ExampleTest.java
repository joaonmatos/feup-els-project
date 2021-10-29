package pt.up.fe.els2021;

import static org.junit.Assert.*;

import org.junit.Test;
import pt.up.fe.specs.util.SpecsIo;


public class ExampleTest {
	
    @Test
    public void exampleTest() {
		
		// Reads a resource and tests contents
		assertEquals(SpecsIo.getResource("pt/up/fe/els2021/resource.txt"), "Expected text");
    }
}