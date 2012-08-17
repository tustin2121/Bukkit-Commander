package org.digiplex.bukkitplugin.commander.scripting.lines;

import java.util.logging.Level;

import org.bukkit.command.CommandException;
import org.digiplex.bukkitplugin.commander.CommanderEngine;
import org.digiplex.bukkitplugin.commander.scripting.ScriptEnvironment;
import org.digiplex.bukkitplugin.commander.scripting.exceptions.BadScriptException;

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
		
		if (CommanderEngine.getInstance().scriptDebugMode)
			CommanderEngine.Log.info("[Commander:DEBUG:line] "+command);
		
		try {
			boolean found;
			if (modCommand)
				found = env.getServer().dispatchCommand(CommanderEngine.ccs, command);
			else
				found = env.getServer().dispatchCommand(env.getCommandSender(), command);
			
			env.setCommandResults(found);
		} catch (CommandException ex) {
			env.setCommandError(ex);
			if (!env.shouldContinueOnError()) throw ex;
			CommanderEngine.Log.log(Level.SEVERE, "Error from command while processing script! Command=\""+command+"\"\n", ex);
		}
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
