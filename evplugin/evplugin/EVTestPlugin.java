package evplugin;

import java.util.logging.Logger;

import org.bukkit.plugin.java.JavaPlugin;
import org.digiplex.bukkitplugin.commander.api.CommanderAPI;

public class EVTestPlugin extends JavaPlugin {
	//public static final Logger Log = Logger.getLogger("Minecraft");
	EVTestEVM evm = new EVTestEVM();
	
	@Override public void onEnable() {
		CommanderAPI.registerEVM(evm);
		
		System.out.println("[EVTest] Enabled");
	}
	
	@Override public void onDisable() {
		CommanderAPI.unregisterEVM(evm);
		System.out.println("[EVTest] Disabled");
	}
	
	
}
