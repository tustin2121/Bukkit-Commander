package commander.test;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.digiplex.bukkitplugin.commander.CommanderPlugin;
import org.digiplex.bukkitplugin.commander.scripting.ScriptEnvironment;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.rules.TestName;

import commander.test.placeholders.TestPlayer;
import commander.test.placeholders.TestServer;

/**
 * Superclass for all test classes in the Test Suite. :)
 * @author Tim
 *
 */
public class TestCase {
	protected static final Logger LOG = Logger.getLogger("TESTPLUGIN");
	
	public static CommanderPlugin plugin;
	public static TestServer server;
	public static TestPlayer myplayer;
	
	public ScriptEnvironment environment;
	
	@Rule public TestName testname = new TestName();
	
	@BeforeClass public static void setUpClass() throws Exception {
		System.out.println(System.getProperty("user.dir"));
		server = (TestServer) Bukkit.getServer();
		if (server == null) {
			server = new TestServer();
			Bukkit.setServer(server);
		
			myplayer = new TestPlayer("TestPlayer", server);
			//other players on the server
			new TestPlayer("AAA", server);
			new TestPlayer("BBB", server);
			new TestPlayer("Notch", server);
			new TestPlayer("Ben", server);
		} else {
			myplayer = (TestPlayer) server.getPlayer("TestPlayer");
		}
		
		plugin = new CommanderPlugin();
		server.getPluginManager().enablePlugin(plugin);
	}
	
	@Before public void setUp() throws Exception {
		environment = new ScriptEnvironment(); {
			environment.setServer(server);
			environment.setCommandSender(myplayer);
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
}
