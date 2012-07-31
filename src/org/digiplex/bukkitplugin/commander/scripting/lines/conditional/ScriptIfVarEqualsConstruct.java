package org.digiplex.bukkitplugin.commander.scripting.lines.conditional;

import org.digiplex.bukkitplugin.commander.scripting.BadScriptException;
import org.digiplex.bukkitplugin.commander.scripting.ScriptEnvironment;
import org.digiplex.bukkitplugin.commander.scripting.lines.ScriptConditionLine;

public class ScriptIfVarEqualsConstruct extends ScriptConditionLine {
	String var, equals;
	
	public ScriptIfVarEqualsConstruct(String var, String equals) {
		this.var = var;
		this.equals = equals;
	}
	
	@Override protected boolean executeCondition(ScriptEnvironment env) throws BadScriptException {
		Object val = env.getVariableValue(var);
		String eqcmd = env.substituteTokens(equals);
		
		if (val instanceof String) {
			return val.equals(eqcmd);
		} else if (val instanceof Integer) {
			try {
				int num = Integer.parseInt(eqcmd);
				return ((Integer)val).intValue() == num;
			} catch (NumberFormatException ex) {
				return false;
			}
		} else {
			throw new BadScriptException("Invalid type of variable: "+val.getClass().toString());
		}
	}
	
	@Override public String toString() {
		return "Condition["+((not)?"!":" ")+"if equals "+var+" = "+equals+" ]";
	}
}
