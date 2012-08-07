package org.digiplex.bukkitplugin.commander;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.digiplex.bukkitplugin.commander.CommanderEngine.EchoCommand;
import org.digiplex.bukkitplugin.commander.api.CommanderEnvVarModule;
import org.digiplex.bukkitplugin.commander.module.ConsoleCommandModule;
import org.digiplex.bukkitplugin.commander.module.Module;
import org.digiplex.bukkitplugin.commander.module.PlayerChatModule;
import org.digiplex.bukkitplugin.commander.module.PlayerCommandModule;
import org.digiplex.bukkitplugin.commander.replacement.ReplacementCommand;
import org.digiplex.bukkitplugin.commander.replacement.ReplacementPair;
import org.digiplex.bukkitplugin.commander.replacement.ReplacementRandom;
import org.digiplex.bukkitplugin.commander.replacement.ReplacementScript;
import org.digiplex.bukkitplugin.commander.replacement.ReplacementString;
import org.digiplex.bukkitplugin.commander.scripting.ScriptBlock;
import org.digiplex.bukkitplugin.commander.scripting.ScriptParser;
import org.digiplex.bukkitplugin.commander.scripting.env.GameEnvironment;
import org.digiplex.bukkitplugin.commander.scripting.exceptions.BadScriptException;

public class CommanderPlugin extends JavaPlugin {
	public static final Logger Log = Logger.getLogger("Minecraft");
	
	public FileConfiguration config;
	
	PlayerCommandModule pcmd = null;
	PlayerChatModule pchat = null;
	ConsoleCommandModule ccmd = null;
	
	@Override public String toString() { return "CommanderPlugin"; }
	
	@Override public void onDisable() {
		CommanderEngine.unregisterInstance();
		Log.info("[Commander] Disabled");
	}

	@Override public void onEnable() {
		CommanderEngine.registerInstance(); //creates an instance
		CommanderEngine instance = CommanderEngine.getInstance();
		
		config = this.getConfig();
		config.options().copyDefaults(true);
		this.saveConfig();
		instance.setConfig(config);
		
		copyReferenceFile();
		
		PluginManager pm = this.getServer().getPluginManager();
		
		this.getCommand("commander").setExecutor(instance.new AdminCommand());
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
		
		CommanderEngine.getInstance().reload(); //probably not good if being used by more than 1 plugin
		
		loadLists();
	}
	
	public void addCommanderScriptHook(String namespace, CommanderEnvVarModule hook) {
		GameEnvironment.registerCommanderPlugin(namespace, hook);
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
			
			//load additional script files here
			List<String> lst = config.getStringList("files.script-files");
			for (String filename : lst) {
				File scriptfile = new File(this.getDataFolder(), filename);
				if (!scriptfile.exists()) {
					Log.warning("Script file \""+filename+"\" does not exist!");
					continue;
				} else {
					
				}
			}
			
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
	
	////////////////////////////////////////////////
	
	
}
