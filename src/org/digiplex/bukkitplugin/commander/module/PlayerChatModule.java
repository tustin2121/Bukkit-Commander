package org.digiplex.bukkitplugin.commander.module;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerListener;
import org.digiplex.bukkitplugin.commander.ReplacementPair;

public class PlayerChatModule extends PlayerListener implements Module {
	public List<ReplacementPair> pairs;
	public boolean allUpper = true;
	
	public PlayerChatModule() {
		pairs = new ArrayList<ReplacementPair>();
	}
	
	@Override public void addReplacementPair(ReplacementPair pair) {
		pairs.add(pair);
	}
	@Override public void clearReplacementPairs() {
		pairs.clear();	
	}
	
	@Override public void onPlayerChat(PlayerChatEvent e) {
		for (ReplacementPair rp : pairs) {
			StringBuffer sb = new StringBuffer();
			Matcher m = rp.getRegex().matcher(e.getMessage());
			
			if (!m.find()) continue;
			
			//loop through with find/replace
			do { //use do while, due to the find() invocation above
				//test if it is all upper, and replace with all upper
				if (allUpper && m.group().toUpperCase().equals(m.group())){
					m.appendReplacement(sb, rp.getReplacement().toUpperCase());
				} else {
					m.appendReplacement(sb, rp.getReplacement());
				}
			} while (m.find());
			m.appendTail(sb);
			
			e.setMessage(sb.toString());
		}
	}
}
