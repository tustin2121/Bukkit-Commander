package org.digiplex.bukkitplugin.commander.scripting.lines.variables;

import java.util.ArrayList;
import java.util.Collections;

import org.digiplex.bukkitplugin.commander.scripting.ScriptEnvironment;
import org.digiplex.bukkitplugin.commander.scripting.exceptions.BadScriptException;
import org.digiplex.bukkitplugin.commander.scripting.lines.ScriptLine;

public class ScriptVarCollectionAssignment extends ScriptLine {
	String var;
	ArrayList<String> collectionToAssign;
	boolean doGlobal;
	
	public ScriptVarCollectionAssignment(String var, String collectionString) throws BadScriptException {
		this(var, collectionString, false);
	}
	public ScriptVarCollectionAssignment(String var, String collectionString, boolean doGlobal) throws BadScriptException {
		if (var == null || var.isEmpty())
			throw new BadScriptException("No var to assign collection to!");
		if (collectionString == null || collectionString.isEmpty())
			throw new BadScriptException("No or empty collection to assign!");
		
		this.var = var;
		
		String[] col = collectionString.split(";");
		collectionToAssign = new ArrayList<String>(col.length);
		Collections.addAll(collectionToAssign, col);
	}
	
	@Override public void execute(ScriptEnvironment env) throws BadScriptException {
		String c = env.pushCollection(collectionToAssign);
		if (doGlobal)
			env.setVariableGlobally(var, c);
		else
			env.setVariableValue(var, c);
	}
	
	@Override public String toString() {
		return "VarAssign["+var+"= collection("+collectionToAssign.size()+"), global="+doGlobal+"]";
	}
	
	@Override public void verify() throws BadScriptException {}

	@Override public boolean isConstruct() {return false;}
	@Override public boolean isDirective() {return false;}
	
	@Override public boolean requiresNextLine() {return false;}

	@Override public boolean requiresPreviousConstruct() {return false;}

}
