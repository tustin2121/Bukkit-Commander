package org.digiplex.bukkitplugin.commander.module;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.digiplex.bukkitplugin.commander.CommanderPlugin;
import org.digiplex.bukkitplugin.commander.replacement.ReplacementPair;
import org.digiplex.bukkitplugin.commander.scripting.ScriptEnvironment;

public class PlayerCommandModule implements Module {
	private static final Logger Log = Logger.getLogger("Minecraft");
	
	public List<ReplacementPair> pairs;
	public boolean echoCmds = false;
	
	public PlayerCommandModule() {
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
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent e) {
		if (e.isCancelled()) return; //ignore canceled events
		try {
			ScriptEnvironment env = new ScriptEnvironment(); {
				env.setCommandSender(e.getPlayer());
				env.setServer(e.getPlayer().getServer());
			}
			
			//Log.info("CmdPre: "+e.getMessage());
			for (ReplacementPair rp : pairs) {
				Matcher m = rp.getRegex().matcher(e.getMessage().substring(1));
				if (m.matches()){
					env.setMatcher(m);
					
					if (echoCmds)
						Log.info("[PLAYERCMD] "+e.getPlayer().getName()+": "+ e.getMessage() +rp.predicateString());
					
					rp.executeEffects(env);
					e.setCancelled(true);
					
					return;
					
					/*
					if (rp.playerWillVanish()) {
						rp.executeReplacement(e);
						e.setCancelled(false);
						return;
					}
					
					String rps = m.replaceFirst(rp.executeReplacement(e));
					//Log.info(rps);
					e.setCancelled(true);
					if (echoCmds) 
						Log.info("[PLAYERCMD] "+e.getPlayer().getName()+": "+e.getMessage()+" ==> "+rps);
					e.getPlayer().performCommand(rps);
					return;*/
				}
			}
		} catch (Exception ex){
			CommanderPlugin.Log.log(Level.SEVERE, "[Commander] An exception was caught during player command replacement processing! Command passed through.", ex);
		}
		if (echoCmds) {
			Log.info("[PLAYERCMD] "+e.getPlayer().getName()+": "+e.getMessage());
		}
	}
}
