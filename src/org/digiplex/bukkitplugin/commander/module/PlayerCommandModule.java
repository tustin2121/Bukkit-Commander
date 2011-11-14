package org.digiplex.bukkitplugin.commander.module;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;

import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerListener;
import org.digiplex.bukkitplugin.commander.ReplacementPair;

public class PlayerCommandModule extends PlayerListener implements Module {
	private static final Logger Log = Logger.getLogger("Minecraft");
	
	public List<ReplacementPair> pairs;
	public boolean echoCmds = false;
	
	public PlayerCommandModule() {
		pairs = new ArrayList<ReplacementPair>();
	}
	
	@Override public void addReplacementPair(ReplacementPair pair) {
		pairs.add(pair);
	}
	@Override public void clearReplacementPairs() {
		pairs.clear();	
	}
	
	
	
	@Override public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent e) {
		//Log.info("CmdPre: "+e.getMessage());
		for (ReplacementPair rp : pairs) {
			Matcher m = rp.getRegex().matcher(e.getMessage().substring(1));
			if (m.matches()){
				String rps = m.replaceFirst(replacementString(rp.getReplacement(), e));
				//Log.info(rps);
				e.setCancelled(true);
				if (echoCmds) 
					Log.info("[PLAYERCMD] "+e.getPlayer().getName()+": "+e.getMessage()+" ==> "+rps);
				e.getPlayer().performCommand(rps);
				return;
			}
		}
		if (echoCmds) {
			Log.info("[PLAYERCMD] "+e.getPlayer().getName()+": "+e.getMessage());
		}
	}
	
	private String replacementString(String rep, PlayerCommandPreprocessEvent e){
		return rep
			.replaceAll("\\$p", e.getPlayer().getName())
			;
	}
}
