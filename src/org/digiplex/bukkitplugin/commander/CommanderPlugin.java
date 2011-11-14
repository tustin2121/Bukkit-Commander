package org.digiplex.bukkitplugin.commander;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.digiplex.bukkitplugin.commander.module.ConsoleCommandModule;
import org.digiplex.bukkitplugin.commander.module.Module;
import org.digiplex.bukkitplugin.commander.module.PlayerChatModule;
import org.digiplex.bukkitplugin.commander.module.PlayerCommandModule;

public class CommanderPlugin extends JavaPlugin {
	public static final Logger Log = Logger.getLogger("Minecraft");
	public FileConfiguration config;
	
	PlayerCommandModule pcmd = null;
	PlayerChatModule pchat = null;
	ConsoleCommandModule ccmd = null;
	
	@Override public void onDisable() {
		Log.info("[Commander] Disabled");
	}

	@Override public void onEnable() {
		config = this.getConfig();
		config.options().copyDefaults(true);
		this.saveConfig();
		
		PluginManager pm = this.getServer().getPluginManager();
		
		this.getCommand("commander").setExecutor(new AdminCommand());
		
		pcmd = new PlayerCommandModule();
		pm.registerEvent(Type.PLAYER_COMMAND_PREPROCESS, pcmd, Priority.High, this);
		pcmd.echoCmds = config.getBoolean("log.player.commands");
		
		pchat = new PlayerChatModule();
		pm.registerEvent(Type.PLAYER_CHAT, pchat, Priority.Normal, this);
		
		ccmd = new ConsoleCommandModule();
		pm.registerEvent(Type.SERVER_COMMAND, ccmd, Priority.High, this);
		
		loadLists();
		
		Log.info("[Commander] Enabled");
	}
	
	public void reload(){
		this.reloadConfig();
		
		pcmd.echoCmds = config.getBoolean("log.player.commands");
		pcmd.clearReplacementPairs();
		
		pchat.clearReplacementPairs();
		
		ccmd.clearReplacementPairs();
		
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

	public void loadListFromFile(File listfile, Module module) {
		BufferedReader br = null;
		try {
			if (!listfile.canRead()){ throw new FileNotFoundException(); }
			
			br = new BufferedReader(new FileReader(listfile));
			Pattern p = Pattern.compile("\\/(.+)\\/(\\w*)\\s*==>\\s*(.*)");
			String line;
			
			int success = 0, lineno = 0;
			while ((line = br.readLine()) != null) {
				lineno++;
				if (line.isEmpty() || line.startsWith("#")) continue;
				Matcher m = p.matcher(line);
				if (m.matches()) {
					success++;
					
					String regex = m.group(1);
					//String opts = m.group(2);
					String repl = m.group(3);
					//Log.info("line: "+line+" > "+regex+" ==> "+repl);
					
					ReplacementPair rp = new ReplacementPair(regex, repl);
					module.addReplacementPair(rp);
				} else {
					Log.warning("Line "+lineno+" is badly formatted. Ignoring.");
				}
			}
			Log.info("Successfully imported "+success+" patterns from "+listfile.getName());
		} catch (FileNotFoundException ex){
			Log.warning("Could not open replacement file: "+listfile.getName());
		} catch (IOException e) {
			Log.log(Level.WARNING, "IOException thrown while parsing replacement file "+listfile.getName(), e);
		} finally {
			try { if (br != null) br.close(); } catch (IOException e) {}
		}
	}
	
	public class AdminCommand implements CommandExecutor {
		@Override public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
			if (sender instanceof Player) return false;
			if (args[0].equalsIgnoreCase("reload")){
				reload();
				return true;
			}
			return false;
		}
	}
	
}
