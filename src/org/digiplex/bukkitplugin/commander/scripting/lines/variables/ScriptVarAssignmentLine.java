package org.digiplex.bukkitplugin.commander.scripting.lines.variables;

import static org.digiplex.bukkitplugin.commander.CommanderEngine.printDebug;

import org.digiplex.bukkitplugin.commander.scripting.ScriptEnvironment;
import org.digiplex.bukkitplugin.commander.scripting.exceptions.BadScriptException;
import org.digiplex.bukkitplugin.commander.scripting.lines.ScriptLine;

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
	
	@Override public void execute(ScriptEnvironment env) throws BadScriptException {
		String command = env.substituteTokens(literal);
		
		printDebug("variable", "assign%s %s = %s (%s)", doGlobal?" globally":"", varname, literal, command);
		
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
	
	@Override public void verify() throws BadScriptException {
		if (varname == null)
			throw new BadScriptException("Variable is null!", lineno);
		if (literal == null)
			throw new BadScriptException("Literal is null!", lineno);
	}
}
