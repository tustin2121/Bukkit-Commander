package evplugin;

import org.bukkit.command.CommandSender;
import org.digiplex.bukkitplugin.commander.api.CmdrEnvVarModule;

public class EVTestEVM extends CmdrEnvVarModule {

	@Override public String getNamespace() {
		return "EVTest";
	}

	@Override public Object getEVValue(String varname, CommandSender sender) {
		if (varname.matches("(?i)hello"))
			return "Hello "+sender.getName();
		return null;
	}

}
