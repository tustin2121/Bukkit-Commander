package org.digiplex.bukkitplugin.commander.scripting.lines.conditions;

import org.digiplex.bukkitplugin.commander.scripting.ScriptEnvironment;
import org.digiplex.bukkitplugin.commander.scripting.exceptions.BadScriptException;

public class ScriptIfVarCheckCondition extends ScriptCondition {
	String var;
	
	public ScriptIfVarCheckCondition(String var) throws BadScriptException {
		if (var == null || var.isEmpty()) 
			throw new BadScriptException("Check condition has no variable to check!");
		
		this.var = var;
	}

	@Override public boolean executeCondition(ScriptEnvironment env) throws BadScriptException {
		Object val = env.getVariableValue(var);
		if (val instanceof Boolean) {
			return ((Boolean) val).booleanValue();
		} else if (val instanceof String) {
			return !((String) val).isEmpty();
		} else if (val != null) {
			return true;
		}
		return false;
	}
	
	@Override public String toString() {
		return "Condition["+((not)?"!":" ")+"if var check "+var+" ]";
	}
}
