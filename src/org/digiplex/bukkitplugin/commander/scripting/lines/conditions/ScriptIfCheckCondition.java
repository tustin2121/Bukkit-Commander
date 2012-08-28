package org.digiplex.bukkitplugin.commander.scripting.lines.conditions;

import static org.digiplex.bukkitplugin.commander.CommanderEngine.printDebug;

import org.digiplex.bukkitplugin.commander.scripting.ScriptEnvironment;
import org.digiplex.bukkitplugin.commander.scripting.exceptions.BadScriptException;

public class ScriptIfCheckCondition extends ScriptCondition {
	String var;
	
	public ScriptIfCheckCondition(String var) throws BadScriptException {
		if (var == null || var.isEmpty()) 
			throw new BadScriptException("Check condition has no value to check!");
		
		this.var = var;
	}

	@Override public boolean executeCondition(ScriptEnvironment env) throws BadScriptException {
		String val = env.substituteTokens(var);
		
		printDebug("condition", "check %s (%s)", var, val);
		
		if (val == null) return false;
		if (val.isEmpty()) return false;
		if (val.equalsIgnoreCase("true")) return true;
		if (val.equalsIgnoreCase("false")) return false;
		//does not check if the value is integer 0
		
		return false;
	}
	
	@Override public String toString() {
		return "Condition["+((not)?"!":" ")+"if check "+var+" ]";
	}
}
