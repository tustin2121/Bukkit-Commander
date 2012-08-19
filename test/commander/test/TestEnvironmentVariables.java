package commander.test;

import static org.junit.Assert.*;

import org.digiplex.bukkitplugin.commander.scripting.Executable;
import org.digiplex.bukkitplugin.commander.scripting.ScriptParser;
import org.junit.Test;

public class TestEnvironmentVariables extends TestCase {
	
	@Test public void function_random() throws Exception {
		String[] commands = new String[] {
				"@0 := $(function.random)", //random number in range [0, 100)
				"@1 := $(function.random 5)", //random number in the range [0, 5)
				"@2 := $(function.random 20 30)", //random number in the range [20, 30)
		};
		
		Executable sl = ScriptParser.parseScript(commands);
		
		int v;
		for (int i = 0; i < 100; i++) { //statistically test this
			sl.execute(environment);
			
			v = Integer.parseInt((String) environment.getVariableValue("0"));
			assertTrue("0 argument function fail!", (v >= 0 && v < 100));
			
			v = Integer.parseInt((String) environment.getVariableValue("1"));
			assertTrue("1 argument function fail!", (v >= 0 && v < 5));
			
			v = Integer.parseInt((String) environment.getVariableValue("2"));
			assertTrue("2 argument function fail!", (v >= 20 && v < 30));
		}
	}
	
	
}
