package org.digiplex.bukkitplugin.commander.module;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.digiplex.bukkitplugin.commander.CommanderPlugin;
import org.digiplex.bukkitplugin.commander.replacement.ReplacementPair;
import org.digiplex.bukkitplugin.commander.scripting.ScriptEnvironment;

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
	@Override public void clearReplacementPairs() {
		pairs.clear();	
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerChat(AsyncPlayerChatEvent e) {
		if (e.isCancelled()) return; //ignore canceled events
		try {
			ScriptEnvironment env = new ScriptEnvironment(); {
				env.setCommandSender(e.getPlayer());
				env.setServer(e.getPlayer().getServer());
			}
			
			ArrayList<ReplacementPair> preparedEffects = new ArrayList<ReplacementPair>(); //holds all effects until all replacements done
			
			for (ReplacementPair rp : pairs) {
				StringBuffer sb = new StringBuffer();
				Matcher m = rp.getRegex().matcher(e.getMessage());
				
				if (!m.find()) continue;
				env.setMatcher(m);
				
				if (echoCmds)
					Log.info("[PLAYERCHAT] "+e.getPlayer().getName()+": "+ m.group(0) +rp.predicateString());
				
				if (rp.playerWillVanish()) { //the player will vanish as a result of this, special handling
					int cutlen = CommanderPlugin.instance.config.getInt("options.cutoff.length", 1);
					String cuttext = CommanderPlugin.instance.config.getString("options.cutoff.indicator", "--*");
					
					String rep = m.group().substring(0, cutlen).concat(cuttext);
					m.appendReplacement(sb, rep);
					e.setMessage(sb.toString());
					//e.setCancelled(true);
					//e.getPlayer().chat(sb.toString()); //chat first
					
					rp.executeEffects(env); //then execute the replacement
					return;
				}
				
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
				
				if (!preparedEffects.contains(rp))
					preparedEffects.add(rp);
				
				e.setMessage(sb.toString());
			}
			
			//after all replacements are in: execute the effects
			if (!preparedEffects.isEmpty()) {
				//e.setCancelled(true);
				//e.getPlayer().chat(sb.toString()); //chat first
				
				env.setMatcher(null);
				for (ReplacementPair rp : preparedEffects){
					rp.executeEffects(env);
				}
			}
		} catch (Exception ex){
			CommanderPlugin.Log.log(Level.SEVERE, "[Commander] An exception was caught during chat replacement processing! Chat passed through.", ex);
		}
	}
}
