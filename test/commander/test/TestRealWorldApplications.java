package commander.test;

import static junit.framework.Assert.assertTrue;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Location;
import org.digiplex.bukkitplugin.commander.scripting.Executable;
import org.digiplex.bukkitplugin.commander.scripting.ScriptParser;
import org.junit.Test;

public class TestRealWorldApplications extends TestCase {
	
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
	
	@Test public void seaLevelCommand() throws Exception {
		String[] commands = new String[] {
			    "@blockY = $(me.position.y)",
			    "@sl = $(world.sealevel)",
			    "[if @blockY > @sl] {",
			    "    echo You are above sea level.",
			    "}",
			    "[else if @blockY < @sl]",
			    "    echo You are below sea level.",
			    "[else]",
			    "    echo You are at sea level!",
		};
		Executable sl = ScriptParser.parseScript(commands);
		
		myplayer.teleport(new Location(myplayer.getWorld(), 0, 23, 0));
		sl.execute(environment);
		
		myplayer.teleport(new Location(myplayer.getWorld(), 0, 122, 0));
		sl.execute(environment);
		
		myplayer.teleport(new Location(myplayer.getWorld(), 0, 64, 0));
		sl.execute(environment);
		
		assertTrue("Commands don't match!", server.checkCommands(
				"echo You are below sea level.", "echo You are above sea level.", "echo You are at sea level!"));
	}
	
	@Test public void stringConcat() throws Exception {
		String[] commands  = new String[] {
				"@list",
				"[foreach @player in $(server.players)] {",
				"    [!if @list]    #check if the list is empty",
				"        @list = @player",
				"    [else]",
				"        @list = @list, @player",
				"}",
				"echo We mourn the passing of these dickweeds: @list",
		};
		
		Executable sl = ScriptParser.parseScript(commands);
		sl.execute(environment);
		
		assertTrue("Commands don't match!", server.checkCommands(
				"echo We mourn the passing of these dickweeds: TestPlayer, AAA, BBB, Notch, Ben"));
	}
}
