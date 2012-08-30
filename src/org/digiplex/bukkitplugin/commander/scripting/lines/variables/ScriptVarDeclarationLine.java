package org.digiplex.bukkitplugin.commander.scripting.lines.variables;

import static org.digiplex.bukkitplugin.commander.CommanderEngine.printDebug;

import org.digiplex.bukkitplugin.commander.scripting.ScriptEnvironment;
import org.digiplex.bukkitplugin.commander.scripting.exceptions.BadScriptException;
import org.digiplex.bukkitplugin.commander.scripting.lines.ScriptLine;

/**
 * @since 2.0.1
 */
public class ScriptVarDeclarationLine extends ScriptLine {
	String varname;
	boolean doGlobal;
	
	public ScriptVarDeclarationLine(String var) {
		this.varname = var;
		this.doGlobal = false;
	}
	public ScriptVarDeclarationLine(String var, boolean global) {
		this.varname = var;
		this.doGlobal = global;
	}
	
	@Override public void execute(ScriptEnvironment env) throws BadScriptException {
		printDebug("variable", "declare %s", varname);
		
		Object o = env.getVariableValue(varname);
		if (o == null) o = "";
		
		if (doGlobal)
			env.setVariableGlobally(varname, o); //set it as itself, or empty string
		else
			env.setVariableValue(varname, o); //set it as itself, or empty string
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
