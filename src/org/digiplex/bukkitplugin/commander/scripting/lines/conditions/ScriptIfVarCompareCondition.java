package org.digiplex.bukkitplugin.commander.scripting.lines.conditions;

import org.digiplex.bukkitplugin.commander.scripting.ScriptEnvironment;
import org.digiplex.bukkitplugin.commander.scripting.exceptions.BadScriptException;

public class ScriptIfVarCompareCondition extends ScriptCondition {
	String var, equals;
	boolean gt, eq; // > mode (false is < mode), equal-to mode 
	
	public ScriptIfVarCompareCondition(String var, String comp, boolean gt, boolean eq) throws BadScriptException { //gt = greater than, opposite less than
		if (var == null || var.isEmpty()) 
			throw new BadScriptException("Check condition has no variable to check!");
		if (comp == null || comp.isEmpty()) 
			throw new BadScriptException("Check condition has no value to check against!");
		
		this.var = var; this.equals = comp;
		this.gt = gt; this.eq = eq;
	}
	
	@Override public boolean executeCondition(ScriptEnvironment env) throws BadScriptException {
		Object val = env.getVariableValue(var);
		String eqcmd = env.substituteTokens(equals);
		
		if (val instanceof String) {
			int cmp = ((String) val).compareToIgnoreCase(eqcmd);
			if (cmp == 0 && eq) return true; //if it is equal and we're working equal-to mode, true
			if (cmp > 0 && gt) return true;
			if (cmp < 0 && !gt) return true;
			return false;
			
		} else if (val instanceof Integer) {
			try {
				int valnum = ((Integer) val).intValue();
				int eqnum = Integer.parseInt(eqcmd);
				
				if (valnum == eqnum && eq) return true; //if it is equal and we're working equal-to mode, true
				if (valnum > eqnum && gt) return true;
				if (valnum < eqnum && !gt) return true;
				return false;
				
			} catch (NumberFormatException ex) {
				throw new BadScriptException("Cannot compare number and string with less than or greater than!");
			}
		} else {
			throw new BadScriptException("Invalid type of variable: "+val.getClass().toString());
		}
	}
	
	@Override public String toString() {
		return "Condition["+((not)?"!":" ")+"if var compare "+var+" = "+equals+", gt="+gt+", eq="+eq+" ]";
	}
}
