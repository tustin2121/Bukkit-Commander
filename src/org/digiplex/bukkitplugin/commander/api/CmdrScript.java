package org.digiplex.bukkitplugin.commander.api;

import java.util.List;
import java.util.Map;
import java.util.regex.MatchResult;

import org.bukkit.command.CommandSender;
import org.digiplex.bukkitplugin.commander.CommanderEngine;
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
 * @since 2.0
 */
public class CmdrScript {
	Executable block;
	private ScriptEnvironment lastRunEnv;
	
	/**
	 * Executes this script with the given sender and matcher. The matcher is used by the script as if
	 * it were a matcher from a replacement pair match.
	 * <p>The args variable allows variables to be set before the script starts. The caller must supply
	 * a Map of String to Object mappings. Valid objects include Integers, Strings, Booleans, and Lists
	 * of Strings. Violation of these will result in an IllegalArgumentException. Use of the argument
	 * feature should be used sparingly, as it is slow due to the initial type checking.
	 * @param sender The player or other CommandSender running this script. Cannot be null.
	 * @param cmdMatcher The Matcher object (obtainable via the java.util.regex.Pattern class)
	 * @param args A Map of String to valid Objects.
	 * @throws ScriptExecutionException Thrown if an error occurs while executing the script
	 * @since 2.0
	 */
	public void execute(CommandSender sender, MatchResult cmdMatcher, Map<String, Object> args) throws ScriptExecutionException {
		if (sender == null) throw new IllegalArgumentException("Sender cannot be null!");
		lastRunEnv = new ScriptEnvironment(); {
			lastRunEnv.setCommandSender(sender);
			lastRunEnv.setServer(sender.getServer());
			lastRunEnv.setMatch(cmdMatcher);
		}
		
		//MAJOR type checking on every variable in the list here
		if (args != null) {
			//for every key in the arguments:
			for (String key : args.keySet()) {
				Object val = args.get(key);
				
				boolean valid = false;
				for (int i = 0; i < CommanderEngine.VALID_VAR_TYPES.length; i++) {
					valid |= CommanderEngine.VALID_VAR_TYPES[i] == val.getClass();
				}
				if (!valid) throw new IllegalArgumentException("Invalid class of argument! "+val.getClass().getName());
				
				if (val.getClass() == List.class) { 
					//if it's a list, and due to Java's erasure of what type of list, we now must check!
					for (Object o : (List<?>)val) {
						valid = false;
						for (int i = 0; i < CommanderEngine.VALID_COLL_TYPES.length; i++) {
							valid |= CommanderEngine.VALID_COLL_TYPES[i] == o.getClass();
						}
						if (!valid) throw new IllegalArgumentException("Invalid class of argument in List! "+val.getClass().getName());
					}
					//if we get here, we're good, insert into the collections
					@SuppressWarnings("unchecked") List<String> s = (List<String>) val;
					String id = lastRunEnv.pushCollection(s);
					lastRunEnv.setVariableValue(key, id);
					
				} else {
					//if it is not a list, then we can just insert it now
					lastRunEnv.setVariableValue(key, val);
				}
			}
		}
		
		try {
			block.execute(lastRunEnv);
		} catch (BreakScriptException ex) {
			//do nothing
		} catch (BadScriptException ex) {
			throw new ScriptExecutionException("Error while executing script!", ex);
		}
	}
	
	/**
	 * Executes this script with the given sender.
	 * <p>The args variable allows variables to be set before the script starts. The caller must supply
	 * a Map of String to Object mappings. Valid objects include Integers, Strings, Booleans, and Lists
	 * of Strings. Violation of these will result in an IllegalArgumentException. Use of the argument
	 * feature should be used sparingly, as it is slow due to the initial type checking.
	 * @param sender The player or other CommandSender running this script. Cannot be null.
	 * @param args A Map of String to valid Objects.
	 * @throws ScriptExecutionException Thrown if an error occurs while executing the script
	 * @since 2.0
	 */
	public void execute(CommandSender sender, Map<String, Object> args) throws ScriptExecutionException {
		this.execute(sender, null, args);
	}
	
	/**
	 * Executes this script with the given sender and matcher. The matcher is used by the script as if
	 * it were a matcher from a replacement pair match.
	 * @param sender The player or other CommandSender running this script. Cannot be null.
	 * @param cmdMatcher The Matcher object (obtainable via the java.util.regex.Pattern class)
	 * @throws ScriptExecutionException Thrown if an error occurs while executing the script
	 * @since 2.0
	 */
	public void execute(CommandSender sender, MatchResult cmdMatcher) throws ScriptExecutionException {
		this.execute(sender, cmdMatcher, null);
	} 
	
	/**
	 * Executes this script with the given sender.
	 * @param sender The player or other CommandSender running this script. Cannot be null.
	 * @throws ScriptExecutionException Thrown if an error occurs while executing the script
	 * @since 2.0
	 */
	public void execute(CommandSender sender) throws ScriptExecutionException {
		this.execute(sender, null, null);
	}
	
	/**
	 * Allows values to be accessed from the variable listing of the last run of this script.
	 * @param varname
	 * @return
	 * @since 2.0
	 */
	public Object getLastRunVariable(String varname) {
		return lastRunEnv.getVariableValue(varname);
	}
}
