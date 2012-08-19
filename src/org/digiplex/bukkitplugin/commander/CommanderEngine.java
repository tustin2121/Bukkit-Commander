package org.digiplex.bukkitplugin.commander;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.entity.Player;
import org.digiplex.bukkitplugin.commander.api.CmdrEnvVarModule;
import org.digiplex.bukkitplugin.commander.replacement.ReplacementCommand;
import org.digiplex.bukkitplugin.commander.replacement.ReplacementPair;
import org.digiplex.bukkitplugin.commander.replacement.ReplacementRandom;
import org.digiplex.bukkitplugin.commander.replacement.ReplacementScript;
import org.digiplex.bukkitplugin.commander.replacement.ReplacementString;
import org.digiplex.bukkitplugin.commander.scripting.EchoControl;
import org.digiplex.bukkitplugin.commander.scripting.ScriptBlock;
import org.digiplex.bukkitplugin.commander.scripting.ScriptEnvironment;
import org.digiplex.bukkitplugin.commander.scripting.ScriptParser;
import org.digiplex.bukkitplugin.commander.scripting.env.GameEnvironment;
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
		defaultOptions.set("options.commands.null", "commander null"); //hidden from config file
		
		defaultOptions.set("log.player.commands", false);
		defaultOptions.set("log.player.chat", false);
		
		defaultOptions.set("files.playercmd",  "playercmd.txt");
		defaultOptions.set("files.playerchat", "playerchat.txt");
		defaultOptions.set("files.consolecmd", "consolecmd.txt");
		defaultOptions.set("files.script-files", Arrays.asList("scripts.txt"));
		
		currentOptions = new MemoryConfiguration(defaultOptions);
	}
	
	public static Configuration getConfig() { return getInstance().currentOptions; }
	
	public void setConfig(Configuration config) {
		this.currentOptions = config;
		config.setDefaults(defaultOptions);
	}
	
	public void reload(){
		//clear all saved states in the engine
		currentOptions = new MemoryConfiguration(defaultOptions);
		
		aliasedScripts.clear();
	}
	
	//Valid types for variables and collections
	public static final Class<?>[] VALID_VAR_TYPES = new Class<?>[] {
		String.class, Integer.class, Boolean.class, List.class,
	};
	public static final Class<?>[] VALID_COLL_TYPES = new Class<?>[] {
		String.class,
	};
	
	//////////////////////////////////////////////////////////////////////////////////////////
	// Parser
	public enum MatchingContext {
		Chat, Command,
	}
	public List<ReplacementPair> loadReplacementListFromFile(File listfile, MatchingContext context) {
		ArrayList<ReplacementPair> pairs = new ArrayList<ReplacementPair>();
		BufferedReader br = null;
		try {
			if (!listfile.canRead()){ throw new FileNotFoundException(); }
			
			br = new BufferedReader(new FileReader(listfile));
			/* Explination of Regex:
			 *  /hello/o =m=> [opt,opt2] world
			 * \/(.+)\/(\w*)\s*=(\w?)=([>{])\s*(?:\[([^\])]+\])?\s*(.*)
			 *   \--/   \-/      \-/   \--/         \----/     \-/
			 *     |     |        |      |            |       Replacement String/Script Alias
			 *     |     |        |      |        Replacement Options
			 *     |     |        |    Script or Single Mode
			 *     |     |      Method Character
			 *     |   Regex Option Chatacters
			 *   Regex
			 */
			Pattern p = Pattern.compile("\\/(.+)\\/(\\w*)\\s*=(\\w?)=([>{])\\s*(?:\\[([^\\]]+)\\])?\\s*(.*)");
			String line;
			
			int success = 0, lineno = 0;
			while ((line = br.readLine()) != null) {
				lineno++;
				if (line.isEmpty() || line.startsWith("#")) continue;
				Matcher m = p.matcher(line);
				if (m.matches()) {
					success++;
					
					String regex = m.group(1);
					String opts = m.group(2);
					String methodstr = m.group(3);
					String scriptmode = m.group(4);
					String replopts = m.group(5); 
					String repl = m.group(6);
					//Log.info("line: "+line+" > "+regex+" ==> "+repl);
					
					ReplacementPair rp = null;
					if (scriptmode.equals(">")){
						char method = ' ';
						if (!methodstr.isEmpty()) method = methodstr.charAt(0);
						switch (method){
						case 'c': //command method - to force command instead of chat replacement
							rp = new ReplacementCommand(regex, repl, replopts); break;
						case 'r': //random method - choose from one of the ; seperated list
							if (context != MatchingContext.Chat) {
								Log.warning("Random method replacements are not allowed anywhere but chat-matching contexts! Ignoring. Line "+lineno);
								continue;
							}
							rp = new ReplacementRandom(regex, repl); break;
						case ' ':
						default:
							switch (context){
							case Command:
								rp = new ReplacementCommand(regex, repl, replopts); break;
							case Chat: //only use ReplacementString for chat
								rp = new ReplacementString(regex, repl); break;
							}
							break;
						}
						
					} else if (scriptmode.equals("{")){
						int blockDepth = 1;
						ArrayList<String> scriptblock = new ArrayList<String>();
						
						while ((line = br.readLine()) != null){
							//loop through input lines to get the script block
							lineno++;
							if (line.isEmpty() || line.startsWith("#")) continue;
							
							if (line.trim().equals("{")){
								blockDepth++;
							} else if (line.trim().equals("}")) {
								blockDepth--;
							} 
							if (blockDepth == 0){
								ScriptBlock block = (ScriptBlock)ScriptParser.parseScript(scriptblock);//new ScriptBlock(scriptblock, repl);
								block.setAlias(repl);
								rp = new ReplacementScript(regex, block);
								CommanderEngine.getInstance().setScriptForAlias(repl, block);
								break;
							} else {
								scriptblock.add(line);
							}
						}
						if (blockDepth != 0){
							//if the above loop broke without completing the script, it hit the end of line
							throw new BadScriptException("EOF reached before end of script reached! Please re-balance braces!");
						}
					}
					rp.setRegexOptions(opts);
					pairs.add(rp);
				} else {
					Log.warning("[Commander] Line "+lineno+" is badly formatted. Ignoring.");
				}
			}
			Log.info("[Commander] Successfully imported "+success+" patterns from "+listfile.getName());
		} catch (BadScriptException e) {
			Log.severe("[Commander] Error reading replacement file "+listfile.getName()+" : "+e.getMessage());
		} catch (FileNotFoundException ex){
			Log.warning("[Commander] Could not open replacement file: "+listfile.getName());
		} catch (IOException e) {
			Log.log(Level.WARNING, "[Commander] IOException thrown while parsing replacement file "+listfile.getName(), e);
		} finally {
			try { if (br != null) br.close(); } catch (IOException e) {}
		}
		return pairs;
	}
	
	public void loadScriptsFromFile(File listfile) {
		BufferedReader br = null;
		try {
			if (!listfile.canRead()){ throw new FileNotFoundException(); }
			
			br = new BufferedReader(new FileReader(listfile));
			Pattern p = Pattern.compile("{\\s*(.*)");
			String line;
			
			int success = 0, lineno = 0; int err = 0;
			while ((line = br.readLine()) != null) {
				lineno++;
				if (line.isEmpty() || line.startsWith("#")) continue;
				Matcher m = p.matcher(line);
				if (m.matches()) {
					success++;
					
					String name = m.group(1);
					
					{
						int blockDepth = 1;
						ArrayList<String> scriptblock = new ArrayList<String>();
						
						while ((line = br.readLine()) != null){
							//loop through input lines to get the script block
							lineno++;
							if (line.isEmpty() || line.startsWith("#")) continue;
							
							if (line.trim().endsWith("{") && !line.trim().endsWith("\\{")){
								blockDepth++;
							} else if (line.trim().equals("}")) {
								blockDepth--;
							} 
							if (blockDepth == 0){
								if (name == null || name.isEmpty()) {
									Log.warning("Found a script block with no alias!"+ ((err == 0)?" All scripts in script files must have an alias! No way to call them otherwise!":"")+" Skipping parsing.");
									err++;
									//if we can't store it, don't even bother parsing it.
									break;
								}
								ScriptBlock block = (ScriptBlock)ScriptParser.parseScript(scriptblock);//new ScriptBlock(scriptblock, repl);
								block.setAlias(name);
								CommanderEngine.getInstance().setScriptForAlias(name, block);
								break;
							} else {
								scriptblock.add(line);
							}
						}
						if (blockDepth != 0){
							//if the above loop broke without completing the script, it hit the end of line
							throw new BadScriptException("EOF reached before end of script reached! Please re-balance braces!");
						}
					}
				} else {
					Log.warning("[Commander] Line "+lineno+" does not start a script block. Only named blocks of script, not loose code, are allowed in script files.");
				}
			}
			Log.info("[Commander] Successfully imported "+success+" scripts from "+listfile.getName());
		} catch (BadScriptException e) {
			Log.severe("[Commander] Error reading replacement file "+listfile.getName()+" : "+e.getMessage());
		} catch (FileNotFoundException ex){
			Log.warning("[Commander] Could not open replacement file: "+listfile.getName());
		} catch (IOException e) {
			Log.log(Level.WARNING, "[Commander] IOException thrown while parsing replacement file "+listfile.getName(), e);
		} finally {
			try { if (br != null) br.close(); } catch (IOException e) {}
		}
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
	public AdminCommand getAdminCommandExecutor() { return new AdminCommand(); }
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
