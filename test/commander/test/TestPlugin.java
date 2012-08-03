package commander.test;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.digiplex.bukkitplugin.commander.CommanderPlugin;
import org.digiplex.bukkitplugin.commander.scripting.Executable;
import org.digiplex.bukkitplugin.commander.scripting.ScriptEnvironment;
import org.digiplex.bukkitplugin.commander.scripting.ScriptParser;
import org.digiplex.bukkitplugin.commander.scripting.exceptions.BadScriptException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import commander.test.placeholders.TestPlayer;
import commander.test.placeholders.TestServer;

public class TestPlugin {
	private static final Logger LOG = Logger.getLogger("TESTPLUGIN");
	
	public static CommanderPlugin plugin;
	public static TestServer server;
	
	public ScriptEnvironment environment;
	
	@Rule public TestName testname = new TestName();
	
	@BeforeClass public static void setUpClass() throws Exception {
		System.out.println(System.getProperty("user.dir"));
		server = new TestServer();
		Bukkit.setServer(server);
		
		plugin = new CommanderPlugin();
		server.getPluginManager().enablePlugin(plugin);
	}
	
	@Before public void setUp() throws Exception {
		environment = new ScriptEnvironment(); {
			environment.setServer(server);
			environment.setCommandSender(new TestPlayer(server));
		}
		LOG.info("------ Starting "+testname.getMethodName() +" ------");
	}
	
	@After public void tearDown() throws Exception {
		server.clearCommands();
		LOG.info("----- Done "+testname.getMethodName()+" -----");
	}
	
	@AfterClass public static void tearDownClass() throws Exception {
		plugin.onDisable();
	}
	
	//////////////////////////////////////////////////////////////////////////
	
	@Test public void simpleLine() throws Exception {
		String command = "Hello World!";
		
		Executable sl = ScriptParser.parseScript(command);
		sl.execute(environment);
		
		assertTrue(server.checkCommands(command));
	}
	
	@Test public void multilineScript() throws Exception {
		String[] commands = new String[] {
				"Test Line 1  \n",
				"Test Command 2  ",
				" ec bc daytime!  ",
				"",
				"Hello World! \n",
		};
		
		Executable sl = ScriptParser.parseScript(commands);
		sl.execute(environment);
		
		assertTrue(server.checkCommands("Test Line 1", "Test Command 2", "ec bc daytime!", "Hello World!"));
	}
	
	@Test public void detectUnevenParens() {
		String[] commands = new String[] {
				"Test Line 1",
				"{", 
				"Test Line 2",
				"Test Line 3",
				"Test Line 4",
		};
		Executable sl;
		
		try {
			sl = ScriptParser.parseScript(commands);
			sl.execute(environment);
			fail("Did not detect uneven open paren!");
		} catch (BadScriptException e) {
			assertNotNull(e);
			LOG.warning(e.getMessage());
		}
		
		commands = new String[] {
				"Test Line 1",
				"Test Line 2",
				"Test Line 3",
				"}",
				"Test Line 4",
		};
		
		try {
			sl = ScriptParser.parseScript(commands);
			sl.execute(environment);
			fail("Did not detect uneven close paren!");
		} catch (BadScriptException e) {
			assertNotNull(e);
			LOG.warning(e.getMessage());
		}
	}
	
	@Test public void innerBraceScript() throws Exception {
		String[] commands = new String[] {
				"Test Line 1",
				"Test Line 2",
				"{",
				"    Inner Block 1",
				"    {",
				"        Inner-inner Block 1",
				"        Inner-inner Block 2",
				"    }",
				"    Inner Block 2",
				"}",
				"Test Line 3",
				"Test Line 4",
		};
		
		Executable sl = ScriptParser.parseScript(commands);
		sl.execute(environment);
		
		assertTrue(server.checkCommands(commands[0], commands[1], 
				commands[3].trim(), commands[5].trim(),
				commands[6].trim(), commands[8].trim(),
				commands[10], commands[11]));
	}
	
