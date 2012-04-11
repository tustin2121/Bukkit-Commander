package org.digiplex.bukkitplugin.commander.module;

import org.bukkit.event.Listener;
import org.digiplex.bukkitplugin.commander.replacement.ReplacementPair;

public interface Module extends Listener {
	public enum MatchingContext {
		Chat, Command,
	}
	public MatchingContext getMatchingContext();
	
	public void addReplacementPair(ReplacementPair pair);
	public void clearReplacementPairs();
}
