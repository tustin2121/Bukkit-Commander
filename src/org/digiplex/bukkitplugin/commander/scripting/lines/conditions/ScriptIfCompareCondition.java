package org.digiplex.bukkitplugin.commander.scripting.lines.conditions;

import org.digiplex.bukkitplugin.commander.scripting.ScriptEnvironment;
import org.digiplex.bukkitplugin.commander.scripting.exceptions.BadScriptException;

public class ScriptIfCompareCondition extends ScriptCondition {
	String lhs, rhs;
	boolean gt, eq; // > mode (false is < mode), equal-to mode 
	
	public ScriptIfCompareCondition(String lhs, String rhs, boolean gt, boolean eq) throws BadScriptException { //gt = greater than, opposite less than
		if (lhs == null || lhs.isEmpty()) 
			throw new BadScriptException("Check condition has no left hand side!");
		if (rhs == null || rhs.isEmpty()) 
			throw new BadScriptException("Check condition has no right hand side!");
		
		this.lhs = lhs; this.rhs = rhs;
		this.gt = gt; this.eq = eq;
	}
	
	@Override public boolean executeCondition(ScriptEnvironment env) throws BadScriptException {
		String l = env.substituteTokens(lhs);
		String r = env.substituteTokens(rhs);
		
		int li, ri;
		
		try {
			li = Integer.parseInt(l);
			ri = Integer.parseInt(r);
			//both need to parse correctly to do an integer comparison
			
			if (li == ri && eq) return true; //if it is equal and we're working equal-to mode, true
			if (li > ri && gt) return true;
			if (li < ri && !gt) return true;
			return false;
		} catch (NumberFormatException ex) {}
		
		int cmp = l.compareToIgnoreCase(r);
		if (cmp == 0 && eq) return true; //if it is equal and we're working equal-to mode, true
		if (cmp > 0 && gt) return true;
		if (cmp < 0 && !gt) return true;
		return false;
	}
	
	@Override public String toString() {
		return "Condition["+((not)?"!":" ")+"if compare "+lhs+" = "+rhs+", gt="+gt+", eq="+eq+" ]";
	}
	
}