	@Test public void escapeCharacters() throws Exception {
		String command = "Esc\\aping \\characters \\@odds with thing i\\$s f\\un!";
		
		Executable sl = ScriptParser.parseScript(command);
		sl.execute(environment);
		
		assertTrue(server.checkCommands("Escaping characters @odds with thing i$s fun!"));
	}
	
	@Test public void variableReplacement() throws Exception {
		environment.setVariableValue("t1", "hello world");
		environment.setVariableValue("hope", "change");
		
		String command = "Variable @t1 Testing I'm @{hope}ing works";
		
		Executable sl = ScriptParser.parseScript(command);
		sl.execute(environment);
		
		assertTrue(server.checkCommands("Variable hello world Testing I'm changeing works"));
	}
	
	@Test public void varAssignmentAndScope() throws Exception {
		String[] commands = new String[] {
				"@vo = hello",
				"Testing Var @{vo}",
				"{",
				"    @qu = world",
				"    @vo = buddy",
				"    Testing Vars @qu @vo",
				"}",
				"Testing Vars @qu, @vo",
				"Test Line 4",
		};
		
		Executable sl = ScriptParser.parseScript(commands);
		sl.execute(environment);
		
		assertTrue(server.checkCommands("Testing Var hello", "Testing Vars world buddy", "Testing Vars \u00D8, buddy", "Test Line 4"));
	}
	
	@Test public void ifConstruct() throws Exception {
		environment.setVariableValue("hello", "world");
		environment.setVariableValue("i", "1");
		
		String[] commands = new String[] {
				"[if @hello = world]",
				"{",
				"    Good If Hello world!",
				"}",
				"Test Line 12",
				"[if @hello = @i]",
				"    This line shouldn't run",
				"[!if @i = 1]",
				"{",
				"    This line also shouldn't run",
				"}",
				"[if @i = 1]",
				"    But this line should",
				"Test Line 42"
		};
		
		Executable sl = ScriptParser.parseScript(commands);
		sl.execute(environment);
		
		assertTrue(server.checkCommands("Good If Hello world!", "Test Line 12", "But this line should", "Test Line 42"));
	}
	
	@Test public void ifElseConstruct() throws Exception {
		environment.setVariableValue("hello", "hi");
		environment.setVariableValue("i", "1");
		
		String[] commands = new String[] {
				"[if @hello = world]",
				"{",
				"    This command should not run",
				"}",
				"[else]", //else test
				"    But this command should run!",
				"[!if @i = 1]",
				"    This line also shouldn't run",
				"[else if @i = 1]",
				"{", //else if test
				"    But this line should",
				"    As should this line",
				"}",
				"[else]",
				"    One last no run",
				"",
				"[if @i = 13]", //chaining test
				"    No Run 1",
				"[else if @i = 14]",
				"    No Run 2",
				"[else if @i = 20]",
				"    No Run 3",
				"[else if @i = 25]",
				"    No Run 4",
				"[else]",
				"    Yes Run 1",
				"Test Line 196.2"
		};
		
		Executable sl = ScriptParser.parseScript(commands);
		sl.execute(environment);
		
		assertTrue(server.checkCommands("But this command should run!", "But this line should", "As should this line", "Yes Run 1", "Test Line 196.2"));
	}
	
	
	@Test public void permissionConstruct() throws Exception {
		String[] commands = new String[] {
				"[has commander.test1]", //hardcoded in TestPlayer to true
				"{",
				"    This command should run",
				"}",
				"",
				"[has commander.test2]", //false 
				"    This line does not run",
				"[else has commander.test1]", //true, test else
				"    But this line should",
				"[else]",
				"    One last no run",
				"",
				"Test Line 196.5"
		};
		
		Executable sl = ScriptParser.parseScript(commands);
		sl.execute(environment);
		
		assertTrue(server.checkCommands("This command should run", "But this line should", "Test Line 196.5"));
	}
	
