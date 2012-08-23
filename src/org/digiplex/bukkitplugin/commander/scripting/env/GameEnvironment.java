package org.digiplex.bukkitplugin.commander.scripting.env;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.digiplex.bukkitplugin.commander.CommanderEngine;
import org.digiplex.bukkitplugin.commander.CommanderPlugin;
import org.digiplex.bukkitplugin.commander.api.CmdrEnvVarModule;
import org.digiplex.bukkitplugin.commander.scripting.ScriptEnvironment;

/**
 * A central static class for getting environment variables from the Minecraft game. 
 * @author tpittman
 */
public class GameEnvironment {
	private static final Logger LOG = CommanderPlugin.Log;

	private static final int MAX_DIST = 50;
	private static final HashSet<Byte> TRANS_BLOCKS;
	static {
		TRANS_BLOCKS = new HashSet<Byte>();
		TRANS_BLOCKS.add((byte) Material.AIR.getId());
		TRANS_BLOCKS.add((byte) Material.WATER.getId());
		TRANS_BLOCKS.add((byte) Material.STATIONARY_WATER.getId());
		TRANS_BLOCKS.add((byte) Material.LAVA.getId());
		TRANS_BLOCKS.add((byte) Material.STATIONARY_LAVA.getId());
	}
	private static HashMap<String, CmdrEnvVarModule> pluginModules;
	private static EVPluginClassLoader pluginLoader; //TODO
	
	static {
		pluginModules = new HashMap<String, CmdrEnvVarModule>();
	}
	
	public static void loadPluginModules(File modulelist) {
//		if (pluginLoader == null)
//			pluginLoader = new URLClassLoader(urls);
		
		try {
			
		} finally {}
	}
	
