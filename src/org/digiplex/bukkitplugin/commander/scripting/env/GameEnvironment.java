package org.digiplex.bukkitplugin.commander.scripting.env;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.digiplex.bukkitplugin.commander.CommanderPlugin;
import org.digiplex.bukkitplugin.commander.api.CommanderEnvVarModule;
import org.digiplex.bukkitplugin.commander.scripting.ScriptEnvironment;

/**
 * A central static class for getting environment variables from the Minecraft game. 
 * @author tpittman
 */
public class GameEnvironment {
	private static final Logger LOG = CommanderPlugin.Log;
	
	private static HashMap<String, CommanderEnvVarModule> pluginModules;
	private static EVPluginClassLoader pluginLoader; //TODO
	
	static {
		pluginModules = new HashMap<String, CommanderEnvVarModule>();
	}
	
	public static void loadPluginModules(File modulelist) {
//		if (pluginLoader == null)
//			pluginLoader = new URLClassLoader(urls);
		
		try {
			
		} finally {}
	}
	
	public static void registerCommanderPlugin(String name, CommanderEnvVarModule evm) {
		pluginModules.put(name, evm);
	}
	
	//////////////////////////////////////////////////////////////////////////////////
	
	private static List<String> makePlayersIntoList(OfflinePlayer[] playerList) {
		ArrayList<String> arr = new ArrayList<String>(playerList.length);
		for (OfflinePlayer op : playerList)
			arr.add(op.getName());
		return arr;
	}
	private static List<String> makePlayersIntoList(List<Player> playerList) {
		ArrayList<String> arr = new ArrayList<String>(playerList.size());
		for (OfflinePlayer op : playerList)
			arr.add(op.getName());
		return arr;
	}
	
	///////////////////////////////////////////////////////////////////////////////////
	
	/*
	 
	 * collections supported: (collections are represented in commands by a hash value of the format "{c123ABC75}") 
	 *    server.players = collection of all players
	 *    server.online = collection of all currently online players
	 * 
	 * 
	 * [maybe:]
	 * player values:
	 *    player.location.x = gets x block location of player
	 *    player.location.y = gets y block location of player
	 *    player.location.z = gets z block location of player
	 * 
	 * 
	 * 
	 * supported object types:
	 *    String
	 *    Integer
	 *    Boolean
	 *    Collection of Strings
	 */
	
	public static Object getEnvironmentVariable(String varname, ScriptEnvironment env) {
		String[] varpath = varname.split(".");
		
		if (varpath[0].equalsIgnoreCase("command"))		return getFromCommandNamespace(varpath[1], env);
		if (varpath[0].equalsIgnoreCase("server"))		return getFromServerNamespace(varpath[1], env);
		if (varpath[0].equalsIgnoreCase("world"))		return getFromWorldNamespace(varpath[1], env);
		
		if (varpath[0].equalsIgnoreCase("plugin")) {
			String pluginname = varpath[1];
			String passed = varname.substring(6 + 2 + pluginname.length()); //past "plugin.<name>."
			return getFromPluginNamespace(pluginname, passed, env);
		}
		
		return null;
	}

	/** 
	 * command = a namespace for values derived from the last executed command
	 *    command.return = a special value that commands can set via the Commander API (forthcoming?)
	 *    command.found = true if the command was found and executed successfully
	 *    command.error = true if the command threw an error while executing
	 */
	private static Object getFromCommandNamespace(String name, ScriptEnvironment env) {
		if (name.equalsIgnoreCase("return")) 	return env.getCommandReturn();
		if (name.equalsIgnoreCase("found")) 	return env.getCommandFound();
		if (name.equalsIgnoreCase("error")) 	return env.didLastCommandError();
		return null;
	}
	
	/**
	 * server = a namespace for values derived from the current server
	 *    server.offline = collection of all players
	 *    server.players = collection of all currently online players
	 *    server.motd = gets the message of the day. because, why not?
	 */
	private static Object getFromServerNamespace(String name, ScriptEnvironment env) {
		if (name.equalsIgnoreCase("offline"))	return makePlayersIntoList(env.getServer().getOfflinePlayers());
		if (name.equalsIgnoreCase("players"))	return makePlayersIntoList(env.getServer().getOnlinePlayers());
		
		if (name.equalsIgnoreCase("motd"))		return env.getServer().getMotd();
		return null;
	}
	
	
	private static Object getFromWorldNamespace(String name, ScriptEnvironment env) {
		World w = null;
		if (env.getPlayer() == null) {
			LOG.warning("Error! Cannot get from world environment variable namespace when not a player in-game!");
			//TODO allow setting of the current world, perhaps?
			return null;
		}
		w = env.getPlayer().getWorld();
		
		if (name.equalsIgnoreCase("time"))		return w.getTime();
		if (name.equalsIgnoreCase("name"))		return w.getName();
		if (name.equalsIgnoreCase("players"))	return makePlayersIntoList(w.getPlayers());
		if (name.equalsIgnoreCase("ispvp"))		return w.getPVP();
		if (name.equalsIgnoreCase("storming"))	return w.isThundering();
		return null;
	}
	
	private static Object getFromPluginNamespace(String plugin, String passed, ScriptEnvironment env) {
		try {
			CommanderEnvVarModule m = pluginModules.get(plugin);
			if (m == null) {
				LOG.severe("Script requested value from plugin \""+plugin+"\", but no such module is registered! Please check spelling; uppercase counts!");
				return null;
			}
			return m.getEVValue(passed, env.getCommandSender());
		} catch (Exception ex) {
			LOG.log(Level.SEVERE, "Error getting third-party environment variable from plugin \""+plugin+"\"!", ex);
			return null;
		}
	}
	
}
