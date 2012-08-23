package org.digiplex.bukkitplugin.commander.module;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.digiplex.bukkitplugin.commander.CommanderEngine;
import org.digiplex.bukkitplugin.commander.CommanderEngine.MatchingContext;
import org.digiplex.bukkitplugin.commander.replacement.ReplacementPair;
import org.digiplex.bukkitplugin.commander.scripting.ScriptEnvironment;
import org.digiplex.bukkitplugin.commander.scripting.exceptions.BadScriptException;
import org.digiplex.bukkitplugin.commander.scripting.exceptions.BreakScriptException;

public class PlayerChatModule implements Module {
	private static final Logger Log = Logger.getLogger("Minecraft");
	
	public List<ReplacementPair> pairs;
	public boolean echoCmds = false;
	public boolean allUpper = true;
	
	public PlayerChatModule() {
		pairs = new ArrayList<ReplacementPair>();
	}
	@Override public MatchingContext getMatchingContext() {
		return MatchingContext.Chat;
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
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerChat(AsyncPlayerChatEvent e) {
		try {
			ScriptEnvironment env = new ScriptEnvironment(); {
				env.setCommandSender(e.getPlayer());
				env.setServer(e.getPlayer().getServer());
			}
			
			for (ReplacementPair rp : pairs) {
				StringBuffer sb = new StringBuffer();
				Matcher m = rp.getRegex().matcher(e.getMessage());
				
				if (!m.find()) continue;
				env.setMatch(m.toMatchResult());
				//special variables for replacement and cutoff
				env.setVariableGlobally("__repl__", "$0");
				env.setVariableGlobally("__endparse__", false);
				
				if (echoCmds)
					Log.info("[PLAYERCHAT] "+e.getPlayer().getName()+": "+ m.group(0) +rp.predicateString());
				
				try {
					rp.executeEffects(env);
				} catch (BreakScriptException ex) {}
				
				{ //test if we are cutting off
					Object o = env.getVariableValue("__endparse__");
					if (o instanceof Boolean && ((Boolean) o).booleanValue()) {
						//if we are here, that means that the endparse variable is true, and we should cutoff
						String repl = "--*";
						o = env.getVariableValue("__repl__");
						if (o instanceof String)
							repl = o.toString();
						 
						m.appendReplacement(sb, repl);
						e.setMessage(sb.toString());
						return;
					}
				} //else, do normally
				
				//loop through with find/replace
				do { //use do while, due to the find() invocation above
					//test if it is all upper, and replace with all upper
					if (allUpper && m.group().toUpperCase().equals(m.group())){
						m.appendReplacement(sb, rp.executeString(env).toUpperCase());
					} else {
						m.appendReplacement(sb, rp.executeString(env));
					}
				} while (m.find());
				m.appendTail(sb);
				
				e.setMessage(sb.toString());
			}
			
		} catch (BadScriptException ex) {
			CommanderEngine.Log.severe("[Commander] Script Error: "+ex.getMessage());
			e.getPlayer().sendMessage("[Commander] An error occurred while processing the script.");
		} catch (Exception ex) {
			CommanderEngine.Log.log(Level.SEVERE, "[Commander] An exception was caught during chat replacement processing! Chat passed through.", ex);
		}
	}
}
