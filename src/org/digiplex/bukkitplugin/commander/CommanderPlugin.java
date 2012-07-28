package org.digiplex.bukkitplugin.commander;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.digiplex.bukkitplugin.commander.module.ConsoleCommandModule;
import org.digiplex.bukkitplugin.commander.module.Module;
import org.digiplex.bukkitplugin.commander.module.PlayerChatModule;
import org.digiplex.bukkitplugin.commander.module.PlayerCommandModule;
import org.digiplex.bukkitplugin.commander.replacement.ReplacementCommand;
import org.digiplex.bukkitplugin.commander.replacement.ReplacementPair;
import org.digiplex.bukkitplugin.commander.replacement.ReplacementRandom;
import org.digiplex.bukkitplugin.commander.replacement.ReplacementScript;
import org.digiplex.bukkitplugin.commander.replacement.ReplacementString;
import org.digiplex.bukkitplugin.commander.scripting.BadScriptException;
import org.digiplex.bukkitplugin.commander.scripting.EchoControl;
import org.digiplex.bukkitplugin.commander.scripting.ScriptBlock;
import org.digiplex.bukkitplugin.commander.scripting.ScriptEnvironment;

public class CommanderPlugin extends JavaPlugin {
	public static final Logger Log = Logger.getLogger("Minecraft");
	public static final CommanderCommandSender ccs = new CommanderCommandSender();
	public static CommanderPlugin instance;
	
	public FileConfiguration config;
	
	PlayerCommandModule pcmd = null;
	PlayerChatModule pchat = null;
	ConsoleCommandModule ccmd = null;
	
	HashMap<String, ScriptBlock> aliasedScripts = null;
	
	public boolean scriptDebugMode = false;
	
	@Override public void onDisable() {
		instance = null;
		Log.info("[Commander] Disabled");
	}

	@Override public void onEnable() {
		instance = this;
		
		config = this.getConfig();
		config.options().copyDefaults(true);
		this.saveConfig();
		
		copyReferenceFile();
		
		PluginManager pm = this.getServer().getPluginManager();
		
		this.getCommand("commander").setExecutor(new AdminCommand());
		if (config.getBoolean("options.commands.echo", true))
			this.getCommand("echo").setExecutor(new EchoCommand());
		
		pcmd = new PlayerCommandModule();
		pm.registerEvents(pcmd, this);
		pcmd.echoCmds = config.getBoolean("log.player.commands", false);
		
		pchat = new PlayerChatModule();
		pchat.echoCmds = config.getBoolean("log.player.chat", false);
		pchat.allUpper = config.getBoolean("options.match-uppercase", true);
		pm.registerEvents(pchat, this);
		
		ccmd = new ConsoleCommandModule();
		pm.registerEvents(ccmd, this);
		
		aliasedScripts = new HashMap<String, ScriptBlock>();
		
		loadLists();
		
		Log.info("[Commander] Enabled");
	}
	
	public void reload(){
		this.reloadConfig();
		
		pcmd.echoCmds = config.getBoolean("log.player.commands", false);
		pcmd.clearReplacementPairs();
		
		pchat.echoCmds = config.getBoolean("log.player.chat", false);
		pchat.allUpper = config.getBoolean("options.match-uppercase", true);
		pchat.clearReplacementPairs();
		
		ccmd.clearReplacementPairs();
		
		aliasedScripts.clear();
		
		loadLists();
	}
	
	public void loadLists(){
		try {
			File playerCommandFile = new File(this.getDataFolder(), config.getString("files.playercmd"));
			checkListFile(playerCommandFile, "playercmd.txt");
			loadListFromFile(playerCommandFile, pcmd);
			
			File playerChatFile = new File(this.getDataFolder(), config.getString("files.playerchat"));
			checkListFile(playerChatFile, "playerchat.txt");
			loadListFromFile(playerChatFile, pchat);
			
			File consoleCommandFile = new File(this.getDataFolder(), config.getString("files.consolecmd"));
			checkListFile(consoleCommandFile, "consolecmd.txt");
			loadListFromFile(consoleCommandFile, ccmd);
			
		} finally {}
	}
	
	private void checkListFile(File listfile, String defresource) {
		if (!listfile.exists()){
			Log.info("[Commander] Could not find "+listfile.getName()+", creating default file.");
			try {
				InputStream in = getResource(defresource);
				FileOutputStream out = new FileOutputStream(listfile);
				
				// Transfer bytes from in to out
			    byte[] buf = new byte[1024];
			    int len;
			    while ((len = in.read(buf)) > 0) {
			        out.write(buf, 0, len);
			    }
			    in.close();
			    out.close();
			} catch (IOException ex) {
				Log.log(Level.WARNING, "[Commander] IOException while copying default file to data folder.", ex);
			}
		}
	}
	
	private void copyReferenceFile() {
		String source = "Reference.txt";
		File refFile = new File(this.getDataFolder(), source);
		try {
			InputStream in = getResource(source);
			FileOutputStream out = new FileOutputStream(refFile);
			
			// Transfer bytes from in to out
		    byte[] buf = new byte[1024];
		    int len;
		    while ((len = in.read(buf)) > 0) {
		        out.write(buf, 0, len);
		    }
		    in.close();
		    out.close();
		} catch (IOException ex) {
			Log.log(Level.WARNING, "[Commander] IOException while copying reference file to data folder.", ex);
		}
	}

	public void loadListFromFile(File listfile, Module module) {
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
							if (module.getMatchingContext() != Module.MatchingContext.Chat) {
								Log.warning("Random method replacements are not allowed anywhere but chat-matching contexts! Ignoring. Line "+lineno);
								continue;
							}
							rp = new ReplacementRandom(regex, repl); break;
						case ' ':
						default:
							switch (module.getMatchingContext()){
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
								ScriptBlock block = new ScriptBlock(scriptblock, repl);
								rp = new ReplacementScript(regex, block);
								setScriptForAlias(repl, block);
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
					module.addReplacementPair(rp);
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
	}
	
	////////////////////////////////////////////////
	
	public static void setScriptForAlias(String alias, ScriptBlock script){
		if (alias == null || alias.isEmpty()) return;
		instance.aliasedScripts.put(alias, script);
	}
	public static ScriptBlock getScript(String alias){
		return instance.aliasedScripts.get(alias);
	}
	
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
				return true;
			}
			return false;
		}
	}
	
	public class EchoCommand implements CommandExecutor {
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
