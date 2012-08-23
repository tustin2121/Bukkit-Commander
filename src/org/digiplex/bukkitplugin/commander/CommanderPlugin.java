package org.digiplex.bukkitplugin.commander;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.digiplex.bukkitplugin.commander.CommanderEngine.EchoCommand;
import org.digiplex.bukkitplugin.commander.api.CmdrEnvVarModule;
import org.digiplex.bukkitplugin.commander.module.ConsoleCommandModule;
import org.digiplex.bukkitplugin.commander.module.PlayerChatModule;
import org.digiplex.bukkitplugin.commander.module.PlayerCommandModule;
import org.digiplex.bukkitplugin.commander.replacement.ReplacementPair;
import org.digiplex.bukkitplugin.commander.scripting.env.GameEnvironment;

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
		Log.info("Reload Complete!");
	}
	
	public void addCommanderScriptHook(String namespace, CmdrEnvVarModule hook) {
		GameEnvironment.registerCommanderPlugin(namespace, hook);
	}
	
	public void loadLists(){
		try {
			List<ReplacementPair> rps;
			
			File playerCommandFile = new File(this.getDataFolder(), config.getString("files.playercmd"));
			checkListFile(playerCommandFile, "playercmd.txt");
			rps = CommanderEngine.getInstance().loadReplacementListFromFile(playerCommandFile, pcmd.getMatchingContext());
			pcmd.addReplacementPairs(rps);
			
			File playerChatFile = new File(this.getDataFolder(), config.getString("files.playerchat"));
			checkListFile(playerChatFile, "playerchat.txt");
			rps = CommanderEngine.getInstance().loadReplacementListFromFile(playerChatFile, pchat.getMatchingContext());
			pchat.addReplacementPairs(rps);
			
			File consoleCommandFile = new File(this.getDataFolder(), config.getString("files.consolecmd"));
			checkListFile(consoleCommandFile, "consolecmd.txt");
			rps = CommanderEngine.getInstance().loadReplacementListFromFile(consoleCommandFile, ccmd.getMatchingContext());
			ccmd.addReplacementPairs(rps);
			
			//load additional script files here
			List<String> lst = config.getStringList("files.script-files");
			for (String filename : lst) {
				File scriptfile = new File(this.getDataFolder(), filename);
				if (!scriptfile.exists()) {
					Log.warning("Script file \""+filename+"\" does not exist!");
					continue;
				} else {
					//TODO
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

	
	////////////////////////////////////////////////
	
	
}
