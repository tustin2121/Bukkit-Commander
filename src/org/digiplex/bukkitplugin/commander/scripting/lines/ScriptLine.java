package org.digiplex.bukkitplugin.commander.scripting.lines;

import org.digiplex.bukkitplugin.commander.scripting.Executable;
import org.digiplex.bukkitplugin.commander.scripting.exceptions.BadScriptException;



public abstract class ScriptLine implements Executable {
	protected int lineno = -1;
	
	public int getLineNumber() { return lineno; }
	public void setLineNumber(int lineno) { this.lineno = lineno; }
	
	///////////////////////////////////////////////////////////
	
	public abstract boolean isConstruct();
	public abstract boolean isDirective();
	
	public abstract boolean requiresNextLine();
	public abstract boolean requiresPreviousConstruct();
	
	public boolean giveNextLine(Executable script) throws BadScriptException{
		return false;
	}
	
}
