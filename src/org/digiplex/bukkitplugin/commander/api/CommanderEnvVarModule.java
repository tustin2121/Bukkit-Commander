package org.digiplex.bukkitplugin.commander.api;

import org.bukkit.command.CommandSender;

/**
 * A module that provides environment variables to the Commander plugin, so that the game 
 * (and potentially other plugins) can give variables and states to scripts running in Commander. 
 * @author tpittman
 */
public abstract class CommanderEnvVarModule {
	
	public abstract Object getEVValue(String varname, CommandSender sender);
	
}
