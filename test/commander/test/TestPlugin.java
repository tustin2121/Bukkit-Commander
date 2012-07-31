package commander.test;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.digiplex.bukkitplugin.commander.CommanderPlugin;
import org.digiplex.bukkitplugin.commander.scripting.BadScriptException;
import org.digiplex.bukkitplugin.commander.scripting.Executable;
import org.digiplex.bukkitplugin.commander.scripting.ScriptBlock;
import org.digiplex.bukkitplugin.commander.scripting.ScriptEnvironment;
import org.digiplex.bukkitplugin.commander.scripting.ScriptLine;
import org.digiplex.bukkitplugin.commander.scripting.ScriptParser;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import commander.test.placeholders.TestPlayer;
import commander.test.placeholders.TestServer;

import static junit.framework.Assert.*;

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
				"[if @hello = hi]",
				"    This line shouldn't run",
				"[if @i = 2]",
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
}
