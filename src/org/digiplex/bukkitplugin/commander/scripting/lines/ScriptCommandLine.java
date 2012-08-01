package org.digiplex.bukkitplugin.commander.scripting.lines;

import org.digiplex.bukkitplugin.commander.CommanderPlugin;
import org.digiplex.bukkitplugin.commander.scripting.BadScriptException;
import org.digiplex.bukkitplugin.commander.scripting.ScriptEnvironment;

public class ScriptCommandLine extends ScriptLine {
	public String cmd;
	public boolean modCommand;
	
	public ScriptCommandLine(String command) {
		if (command.trim().startsWith("sudo")) { //allow "sudo" commands to be run by the console
			modCommand = true;
			this.cmd = command.trim()
					.replaceFirst("(?<!\\\\)sudo", "").replaceFirst("\\sudo", "sudo"); //remove sudo, unless it has a \ in front
					//this is a negative look-back, see ReplacementString
		} else {
			this.cmd = command;
		}
		this.cmd = this.cmd.trim();
	}
	
	@Override public void execute(ScriptEnvironment env) {
		String command = env.substituteTokens(cmd);
//		if (env.getMatch() != null)
//			command = env.getMatch().replaceFirst(command);
		
		if (CommanderPlugin.instance.scriptDebugMode)
			CommanderPlugin.Log.info("[Commander:DEBUG:line] "+command);
		
		if (modCommand)
			env.getServer().dispatchCommand(CommanderPlugin.ccs, command);
		//	env.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
		else
			env.getServer().dispatchCommand(env.getCommandSender(), command);
	}
	
	@Override public String toString() {
		return "Command["+cmd+"]";
	}
	
	@Override public boolean isConstruct() {return false;}
	@Override public boolean isDirective() {return false;}

	@Override public boolean requiresNextLine() {return false;}
	@Override public boolean requiresPreviousConstruct() {return false;}
	
	@Override public void verify() throws BadScriptException {
		if (cmd == null)
			throw new BadScriptException("Null Command line!", lineno);
	}
}
