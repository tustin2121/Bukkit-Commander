package org.digiplex.bukkitplugin.commander.module;

import org.digiplex.bukkitplugin.commander.replacement.ReplacementPair;

public interface Module {
	public void addReplacementPair(ReplacementPair pair);
	public void clearReplacementPairs();
}
