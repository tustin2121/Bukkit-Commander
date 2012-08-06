package commander.test.placeholders;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.help.HelpMap;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.map.MapView;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.ServicesManager;
import org.bukkit.plugin.messaging.Messenger;
import org.bukkit.scheduler.BukkitScheduler;

import com.avaje.ebean.config.ServerConfig;

public class TestServer implements Server {
	public static final Logger LOG = Logger.getLogger("TestServer");
	public TestPluginManager pluginManager;
	
	{
		Handler[] hs = Logger.getLogger("").getHandlers();
		for (Handler h : hs) {
			h.setFormatter(new Formatter() {
				@Override public String format(LogRecord r) {
					return "["+r.getLoggerName()+"] ["+r.getLevel().getName()+"] " + r.getMessage()+"\n";
				}
			});
		}
	}
	
	private ArrayList<String> issuedCommands = new ArrayList<String>();
	
	public void clearCommands() {
		issuedCommands.clear();
	}
	
	public boolean checkCommands(String... cmds) {
		try {
			if (cmds.length != issuedCommands.size()) return false;
			
			for (int i = 0; i < cmds.length; i++) {
				String cmd = cmds[i];
				String isc = issuedCommands.get(i);
				if (!isc.equals(cmd)) return false;
			}
			return true;
		} finally {
		//	issuedCommands.clear();
		}
	}
	
	ArrayList<Player> playerList = new ArrayList<Player>();
	
	public void addPlayer(Player player) {
		playerList.add(player);
	}
	
	/////////////////////////////////// Relevant Methods //////////////////////////////////////
	
	@Override public boolean dispatchCommand(CommandSender sender, String commandLine) throws CommandException {
		System.out.println("[TestServer] dispatchCommand() : "+commandLine);
		issuedCommands.add(commandLine);
		if (commandLine.contains("ec"))
			sender.sendMessage("Echo from '"+commandLine+"'");
		if (commandLine.contains("bc"))
			broadcastMessage("Broadcast from '"+commandLine+"'");
		return true;
	}
	
	@Override public Player getPlayer(String name) {
		for (Player p : playerList) {
			if (p.getName().equalsIgnoreCase(name)) return p;
		}
		return null;
	}

	@Override public Player getPlayerExact(String name) {
		for (Player p : playerList) {
			if (p.getName().equals(name)) return p;
		}
		return null;
	}

	@Override public List<Player> matchPlayer(String name) {
		ArrayList<Player> matched = new ArrayList<Player>();
		for (Player p : playerList) {
			if (p.getName().matches(name))
				matched.add(p);
		}
		return matched;
	}
	
	@Override public PluginManager getPluginManager() {
		if (pluginManager == null)
			pluginManager = new TestPluginManager(this);
		return pluginManager;
	}
	
	@Override public Logger getLogger() {
		return LOG;
	}
	
	
	@Override public int broadcast(String message, String permission) {
		System.out.println("[TestServer] broadcast() : "+message);
		return 1;
	}
	@Override public int broadcastMessage(String message) {
		System.out.println("[TestServer] broadcastMessage() : "+message);
		return 1;
	}
	
