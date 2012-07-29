package commander.test.placeholders;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Server;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.permissions.Permissible;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.InvalidPluginException;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.UnknownDependencyException;
import org.bukkit.plugin.java.JavaPlugin;
import org.digiplex.bukkitplugin.commander.CommanderPlugin;

public class TestPluginManager implements PluginManager {
	public static final String COMMANDER = "Commander";
	public CommanderPlugin myplugin = null;
	private Server server;
	
	public TestPluginManager(Server server) {
		this.server = server;
	}

	/////////////////////////////////// Relevant Methods //////////////////////////////////////
	
	@Override public Plugin getPlugin(String name) {
		System.out.println("[TestPluginManager] getPlugin() : "+name);
		if (name.equals(COMMANDER)) return myplugin; 
		return null;
	}

	@Override public Plugin[] getPlugins() {
		System.out.println("[TestPluginManager] getPlugins()");
		return new Plugin[] { myplugin };
	}

	@Override public boolean isPluginEnabled(String name) {
		System.out.println("[TestPluginManager] isPluginEnabled(string) : "+name);
		if (name.equals(COMMANDER)) return true; 
		return false;
	}

	@Override public boolean isPluginEnabled(Plugin plugin) {
		System.out.println("[TestPluginManager] isPluginEnabled(plugin) : "+plugin.toString());
		if (plugin == myplugin) return true; 
		return false;
	}
	
	@Override public void registerEvents(Listener listener, Plugin plugin) {
		System.out.println("[TestPluginManager] registerEvents(2) : " + listener.toString());
	}
	@Override public void registerEvent(Class<? extends Event> event,
			Listener listener, EventPriority priority, EventExecutor executor,
			Plugin plugin) {
		System.out.println("[TestPluginManager] registerEvents(5) : " + listener.toString());
	}
	@Override public void registerEvent(Class<? extends Event> event,
			Listener listener, EventPriority priority, EventExecutor executor,
			Plugin plugin, boolean ignoreCancelled) {
		System.out.println("[TestPluginManager] registerEvents(6) : " + listener.toString());
	}

	
	@Override public Plugin loadPlugin(File file)
			throws InvalidPluginException, InvalidDescriptionException,
			UnknownDependencyException {
		System.out.println("[TestPluginManager] loadPlugin() : " + file.toString());
		myplugin.onLoad();
		return myplugin;
	}
	
	@Override public void enablePlugin(Plugin plugin) {
		System.out.println("[TestPluginManager] enablePlugin() ");
		if (!(plugin instanceof CommanderPlugin)) return;
		
		myplugin = (CommanderPlugin) plugin;
		
		//initialize(PluginLoader loader, Server server, PluginDescriptionFile description, File dataFolder, File file, ClassLoader classLoader)
		try {
			Method m = JavaPlugin.class.getDeclaredMethod("initialize", PluginLoader.class, Server.class, PluginDescriptionFile.class, File.class, File.class, ClassLoader.class);
			m.setAccessible(true);
			m.invoke(myplugin, 
					null, //PluginLoader
					server, //Server
					new PluginDescriptionFile(COMMANDER, "Test", CommanderPlugin.class.getPackage().getName()+"."+CommanderPlugin.class.getName()),
					new File(System.getProperty("user.dir")+"\\TestData"), //datafolder
					new File(System.getProperty("user.dir")+"\\TestData\\config.yml"), //config
					this.getClass().getClassLoader()); //classloader
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		
		myplugin.onEnable();
	}

	@Override public void disablePlugin(Plugin plugin) {
		System.out.println("[TestPluginManager] disablePlugin() ");
		myplugin.onDisable();
	}

	////////////////////////////////// Irrelevant Methods //////////////////////////////////////
	
	@Override public void registerInterface(Class<? extends PluginLoader> loader)
			throws IllegalArgumentException {}

	@Override public Plugin[] loadPlugins(File directory) {
		return null;
	}

	@Override public void disablePlugins() {}

	@Override public void clearPlugins() {}

	@Override public void callEvent(Event event) throws IllegalStateException {}

	@Override public Permission getPermission(String name) {
		return null;
	}

	@Override public void addPermission(Permission perm) {}

	@Override public void removePermission(Permission perm) {}

	@Override public void removePermission(String name) {}

	@Override public Set<Permission> getDefaultPermissions(boolean op) {
		return new HashSet<Permission>();
	}

	@Override public void recalculatePermissionDefaults(Permission perm) {}

	@Override public void subscribeToPermission(String permission, Permissible permissible) {}

	@Override public void unsubscribeFromPermission(String permission, Permissible permissible) {}

	@Override public Set<Permissible> getPermissionSubscriptions(String permission) {
		return new HashSet<Permissible>();
	}

	@Override public void subscribeToDefaultPerms(boolean op, Permissible permissible) {}

	@Override public void unsubscribeFromDefaultPerms(boolean op, Permissible permissible) {}

	@Override public Set<Permissible> getDefaultPermSubscriptions(boolean op) {
		return new HashSet<Permissible>();
	}

	@Override public Set<Permission> getPermissions() {
		return new HashSet<Permission>();
	}

	@Override public boolean useTimings() {
		return false;
	}

}