	@Test public void comparisonCondition() throws Exception {
		environment.setVariableValue("x", 1);
		
		String[] commands = new String[] {
				"[if @x > 0] {",
				"    X is one here",
				"}",
				"@x--",
				"[if @x > 0]",
				"    X is now zero and cond false",
				"[if @x <= 0]",
				"    This will run",
				"[!if @x = 0]",
				"    This will not",
				"[else if @x < 0]",
				"    Also won't run",
				"[else if @x >= 0]",
				"    Runs",
				"Test Line 196.5"
		};
		
		Executable sl = ScriptParser.parseScript(commands);
		sl.execute(environment);
		
		assertTrue(server.checkCommands("X is one here", "This will run", "Runs", "Test Line 196.5"));
	}
	
	@Test public void checkCondition() throws Exception {
		environment.setVariableValue("x", true);
		environment.setVariableValue("y", false);
		environment.setVariableValue("z", null);
		environment.setVariableValue("w", "Hello");
		
		String[] commands = new String[] {
				"[if @x] {",
				"    True statement",
				"}",
				"[if @y]",
				"    false statement",
				"[if @z]",
				"    null statement",
				"[else if @w]",
				"    Object statement",
				"[!if @y]",
				"    Not false",
				"Test Line 295"
		};
		
		Executable sl = ScriptParser.parseScript(commands);
		sl.execute(environment);
		
		assertTrue(server.checkCommands("True statement", "Object statement", "Not false", "Test Line 295"));
	}
	
	@Test public void forIntLoop() throws Exception {
		String[] commands = new String[] {
				"[loop @i = 0 to 5] {", //inclusive on both ends
				"    This is loop @i",
				"    @i = 42", //the loop construct does not care if you step on its variable, it simply overwrites it on next loop
				"}",
				"[loop @i = 0 to 6 step 2]",
				"    Step 2 Loop @i",
				"[loop @i = 0 to 3 step 2]",
				"    Step 2 odd Loop @i",
				"Test Line 42"
		};
		
		Executable sl = ScriptParser.parseScript(commands);
		sl.execute(environment);
		
		assertTrue(server.checkCommands("This is loop 0", "This is loop 1", "This is loop 2", "This is loop 3", "This is loop 4", "This is loop 5",
				"Step 2 Loop 0", "Step 2 Loop 2", "Step 2 Loop 4", "Step 2 Loop 6",
				"Step 2 odd Loop 0", "Step 2 odd Loop 2",
				"Test Line 42"));
	}
	
	@Test public void whileLoop() throws Exception {
		environment.setVariableValue("i", "0");
		
		String[] commands = new String[] {
				"[while @i < 5] {", //if statement, except it loops
				"    This is loop @i",
				"}",
				"Test Line 42"
		};
		
		Executable sl = ScriptParser.parseScript(commands);
		sl.execute(environment);
		
		assertTrue(server.checkCommands("This is loop 0", "This is loop 1", "This is loop 2", "This is loop 3", "This is loop 4", 
				"Test Line 42"));
	}
	
	@Test public void invalidLoopFormat() {
		String[] commands = new String[] {
				"[loop @hello = w to z]",
				"    This loop is invalid",
		};
		Executable sl;
		
		try {
			sl = ScriptParser.parseScript(commands);
			sl.execute(environment);
			fail("Parser somehow parsed this!");
		} catch (BadScriptException e) {
			assertNotNull(e);
			LOG.warning(e.getMessage());
		}
	}
	
	/**
	 * [switch @var]
	 * {
	 * 	 [case 1] {
	 *     case 1
	 *   }
	 *   [case 2] {
	 *     case 2
	 *   }
	 *   [else] {
	 *     case else
	 *   }
	 * }
	 * 
	 * [switch @num]
	 * {
	 *   [case 1-2] {
	 *     case 1 or 2
	 *   }
	 *   [case 3-6] {
	 *     case 3, 4, 5, or 6
	 *   }
	 *   [case > 7] {
	 *     cases above 7
	 *   }
	 *   [case < -3] {
	 *     cases below -3
	 *   }
	 *   [else] {
	 *     cases -1, -2, -3, and 7
	 *   }
	 * }
	 * 
	 * 
	 * @throws Exception
	 */
	@Test public void switchCase() throws Exception {
		fail("Not yet implemented");
	}
}
