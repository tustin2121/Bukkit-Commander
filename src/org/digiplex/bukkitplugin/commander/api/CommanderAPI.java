package org.digiplex.bukkitplugin.commander.api;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.digiplex.bukkitplugin.commander.CommanderEngine;
import org.digiplex.bukkitplugin.commander.CommanderEngine.MatchingContext;
import org.digiplex.bukkitplugin.commander.replacement.ReplacementPair;
import org.digiplex.bukkitplugin.commander.scripting.Executable;
import org.digiplex.bukkitplugin.commander.scripting.ScriptBlock;
import org.digiplex.bukkitplugin.commander.scripting.ScriptParser;
import org.digiplex.bukkitplugin.commander.scripting.env.GameEnvironment;
import org.digiplex.bukkitplugin.commander.scripting.exceptions.BadScriptException;

/**
 * For plugins that want the features of Commander's scripting engine without having to setup and
 * deal with the engine classes themselves, this is the CommanderAPI. This static class provides 
 * methods for accessing the Commander scripting engine opaquely.
 * @author Tim
 */
public class CommanderAPI {
	static {
		CommanderEngine.registerInstance();
	}
	
	/**
	 * This method allows plugins to register their EVM 
	 * ({@link org.digiplex.bukkitplugin.commander.api.CmdrEnvVarModule CommanderEnvironmentVariableModule})
	 * with the scripting engine. A EVM allows the plugin to give Commander scripts variables through the
	 * environment variable feature.
	 * 
	 * <p>For example, a zones plugin can register an EVM which will respond to environment variable 
	 * "plugin.ZonePlugin.allowed.edit". It will check if the player running the script is in a zone
	 * that allows him to edit the world, and return a Boolean true or false. Scripts can check this
	 * variable in an if statement and do something accordingly.
	 * @param evm This plugin's subclass of the CmdrEnvVarModule class
	 */
	public static void registerEVM(CmdrEnvVarModule evm) {
		GameEnvironment.registerCommanderPlugin(evm.getNamespace(), evm);
	}
	
	/**
	 * Returns an opaque script object that holds the script with the given alias. This script can be
	 * run at any time via one of the {@code execute()} methods.
	 * @param name Alias/Name of the script to retreive
	 * @return A script object containing the desired script, or null if no script of that name was found.
	 */
	public static CmdrScript getScript(String name) {
		Executable exe = CommanderEngine.getInstance().getScript(name);
		if (exe == null) return null;
		
		CmdrScript script = new CmdrScript();
		script.block = exe;
		return script;
	}
	
	/**
	 * Parses the given array of strings as a complete script. If any errors occur while parsing,
	 * an IllegalArgumentException is thrown with the reason.
	 * <p>While the script alias is not required (you may pass null), it is encouraged.
	 * @param lines A complete script that will be parsed.
	 * @param alias The name of this script, or null.
	 * @return A {@link CmdrScript} object with the parsed and prepared script.
	 * @throws IllegalArgumentException Thrown if an error occurs while parsing the script.
	 */
	public static CmdrScript parseScript(String[] lines, String alias) throws IllegalArgumentException {
		try {
			Executable exe = ScriptParser.parseScript(lines);
			if (alias != null) CommanderEngine.getInstance().setScriptForAlias(alias, (ScriptBlock) exe);
			
			CmdrScript script = new CmdrScript();
			script.block = exe;
			return script;
		} catch (BadScriptException e) {
			throw new IllegalArgumentException("Bad Script! "+e.getMessage(), e);
		}
		
	}
	
	/**
	 * Makes the Commander Engine parse the given file as a command replacement file, and returns
	 * the results of that parsing as a list of CmdrReplacement objects. Parsing takes place with
	 * the same function that the Commander standalone plugin uses, and therefore uses the same
	 * text structure.
	 * @param file A file to parse
	 * @return A list of CmdrReplacement objects.
	 */
	public static List<CmdrReplacement> parseCommandFile(File file) {
		return parseReplacementFile(file, MatchingContext.Command);
	}
	/**
	 * Makes the Commander Engine parse the given file as a chat replacement file, and returns
	 * the results of that parsing as a list of CmdrReplacement objects. Parsing takes place with
	 * the same function that the Commander standalone plugin uses, and therefore uses the same
	 * text structure.
	 * @param file A file to parse
	 * @return A list of CmdrReplacement objects.
	 */
	public static List<CmdrReplacement> parseChatFile(File file) {
		return parseReplacementFile(file, MatchingContext.Chat);
	}
	//Not public API, accessed via above two functions
	private static List<CmdrReplacement> parseReplacementFile(File file, MatchingContext ctx) {
		List<ReplacementPair> rps = CommanderEngine.getInstance().loadReplacementListFromFile(file, ctx);
		ArrayList<CmdrReplacement> lst = new ArrayList<CmdrReplacement>();
		for (ReplacementPair rp : rps)
			lst.add(new CmdrReplacement(rp));
		return lst;
	}
	
	/**
	 * Makes the Commander Engine parse the given file as a script library file, and stores the
	 * resulting parsed scripts under their proper aliases in the engine. Parsing takes place with
	 * the same function that the Commander standalone plugin uses, and therefore uses the same
	 * text structure. Access to the parsed scripts can be done through the {@code getScript()} function.
	 * @param file A file to parse
	 */
	public static void parseScriptFile(File file) {
		CommanderEngine.getInstance().loadScriptsFromFile(file);
	}
}
