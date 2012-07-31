package org.digiplex.bukkitplugin.commander.scripting.lines;

import org.digiplex.bukkitplugin.commander.scripting.BadScriptException;
import org.digiplex.bukkitplugin.commander.scripting.ScriptEnvironment;
import org.digiplex.bukkitplugin.commander.scripting.ScriptLine;

public class ScriptVarIncrementLine extends ScriptLine {
	String varname;
	boolean increment;
	
	public ScriptVarIncrementLine(String var) {
		this.varname = var;
		this.increment = true;
	}
	public ScriptVarIncrementLine(String var, boolean decrement) {
		this.varname = var;
		this.increment = !decrement;
	}
	
	@Override public void execute(ScriptEnvironment env) throws BadScriptException {
		Object o = env.getVariableValue(varname);
		int vi = 0;
		if (!(o instanceof Integer)) {
			if (o instanceof String) {
				try {
					vi = Integer.parseInt((String)o);
				} catch (NumberFormatException ex) {
					throw new BadScriptException("Attempting to increment a variable that isn't a number!");
				}
			} else {
				throw new BadScriptException("Attempting to increment a variable that isn't a number!");
			}
		} else {
			vi = (Integer) o;
		}
		
		if (increment) vi++;
		else vi--;
		
		env.setVariableValue(varname, vi);
	}
	
	@Override public String toString() {
		return "Var++["+varname+" "+((increment)?"++":"--")+"]";
	}
	
	@Override public boolean isConstruct() {return false;}
	@Override public boolean isDirective() {return false;}

	@Override public boolean requiresNextLine() {return false;}
	@Override public boolean requiresPreviousConstruct() {return false;}
	
	@Override public void verify() throws BadScriptException {
		if (varname == null)
			throw new BadScriptException("Variable is null!", lineno);
	}

}
