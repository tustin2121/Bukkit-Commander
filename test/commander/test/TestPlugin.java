package commander.test;

import org.bukkit.Bukkit;
import org.digiplex.bukkitplugin.commander.CommanderPlugin;
import org.digiplex.bukkitplugin.commander.scripting.ScriptEnvironment;
import org.digiplex.bukkitplugin.commander.scripting.ScriptLine;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import commander.test.placeholders.TestPlayer;
import commander.test.placeholders.TestServer;

public class TestPlugin {
	public CommanderPlugin plugin;
	public ScriptEnvironment environment;
	public TestServer server;
	
	@Before public void setUp() throws Exception {
		System.out.println(System.getProperty("user.dir"));
		server = new TestServer();
		Bukkit.setServer(server);
		
		plugin = new CommanderPlugin();
		server.getPluginManager().enablePlugin(plugin);
		
		environment = new ScriptEnvironment(); {
			environment.setServer(server);
			environment.setCommandSender(new TestPlayer(server));
		}
	}
	
	@After public void tearDown() throws Exception {
		plugin.onDisable();
	}
	
	//////////////////////////////////////////////////////////////////////////
	
	@Test public void testSimpleLine() {
		String command = "Test Line 1";
		
		ScriptLine sl = ScriptLine.parseScriptLine(command);
		sl.execute(environment);
		
		assert(true);
	}

}
