package org.digiplex.bukkitplugin.commander.scripting.lines;

import org.digiplex.bukkitplugin.commander.scripting.ScriptEnvironment;
import org.digiplex.bukkitplugin.commander.scripting.exceptions.BadScriptException;
import org.digiplex.bukkitplugin.commander.scripting.exceptions.BreakScriptException;

public class ScriptBreakLine extends ScriptLine {

	@Override public void execute(ScriptEnvironment env) throws BadScriptException {
		throw new BreakScriptException();
	}

	@Override public void verify() throws BadScriptException {}

	@Override public boolean isConstruct() {return false;}
	@Override public boolean isDirective() {return false;}

	@Override public boolean requiresNextLine() {return false;}
	@Override public boolean requiresPreviousConstruct() {return false;}
	
	@Override public String toString() {
		return "Break[]";
	}
}
