package org.digiplex.bukkitplugin.commander.module;

import java.util.List;

import org.bukkit.event.Listener;
import org.digiplex.bukkitplugin.commander.CommanderEngine.MatchingContext;
import org.digiplex.bukkitplugin.commander.replacement.ReplacementPair;

public interface Module extends Listener {
	public MatchingContext getMatchingContext();
	
	public void addReplacementPair(ReplacementPair pair);
	public void addReplacementPairs(List<ReplacementPair> pair);
	public void clearReplacementPairs();
}
