package commander.test;

import static junit.framework.Assert.assertTrue;

import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.digiplex.bukkitplugin.commander.CommanderPlugin;
import org.digiplex.bukkitplugin.commander.scripting.Executable;
import org.digiplex.bukkitplugin.commander.scripting.ScriptEnvironment;
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

public class TestAdvancedScripting {
	private static final Logger LOG = Logger.getLogger("TESTPLUGIN");
	
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
	
	//////////////////////////////////////////////////////////////////////////
	
	@Test public void giveCommand() throws Exception {
		Matcher m = Pattern.compile("\\/give (\\S+) (\\S+) (\\S+)")
				.matcher("/give everyone 320 cobblestone");
		//assumeTrue(m.matches());
		assertTrue("Cannot run test because prerequisite matching failed!", m.matches()); //this should be assumeTrue() but eclipse has a bug... 359944
		environment.setMatch(m);
		
		String[] commands = new String[] {
				"[if $1 = everyone] {",
				"    [foreach @player in $(server.players)] {",
				"        give @player $3 $2",
				"    }",
				"}",
				"[else]",
				"    give $1 $3 $2",
		};
		
		Executable sl = ScriptParser.parseScript(commands);
		sl.execute(environment);
		
		assertTrue("Commands don't match!", server.checkCommands(
				"give TestPlayer cobblestone 320", "give AAA cobblestone 320", "give BBB cobblestone 320", "give Notch cobblestone 320", "give Ben cobblestone 320"));
	} 
}
