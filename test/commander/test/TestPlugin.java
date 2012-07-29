package commander.test;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.digiplex.bukkitplugin.commander.CommanderPlugin;
import org.digiplex.bukkitplugin.commander.scripting.BadScriptException;
import org.digiplex.bukkitplugin.commander.scripting.ScriptBlock;
import org.digiplex.bukkitplugin.commander.scripting.ScriptEnvironment;
import org.digiplex.bukkitplugin.commander.scripting.ScriptLine;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.JUnitCore;
import org.junit.runners.JUnit4;

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
		LOG.info("----- Done "+testname.getMethodName()+" -----");
	}
	
	@AfterClass public static void tearDownClass() throws Exception {
		plugin.onDisable();
	}
	
	//////////////////////////////////////////////////////////////////////////
	
	@Test public void simpleLine() throws Exception {
		String command = "Test Line 1";
		
		ScriptLine sl = ScriptLine.parseScriptLine(command);
		sl.execute(environment);
	}
	
	@Test public void multilineScript() throws Exception {
		String[] commands = new String[] {
				"Test Line 1",
				"Test Command 2",
				"daytime!",
				"Hello World!",
		};
		
		ScriptBlock sb = new ScriptBlock(commands);
		sb.execute(environment);
	}
	
	@Test public void detectUnevenParens() {
		String[] commands = new String[] {
				"Test Line 1",
				"{", 
				"Test Line 2",
				"Test Line 3",
				"Test Line 4",
		};
		ScriptBlock sb;
		
		try {
			sb = new ScriptBlock(commands);
			sb.execute(environment);
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
			sb = new ScriptBlock(commands);
			sb.execute(environment);
			fail("Did not detect uneven close paren!");
		} catch (BadScriptException e) {
			assertNotNull(e);
			LOG.warning(e.getMessage());
		}
	}
}