	public static void registerCommanderPlugin(String name, CmdrEnvVarModule evm) {
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
	private static List<String> makePotionsIntoList(Collection<PotionEffect> effects) {
		ArrayList<String> arr = new ArrayList<String>(effects.size());
		for (PotionEffect op : effects)
			arr.add(op.getType().getName());
		return arr;
	}
	
	///////////////////////////////////////////////////////////////////////////////////
	
	private static Object evError(String msg) {
		if (CommanderEngine.getInstance().scriptDebugMode)
			CommanderEngine.Log.warning("[Commander:DEBUG:envVar] Error getting EV: "+msg);
		return null; //always return null
	}
	
	private static String implode(Collection<String> strs, String glue) {
		if (strs == null || strs.size() == 0) return "";
		Iterator<String> it = strs.iterator();
		StringBuilder sb = new StringBuilder(it.next());
		for (; it.hasNext();) {
			sb.append(glue).append(it.next());
		}
		return sb.toString();
	}
	private static String implode(String[] strs, String glue) {
		if (strs == null || strs.length == 0) return "";
		StringBuilder sb = new StringBuilder(strs[0]);
		for (int i = 1; i < strs.length; i++) {
			sb.append(glue).append(strs[i]);
		}
		return sb.toString();
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
		String[] args = varname.split("\\s+");
		String[] varpath = args[0].split("\\.");
		
		if (varpath[0].matches("(?i)fn|function"))		return getFromFunctionNamespace(varpath[1], args, env);
		if (varpath[0].equalsIgnoreCase("command"))		return getFromCommandNamespace(varpath[1], env);
		if (varpath[0].equalsIgnoreCase("server"))		return getFromServerNamespace(varpath[1], env);
		if (varpath[0].equalsIgnoreCase("world"))		return getFromWorldNamespace(Arrays.copyOfRange(varpath, 1, varpath.length), env);
		
		if (varpath[0].equalsIgnoreCase("me"))
			return getFromPlayerNamespace(env.getCommandSender(), Arrays.copyOfRange(varpath, 1, varpath.length), env);
		if (varpath[0].equalsIgnoreCase("player")) {
			Player p = env.getServer().getPlayer(varpath[1]);
			if (p == null) return evError("Cannot get Player with name \""+varpath[1]+"\"");
			return getFromPlayerNamespace(p, Arrays.copyOfRange(varpath, 1, varpath.length), env);
		}
		
		if (varpath[0].equalsIgnoreCase("plugin")) {
			String pluginname = varpath[1];
			String passed = varname.substring(6 + 2 + pluginname.length()); //past "plugin.<name>."
			return getFromPluginNamespace(pluginname, passed, env);
		}
		
		return evError("No namespace matching '"+varpath[0]+"'");
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
		return evError("No variable '"+name+"' in command namespace!");
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
		return evError("No variable '"+name+"' in server namespace!");
	}
	
	
	private static Object getFromWorldNamespace(String[] names, ScriptEnvironment env) {
		World w = null;
		if (env.getPlayer() == null) {
			LOG.warning("Error! Cannot get from world environment variable namespace when not a player in-game!");
			//TODO allow setting of the current world, perhaps?
			return null;
		}
		w = env.getPlayer().getWorld();
		
		switch (names.length) {
		case 1:
			if (names[0].equalsIgnoreCase("time"))		return w.getTime();
			if (names[0].equalsIgnoreCase("name"))		return w.getName();
			if (names[0].equalsIgnoreCase("players"))	return makePlayersIntoList(w.getPlayers());
			if (names[0].equalsIgnoreCase("ispvp"))		return w.getPVP();
			if (names[0].equalsIgnoreCase("storming"))	return w.isThundering();
			if (names[0].equalsIgnoreCase("sealevel"))	return w.getSeaLevel();
			break;
		case 2:
			if (names[0].equalsIgnoreCase("spawn")) {
				if (names[1].equalsIgnoreCase("x")) return w.getSpawnLocation().getBlockX();
				if (names[1].equalsIgnoreCase("y")) return w.getSpawnLocation().getBlockY();
				if (names[1].equalsIgnoreCase("z")) return w.getSpawnLocation().getBlockZ();
			}
			break;
		}
		return evError("No variable '"+implode(names, ".")+"' in world namespace!");
	}
	
	private static Object getFromPlayerNamespace(CommandSender sender, String[] names, ScriptEnvironment env) {
		if (!(sender instanceof Player)) return null;
		Player p = (Player) sender;
		switch (names.length) {
		case 1:
			if (names[0].equalsIgnoreCase("name")) return p.getName();
			if (names[0].equalsIgnoreCase("displayname")) return p.getDisplayName();
			if (names[0].equalsIgnoreCase("murderer")) return p.getKiller().getName();
			
			if (names[0].equalsIgnoreCase("level")) return p.getLevel();
			if (names[0].equalsIgnoreCase("health")) return p.getHealth();
			if (names[0].equalsIgnoreCase("healthmax")) return p.getMaxHealth();
			if (names[0].equalsIgnoreCase("air")) return p.getRemainingAir();
			if (names[0].equalsIgnoreCase("airmax")) return p.getMaximumAir();
			if (names[0].equalsIgnoreCase("food")) return p.getFoodLevel();
			//if (names[0].equalsIgnoreCase("foodsat")) return p.getSaturation(); //no float support
			
			if (names[0].equalsIgnoreCase("potions")) return makePotionsIntoList(p.getActivePotionEffects());
			if (names[0].equalsIgnoreCase("ismoving")) return p.getVelocity().lengthSquared() != 0; //TODO test
			break;
		case 2:
			if (names[0].matches("(?i)location|position|at")) {
				if (names[1].equalsIgnoreCase("x")) return p.getLocation().getBlockX();
				if (names[1].equalsIgnoreCase("y")) return p.getLocation().getBlockY();
				if (names[1].equalsIgnoreCase("z")) return p.getLocation().getBlockZ();
			}
			if (names[0].matches("(?i)crossh[ea]ir|reticu?le|lookat")) {
				if (names[1].equalsIgnoreCase("x")) return p.getTargetBlock(TRANS_BLOCKS, MAX_DIST).getX();
				if (names[1].equalsIgnoreCase("y")) return p.getTargetBlock(TRANS_BLOCKS, MAX_DIST).getY();
				if (names[1].equalsIgnoreCase("z")) return p.getTargetBlock(TRANS_BLOCKS, MAX_DIST).getZ();
			}
			if (names[0].equalsIgnoreCase("compass")) {
				if (names[1].equalsIgnoreCase("x")) return p.getCompassTarget().getBlockX();
				if (names[1].equalsIgnoreCase("y")) return p.getCompassTarget().getBlockY();
				if (names[1].equalsIgnoreCase("z")) return p.getCompassTarget().getBlockZ();
			}
			break;
		}
		return evError("No variable '"+implode(names, ".")+"' in world namespace!");
	}
	
	private static Object getFromPluginNamespace(String plugin, String passed, ScriptEnvironment env) {
		try {
			CmdrEnvVarModule m = pluginModules.get(plugin);
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
	
	//////////////////////////////// Function Namespace ////////////////////////////////////
	private static Object getFromFunctionNamespace(String name, String[] args, ScriptEnvironment env) {
		if (name.equalsIgnoreCase("random"))	return random(args);
		if (name.equalsIgnoreCase("intmath"))	return intmath(args);
		if (name.equalsIgnoreCase("substr"))	return substr(args);
		return evError("No function with name '"+name+"' in function namespace!");
	}
	
	private static int random(String[] args) {
		switch (args.length) {
		case 3: {
			int min = Integer.parseInt(args[1]);
			int max = Integer.parseInt(args[2]);
			return (int)(Math.random() * (max-min)) + min;
		}
		case 2:
			return (int)(Math.random() * Integer.parseInt(args[1]));
		default:
			return (int)(Math.random() * 100);
		}
	}
	
	private static int intmath(String[] args) {
		StringBuilder sb = new StringBuilder();
		for (int i = 1; i < args.length; i++)
			sb.append(args[i]).append(' ');
		
		return 0; //unsupported
	}
	
	private static String substr(String[] args) {
		if (args.length < 3)
			return (String) evError("Must have at least 3 arguments for substr function!");
		try {
			int start = Integer.parseInt(args[0]);
			int end = Integer.parseInt(args[1]);
			StringBuffer sb = new StringBuffer(args[2]);
			for (int i = 3; i < args.length; i++)
				sb.append(' ').append(args[i]);
			
			return sb.toString().substring(start, end);
		} catch (NumberFormatException ex) {
			return (String) evError("Error parsing arguments to substr function!");
		}
	}
	
}