	@Override public PluginCommand getPluginCommand(String name) {
		this.getPluginManager(); //instantiates plugin manager is not already
		try {
			Constructor<PluginCommand> c = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
			c.setAccessible(true);
			return c.newInstance(name, pluginManager.myplugin);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override public Player[] getOnlinePlayers() {
		return playerList.toArray(new Player[playerList.size()]);
	}
	
	@Override public OfflinePlayer[] getOfflinePlayers() {
		return null;
	}
	
	////////////////////////////////// Irrelevant Methods //////////////////////////////////////

	@Override public void sendPluginMessage(Plugin source, String channel, byte[] message) {}

	@Override public Set<String> getListeningPluginChannels() {
		return null;
	}

	@Override public String getName() {
		return null;
	}

	@Override public String getVersion() {
		return null;
	}

	@Override public String getBukkitVersion() {
		return null;
	}

	@Override public int getMaxPlayers() {
		return 0;
	}

	@Override public int getPort() {
		return 0;
	}

	@Override public int getViewDistance() {
		return 0;
	}

	@Override public String getIp() {
		return null;
	}

	@Override public String getServerName() {
		return null;
	}

	@Override public String getServerId() {
		return null;
	}

	@Override public String getWorldType() {
		return null;
	}

	@Override public boolean getGenerateStructures() {
		return false;
	}

	@Override public boolean getAllowEnd() {
		return false;
	}

	@Override public boolean getAllowNether() {
		return false;
	}

	@Override public boolean hasWhitelist() {
		return false;
	}

	@Override public void setWhitelist(boolean value) {}

	@Override public Set<OfflinePlayer> getWhitelistedPlayers() {
		return null;
	}

	@Override public void reloadWhitelist() {}

	@Override public String getUpdateFolder() {
		return null;
	}

	@Override public File getUpdateFolderFile() {
		return null;
	}

	@Override public long getConnectionThrottle() {
		return 0;
	}

	@Override public int getTicksPerAnimalSpawns() {
		return 0;
	}

	@Override public int getTicksPerMonsterSpawns() {
		return 0;
	}

	@Override public BukkitScheduler getScheduler() {
		return null;
	}

	@Override public ServicesManager getServicesManager() {
		return null;
	}

	@Override public List<World> getWorlds() {
		return null;
	}

	@Override public World createWorld(WorldCreator creator) {
		return null;
	}

	@Override public boolean unloadWorld(String name, boolean save) {
		return false;
	}

	@Override public boolean unloadWorld(World world, boolean save) {
		return false;
	}

	@Override public World getWorld(String name) {
		return null;
	}

	@Override public World getWorld(UUID uid) {
		return null;
	}

	@Override public MapView getMap(short id) {
		return null;
	}

	@Override public MapView createMap(World world) {
		return null;
	}

	@Override public void reload() {}

	@Override public void savePlayers() {}

	@Override public void configureDbConfig(ServerConfig config) {}

	@Override public boolean addRecipe(Recipe recipe) {
		return false;
	}

	@Override public List<Recipe> getRecipesFor(ItemStack result) {
		return null;
	}

	@Override public Iterator<Recipe> recipeIterator() {
		return null;
	}

	@Override public void clearRecipes() {}

	@Override public void resetRecipes() {}

	@Override public Map<String, String[]> getCommandAliases() {
		return null;
	}

	@Override public int getSpawnRadius() {
		return 0;
	}

	@Override public void setSpawnRadius(int value) {}

	@Override public boolean getOnlineMode() {
		return false;
	}

	@Override public boolean getAllowFlight() {
		return false;
	}

	@Override public boolean useExactLoginLocation() {
		return false;
	}

	@Override public void shutdown() {}

	@Override public OfflinePlayer getOfflinePlayer(String name) {
		return null;
	}

	@Override public Set<String> getIPBans() {
		return null;
	}

	@Override public void banIP(String address) {}

	@Override public void unbanIP(String address) {}

	@Override public Set<OfflinePlayer> getBannedPlayers() {
		return null;
	}

	@Override public Set<OfflinePlayer> getOperators() {
		return null;
	}

	@Override public GameMode getDefaultGameMode() {
		return null;
	}

	@Override public void setDefaultGameMode(GameMode mode) {}

	@Override public ConsoleCommandSender getConsoleSender() {
		return null;
	}

	@Override public File getWorldContainer() {
		return null;
	}

	@Override public Messenger getMessenger() {
		return null;
	}

	@Override public HelpMap getHelpMap() {
		return null;
	}

	@Override public Inventory createInventory(InventoryHolder owner, InventoryType type) {
		return null;
	}

	@Override public Inventory createInventory(InventoryHolder owner, int size) {
		return null;
	}

	@Override public Inventory createInventory(InventoryHolder owner, int size, String title) {
		return null;
	}

	@Override public int getMonsterSpawnLimit() {
		return 0;
	}

	@Override public int getAnimalSpawnLimit() {
		return 0;
	}

	@Override public int getWaterAnimalSpawnLimit() {
		return 0;
	}

	@Override public boolean isPrimaryThread() {
		return false;
	}

	@Override public String getMotd() {
		return null;
	}

}
