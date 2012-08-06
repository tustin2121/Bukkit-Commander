package org.digiplex.bukkitplugin.commander.scripting.lines.conditions;

import org.digiplex.bukkitplugin.commander.scripting.ScriptEnvironment;
import org.digiplex.bukkitplugin.commander.scripting.exceptions.BadScriptException;

public class ScriptIfVarEqualsCondition extends ScriptCondition {
	String var, equals;
	
	public ScriptIfVarEqualsCondition(String var, String equals) throws BadScriptException {
		if (var == null || var.isEmpty()) 
			throw new BadScriptException("Check condition has no variable to check!");
		if (equals == null || equals.isEmpty()) 
			throw new BadScriptException("Check condition has no value to check against!");
		
		this.var = var;
		this.equals = equals;
	}
	
	@Override public boolean executeCondition(ScriptEnvironment env) throws BadScriptException {
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
		return "Condition["+((not)?"!":" ")+"if var equals "+var+" = "+equals+" ]";
	}
}
