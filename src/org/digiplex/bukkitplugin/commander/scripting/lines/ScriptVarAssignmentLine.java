package org.digiplex.bukkitplugin.commander.scripting.lines;

import org.digiplex.bukkitplugin.commander.scripting.ScriptEnvironment;
import org.digiplex.bukkitplugin.commander.scripting.ScriptLine;

public class ScriptVarAssignmentLine extends ScriptLine {
	String varname, literal;
	boolean doGlobal;
	
	public ScriptVarAssignmentLine(String var, String literal) {
		this.varname = var;
		this.literal = literal;
		this.doGlobal = false;
	}
	public ScriptVarAssignmentLine(String var, String literal, boolean global) {
		this.varname = var;
		this.literal = literal;
		this.doGlobal = global;
	}
	
	@Override public void execute(ScriptEnvironment env) {
		String command = env.substituteTokens(literal);
		
		if (doGlobal) {
			env.setVariableGlobally(varname, command);
		} else {
			env.setVariableValue(varname, command);
		}
	}
	
	@Override public String toString() {
		return "VarAssign["+varname+"="+literal+", global="+doGlobal+"]";
	}

	@Override public boolean isConstruct() {return false;}
	@Override public boolean isDirective() {return false;}

	@Override public boolean requiresNextLine() {return false;}
	@Override public boolean requiresPreviousConstruct() {return false;}

}
