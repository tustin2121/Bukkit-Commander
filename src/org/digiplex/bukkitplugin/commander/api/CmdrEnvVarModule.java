package org.digiplex.bukkitplugin.commander.api;

import org.bukkit.command.CommandSender;

/**
 * <p>Full name: {@code CommanderEnvironmentVariableModule} (That's why I cut it down)</p>
 * A module that provides environment variables to the Commander plugin, so that other plugins
 * can provide variables and states to scripts running in Commander. 
 * @author tpittman
 */
public abstract class CmdrEnvVarModule {
	
	/**
	 * Returns the sub-namespace this EVM should be registered under by default. This value will be
	 * prepended by "plugin." by the registrar. This should be the name of your plugin.
	 * @return case sensitive name of the plugin.
	 */
	public abstract String getNamespace();
	
	/**
	 * Called by the Commander Scripting Engine when a script requests an environment variable
	 * in the "plugin.[pluginName]" namespace. The script is called with the variable name, sans
	 * the "plugin.[pluginName]." that prepends the name. This method should return a proper object
	 * for the given value, or null if that value doesn't exist or the function name is wrong.
	 * @param varname 
	 * 			The Environment Variable or function called on your plugin
	 * @param sender
	 * 			The wrapped command sender who is running the script. If this is being run by a player,
	 * 			the {@code sender} will be an instance of the {@link org.bukkit.entity.Player Player} interface. 
	 * 			From this sender, you can get things like the server or world the sender is in.  
	 * @return
	 * 		An {@code Integer}, {@code Boolean}, {@code String}, {@code List<String>}, or null. 
	 */
	public abstract Object getEVValue(String varname, CommandSender sender);
	
}
