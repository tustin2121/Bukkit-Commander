package org.digiplex.bukkitplugin.commander.scripting.lines.conditions;

import org.digiplex.bukkitplugin.commander.scripting.ScriptEnvironment;
import org.digiplex.bukkitplugin.commander.scripting.exceptions.BadScriptException;

/**
 * Conditions are defined thusly: "\[(if)\s+([^\]])\]"
 * 
 * The line [if condition] defines a conditional. The next line is the true block. If the
 * next line is a open curly brace, it is a block, going to the next matched curly brace.
 * after that, the line [else] defines the false block, going by the same rules.
 * @author timpittman
 */
public abstract class ScriptCondition {
	protected boolean not;
	
	public boolean testCondition(ScriptEnvironment env) throws BadScriptException {
		boolean condres = this.executeCondition(env);
		if (not) condres = !condres;
		return condres;
	}
	protected abstract boolean executeCondition(ScriptEnvironment env) throws BadScriptException;
	
	public boolean inNotMode() { return not; }
	public void setNotMode(boolean not) { this.not = not; }
	
	public void verify() throws BadScriptException {}
}
