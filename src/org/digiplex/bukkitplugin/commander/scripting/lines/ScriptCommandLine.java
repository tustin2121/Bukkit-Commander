package org.digiplex.bukkitplugin.commander.scripting.lines;

import static org.digiplex.bukkitplugin.commander.CommanderEngine.printDebug;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandException;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.Plugin;
import org.digiplex.bukkitplugin.commander.CommanderEngine;
import org.digiplex.bukkitplugin.commander.scripting.ScriptEnvironment;
import org.digiplex.bukkitplugin.commander.scripting.exceptions.BadScriptException;
import org.digiplex.bukkitplugin.commander.scripting.exceptions.CommandExecutionException;

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
	
	@Override public void execute(ScriptEnvironment env) throws BadScriptException {
		String command = env.substituteTokens(cmd);
//		if (env.getMatch() != null)
//			command = env.getMatch().replaceFirst(command);
		
		printDebug("command", "%s", command);
		
		try {
			boolean found;
			if (modCommand)
				found = env.getServer().dispatchCommand(CommanderEngine.ccs, command);
			else
				found = env.getServer().dispatchCommand(env.getCommandSender(), command);
			
			env.setCommandResults(found);
		} catch (CommandException ex) {
			printDebug("command", "Command has thrown exception. I am set to %s on error.", env.shouldContinueOnError()?"CONTINUE":"THROW");
			
			CommandExecutionException cex = null;
			{
				int idx = command.indexOf(' ');
				String cmd = (idx > 0)? command.substring(idx) : command;
				PluginCommand pc = Bukkit.getPluginCommand(cmd);
				if (pc != null) {
					Plugin p = pc.getPlugin();
					if (p != null)
						cex = new CommandExecutionException(String.format("\"%s\" from the plugin \"%s\"", command, p.getName()), ex);
				}
				
				if (cex == null)
					cex = new CommandExecutionException("\""+command+"\"", ex);
				
			}
			
			env.setCommandResultsError(ex);
			if (!env.shouldContinueOnError()) throw cex;
			//CommanderEngine.Log.log(Level.SEVERE, "Error from command while processing script! Command=\""+command+"\"\n", ex);
			CommanderEngine.reportCommandException(cex, false); //report it despite continuing
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
