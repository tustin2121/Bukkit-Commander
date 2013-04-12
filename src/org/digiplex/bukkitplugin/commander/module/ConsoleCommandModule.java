package org.digiplex.bukkitplugin.commander.module;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Matcher;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandException;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.server.ServerCommandEvent;
import org.digiplex.bukkitplugin.commander.CommanderEngine;
import org.digiplex.bukkitplugin.commander.CommanderEngine.MatchingContext;
import org.digiplex.bukkitplugin.commander.replacement.ReplacementPair;
import org.digiplex.bukkitplugin.commander.scripting.ScriptEnvironment;
import org.digiplex.bukkitplugin.commander.scripting.exceptions.BreakScriptException;
import org.digiplex.bukkitplugin.commander.scripting.exceptions.CommandExecutionException;

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
	@Override public void addReplacementPairs(List<ReplacementPair> pair) {
		pairs.addAll(pair);
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
					
					try {
						rp.executeEffects(env);
					} catch (BreakScriptException ex){
						//this space intentionally left blank
					} catch (CommandExecutionException ex) {
						CommanderEngine.reportCommandException(ex, true);
					}
					e.setCommand(CommanderEngine.getConfig().getString("options.commands.null")); //does nothing, prints nothing
					//e.setCommand(m.replaceFirst(rp.executeString(env)));
					
					return;
				}
			}
		} catch (Exception ex){
			CommanderEngine.Log.log(Level.SEVERE, "[Commander] An exception was caught during command replacement processing! Command passed through.", ex);
		}
	}
}
