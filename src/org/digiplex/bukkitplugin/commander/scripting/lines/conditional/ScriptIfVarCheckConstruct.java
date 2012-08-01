package org.digiplex.bukkitplugin.commander.scripting.lines.conditional;

import org.digiplex.bukkitplugin.commander.scripting.BadScriptException;
import org.digiplex.bukkitplugin.commander.scripting.ScriptEnvironment;

public class ScriptIfVarCheckConstruct extends ScriptConditionLine {
	String var;
	
	public ScriptIfVarCheckConstruct(String var) {
		this.var = var;
	}

	@Override protected boolean executeCondition(ScriptEnvironment env) throws BadScriptException {
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
		return "Condition["+((not)?"!":" ")+"if check "+var+" ]";
	}
	
	@Override public void verify() throws BadScriptException {
		if (var == null) //probably unneeded
			throw new BadScriptException("If has no variable!", lineno);
		
		super.verify();
	}
}
