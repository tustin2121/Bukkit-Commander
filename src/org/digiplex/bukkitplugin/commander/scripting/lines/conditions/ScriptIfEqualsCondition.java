package org.digiplex.bukkitplugin.commander.scripting.lines.conditions;

import org.digiplex.bukkitplugin.commander.scripting.ScriptEnvironment;
import org.digiplex.bukkitplugin.commander.scripting.exceptions.BadScriptException;

public class ScriptIfEqualsCondition extends ScriptCondition {
	String lhs, rhs;
	
	public ScriptIfEqualsCondition(String lhs, String rhs) throws BadScriptException {
		if (lhs == null || lhs.isEmpty()) 
			throw new BadScriptException("Check condition has no left hand side!");
		if (rhs == null || rhs.isEmpty()) 
			throw new BadScriptException("Check condition has no right hand side!");
		
		this.lhs = lhs;
		this.rhs = rhs;
	}
	
	@Override public boolean executeCondition(ScriptEnvironment env) throws BadScriptException {
		String r = env.substituteTokens(lhs);
		String l = env.substituteTokens(rhs);
		
		int li, ri;
		try {
			li = Integer.parseInt(l);
			ri = Integer.parseInt(r);
			//both left and right must parse to do an int compare
			return li == ri;
		} catch (NumberFormatException ex) {}
		
		//otherwise, just compare as strings
		return l.equalsIgnoreCase(r);
	}
	
	@Override public String toString() {
		return "Condition["+((not)?"!":" ")+"if equals "+lhs+" = "+rhs+" ]";
	}
}
