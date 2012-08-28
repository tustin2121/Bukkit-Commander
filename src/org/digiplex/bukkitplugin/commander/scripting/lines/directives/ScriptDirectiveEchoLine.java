package org.digiplex.bukkitplugin.commander.scripting.lines.directives;

import static org.digiplex.bukkitplugin.commander.CommanderEngine.printDebug;

import org.digiplex.bukkitplugin.commander.scripting.EchoControl;
import org.digiplex.bukkitplugin.commander.scripting.ScriptEnvironment;
import org.digiplex.bukkitplugin.commander.scripting.exceptions.BadScriptException;
import org.digiplex.bukkitplugin.commander.scripting.lines.ScriptLine;

public class ScriptDirectiveEchoLine extends ScriptLine {
	boolean enable;
	
	public ScriptDirectiveEchoLine(boolean enable) {
		this.enable = enable;
	}

	@Override public void execute(ScriptEnvironment env) throws BadScriptException {
		printDebug("directive", "echo %b", enable);
		
		((EchoControl)env.getCommandSender()).setEchoingEnabled(enable);
	}
	
	@Override public String toString() {
		return "Directive[echo="+enable+"]";
	}

	@Override public boolean isConstruct() {return false;}
	@Override public boolean isDirective() {return true;}

	@Override public boolean requiresNextLine() {return false;}
	@Override public boolean requiresPreviousConstruct() {return false;}
	
	@Override public void verify() throws BadScriptException {}
}
