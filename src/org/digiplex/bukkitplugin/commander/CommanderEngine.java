package org.digiplex.bukkitplugin.commander;

import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Logger;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.entity.Player;
import org.digiplex.bukkitplugin.commander.scripting.EchoControl;
import org.digiplex.bukkitplugin.commander.scripting.ScriptBlock;
import org.digiplex.bukkitplugin.commander.scripting.ScriptEnvironment;
import org.digiplex.bukkitplugin.commander.scripting.exceptions.BadScriptException;

public class CommanderEngine {
	public static final Logger Log = Logger.getLogger("Minecraft");
	public static final CommanderCommandSender ccs = new CommanderCommandSender();
	
	private static CommanderEngine instance;
	private static int usageInstances;
	
	public static void registerInstance() {
		usageInstances++;
		if (instance == null)
			instance = new CommanderEngine();
	}
	public static CommanderEngine getInstance() {
		return instance;
	}
	public static void unregisterInstance() {
		usageInstances--;
		if (usageInstances == 0)
			instance = null;
	}
	
	private CommanderEngine() {}
	
	//////////////////////////////////////////////////////////////////////////////////////////
	// Options
	private final MemoryConfiguration defaultOptions;
	private Configuration currentOptions;
	{ //instance constructor
		defaultOptions = new MemoryConfiguration();
		defaultOptions.set("options.default-echo", true);
		defaultOptions.set("options.match-uppercase", true);
		defaultOptions.set("options.commands.echo", true);
		defaultOptions.set("options.cutoff.length", 1);
		defaultOptions.set("options.cutoff.indicator", "--*");
		
		defaultOptions.set("log.player.commands", false);
		defaultOptions.set("log.player.chat", false);
		
		defaultOptions.set("files.playercmd",  "playercmd.txt");
		defaultOptions.set("files.playerchat", "playerchat.txt");
		defaultOptions.set("files.consolecmd", "consolecmd.txt");
		defaultOptions.set("files.script-files", Arrays.asList("scripts.txt"));
		
		currentOptions = new MemoryConfiguration(defaultOptions);
	}
	
	public Configuration getConfig() { return currentOptions; }
	
	public void setConfig(Configuration config) {
		this.currentOptions = config;
		config.setDefaults(defaultOptions);
	}
	
	public void reload(){
		//clear all saved states in the engine
		currentOptions = new MemoryConfiguration(defaultOptions);
		
		aliasedScripts.clear();
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////
	// Scripts
	private HashMap<String, ScriptBlock> aliasedScripts = new HashMap<String, ScriptBlock>();
	
	public boolean scriptDebugMode = false;
	
	public void setScriptForAlias(String alias, ScriptBlock script){
		if (alias == null || alias.isEmpty()) return;
		aliasedScripts.put(alias, script);
	}
	public ScriptBlock getScript(String alias){
		return aliasedScripts.get(alias);
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////
	// Commands	
	public class AdminCommand implements CommandExecutor {
		@Override public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
			if (args[0].equals("null")){ //undocumented command that does nothing, used internally
				return true;
			}
			
			if (sender instanceof Player) {
				sender.sendMessage("Commander commands can only be executed from the console.");
				return true;
			}
			if (args[0].equalsIgnoreCase("reload")){
				reload();
				return true;
			} else if (args[0].equalsIgnoreCase("debug")){
				scriptDebugMode = !scriptDebugMode;
				Log.info("Script debugging "+((scriptDebugMode)?"enabled":"disabled"));
				return true;
			} else if (args[0].equalsIgnoreCase("runscript")){
				try {
					if (args.length < 2) return false;
					String scriptname = args[1];
					ScriptBlock sb = getScript(scriptname);
					
					if (scriptDebugMode) Log.info("[Commander:DEBUG:run] "+scriptname+" == "+sb);
					
					if (sb == null){
						Log.info("[Commander] No script for registered for the alias \""+scriptname+"\"");
					} else {
						ScriptEnvironment env = new ScriptEnvironment(); {
							env.setCommandSender(sender);
							env.setServer(sender.getServer());
						}
						sb.execute(env);
					}
				} catch (BadScriptException ex) {
					Log.info("");
				}
				return true;
			}
			return false;
		}
	}
	
	public static class EchoCommand implements CommandExecutor {
		@Override public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
			if (args.length == 0) return false;
			
			StringBuffer sb = new StringBuffer(args[0]);
			for (int i = 1; i < args.length; i++){
				sb.append(' ').append(args[i]);
			}
			((EchoControl)sender).getWrappedSender().sendMessage(sb.toString());
			//sender.sendMessage(sb.toString());
			return true;
		}
	}

}
