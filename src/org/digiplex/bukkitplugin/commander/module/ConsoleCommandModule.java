package org.digiplex.bukkitplugin.commander.module;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.event.server.ServerListener;
import org.digiplex.bukkitplugin.commander.ReplacementPair;

public class ConsoleCommandModule extends ServerListener implements Module {
	public List<ReplacementPair> pairs;
	
	public ConsoleCommandModule() {
		pairs = new ArrayList<ReplacementPair>();
	}
	
	@Override public void addReplacementPair(ReplacementPair pair) {
		pairs.add(pair);
	}
	@Override public void clearReplacementPairs() {
		pairs.clear();	
	}
	
	@Override public void onServerCommand(ServerCommandEvent e) {
		for (ReplacementPair rp : pairs) {
			Matcher m = rp.getRegex().matcher(e.getCommand());
			if (m.matches()){
				e.setCommand(m.replaceFirst(rp.getReplacement()));
				return;
			}
		}
	}
}
