package org.digiplex.bukkitplugin.commander.scripting.lines.conditional;

import org.digiplex.bukkitplugin.commander.scripting.BadScriptException;
import org.digiplex.bukkitplugin.commander.scripting.ScriptEnvironment;
import org.digiplex.bukkitplugin.commander.scripting.lines.ScriptConditionLine;

public class ScriptIfVarCompareConstruct extends ScriptConditionLine {
	String var, equals;
	boolean gt, eq; // > mode (false is < mode), equal-to mode 
	
	public ScriptIfVarCompareConstruct(String var, String comp, boolean gt, boolean eq) { //gt = greater than, opposite less than
		this.var = var; this.equals = comp;
		this.gt = gt; this.eq = eq;
	}
	
	@Override protected boolean executeCondition(ScriptEnvironment env) throws BadScriptException {
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
		return "Condition["+((not)?"!":" ")+"if compare "+var+" = "+equals+", gt="+gt+", eq="+eq+" ]";
	}
}
