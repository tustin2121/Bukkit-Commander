package org.digiplex.bukkitplugin.commander.api;

import org.digiplex.bukkitplugin.commander.CommanderEngine;
import org.digiplex.bukkitplugin.commander.scripting.Executable;
import org.digiplex.bukkitplugin.commander.scripting.env.GameEnvironment;


public class CommanderAPI {
	
	private CommanderAPI(){}
	
	public static void registerEVM(CommanderEnvVarModule evm) {
		GameEnvironment.registerCommanderPlugin(evm.getNamespace(), evm);
	}
	
	public static CommanderScript getScript(String name) {
		Executable exe = CommanderEngine.getInstance().getScript(name);
		
		CommanderScript script = new CommanderScript();
		script.block = exe;
		return script;
	}
}
