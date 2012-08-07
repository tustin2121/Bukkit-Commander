package org.digiplex.bukkitplugin.commander.api;

import java.util.regex.MatchResult;

import org.bukkit.command.CommandSender;
import org.digiplex.bukkitplugin.commander.scripting.Executable;
import org.digiplex.bukkitplugin.commander.scripting.ScriptEnvironment;
import org.digiplex.bukkitplugin.commander.scripting.exceptions.BadScriptException;
import org.digiplex.bukkitplugin.commander.scripting.exceptions.BreakScriptException;

/**
 * An opaque class that represents a script that is ready to be run by the commander scripting engine.
 * To run a script block, one needs only call one of the execute() functions, and Commander will do
 * the rest, running the script. There are no return codes from Commander Scripts, but use of the
 * getLastRunVariable() function will allow access to any global variables that were set in the last
 * run of the script.
 * @author tpittman
 */
public class CommanderScript {
	Executable block;
	private ScriptEnvironment lastRunEnv;
	
	public void execute(CommandSender sender, MatchResult cmdMatcher) throws ScriptExecutionException {
		lastRunEnv = new ScriptEnvironment(); {
			lastRunEnv.setCommandSender(sender);
			lastRunEnv.setServer(sender.getServer());
			lastRunEnv.setMatch(cmdMatcher);
		}
		
		try {
			block.execute(lastRunEnv);
		} catch (BreakScriptException ex) {
			//do nothing
		} catch (BadScriptException ex) {
			throw new ScriptExecutionException("Error while executing script!", ex);
		}
	} 
	
	public void execute(CommandSender sender) throws ScriptExecutionException {
		lastRunEnv = new ScriptEnvironment(); {
			lastRunEnv.setCommandSender(sender);
			lastRunEnv.setServer(sender.getServer());
		}
		
		try {
			block.execute(lastRunEnv);
		} catch (BreakScriptException ex) {
			//do nothing
		} catch (BadScriptException ex) {
			throw new ScriptExecutionException("Error while executing script!", ex);
		}
	}
	
	public Object getLastRunVariable(String varname) {
		return lastRunEnv.getVariableValue(varname);
	}
}
