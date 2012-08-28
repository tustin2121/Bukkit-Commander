package org.digiplex.bukkitplugin.commander.scripting.lines.directives;

import static org.digiplex.bukkitplugin.commander.CommanderEngine.printDebug;

import org.digiplex.bukkitplugin.commander.scripting.ScriptEnvironment;
import org.digiplex.bukkitplugin.commander.scripting.exceptions.BadScriptException;
import org.digiplex.bukkitplugin.commander.scripting.lines.ScriptLine;

public class ScriptDirectiveErrorLine extends ScriptLine {
	boolean enable;
	
	public ScriptDirectiveErrorLine(boolean ignore) {
		this.enable = ignore;
	}

	@Override public void execute(ScriptEnvironment env) throws BadScriptException {
		printDebug("directive", "continue on error %b", enable);
		
		env.setContinueOnError(enable);
	}

	@Override public void verify() throws BadScriptException {}
	
	@Override public String toString() {
		return "Directive[errorContinue="+enable+"]";
	}

	@Override public boolean isConstruct() {return false;}
	@Override public boolean isDirective() {return true;}

	@Override public boolean requiresNextLine() {return false;}
	@Override public boolean requiresPreviousConstruct() {return false;}

}
