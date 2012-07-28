package org.digiplex.bukkitplugin.commander.module;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Matcher;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.server.ServerCommandEvent;
import org.digiplex.bukkitplugin.commander.CommanderPlugin;
import org.digiplex.bukkitplugin.commander.replacement.ReplacementPair;
import org.digiplex.bukkitplugin.commander.scripting.ScriptEnvironment;

public class ConsoleCommandModule implements Module {
	public List<ReplacementPair> pairs;
	
	public ConsoleCommandModule() {
		pairs = new ArrayList<ReplacementPair>();
	}
	
	@Override public MatchingContext getMatchingContext() {
		return MatchingContext.Command;
	}
	@Override public void addReplacementPair(ReplacementPair pair) {
		pairs.add(pair);
	}
	@Override public void clearReplacementPairs() {
		pairs.clear();	
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onServerCommand(ServerCommandEvent e) {
		try {
			ScriptEnvironment env = new ScriptEnvironment(); {
				env.setCommandSender(e.getSender());
				env.setServer(e.getSender().getServer());
			}
			
			for (ReplacementPair rp : pairs) {
				Matcher m = rp.getRegex().matcher(e.getCommand());
				if (m.matches()){
					env.setMatch(m.toMatchResult());
					
					rp.executeEffects(env);
					e.setCommand("commander null"); //does nothing, prints nothing
					//e.setCommand(m.replaceFirst(rp.executeString(env)));
					
					return;
				}
			}
		} catch (Exception ex){
			CommanderPlugin.Log.log(Level.SEVERE, "[Commander] An exception was caught during command replacement processing! Command passed through.", ex);
		}
	}
}
