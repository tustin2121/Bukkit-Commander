package org.digiplex.bukkitplugin.commander.scripting.lines.variables;

import static org.digiplex.bukkitplugin.commander.CommanderEngine.printDebug;

import org.digiplex.bukkitplugin.commander.scripting.ScriptEnvironment;
import org.digiplex.bukkitplugin.commander.scripting.exceptions.BadScriptException;
import org.digiplex.bukkitplugin.commander.scripting.lines.ScriptLine;

public class ScriptVarUnsetLine extends ScriptLine {
	String varname;
	boolean doGlobal;
	
	public ScriptVarUnsetLine(String var) {
		this.varname = var;
		this.doGlobal = false;
	}
	public ScriptVarUnsetLine(String var, boolean global) {
		this.varname = var;
		this.doGlobal = global;
	}
	
	@Override public void execute(ScriptEnvironment env) throws BadScriptException {
		printDebug("variable", "unset %s", varname);
		
		env.unsetVariable(varname, doGlobal);
	}
	
	@Override public String toString() {
		return "VarUnset["+varname+", global="+doGlobal+"]";
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
