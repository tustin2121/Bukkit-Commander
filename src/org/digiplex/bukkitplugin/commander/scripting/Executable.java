package org.digiplex.bukkitplugin.commander.scripting;

import org.digiplex.bukkitplugin.commander.scripting.exceptions.BadScriptException;

public interface Executable {
	/**
	 * Executes this executable with the given environment.
	 * @param env
	 * @throws BadScriptException
	 */
	public void execute(ScriptEnvironment env) throws BadScriptException;
	
	/**
	 * Called after parsing is all finished. This method will check to make sure everything is 
	 * clean and ready for execution. If any required parts are missing, a BadScriptException
	 * should be thrown. If anything can be compacted or cleaned up, it can be done now. 
	 * @throws BadScriptException
	 */
	public void verify() throws BadScriptException;
}
