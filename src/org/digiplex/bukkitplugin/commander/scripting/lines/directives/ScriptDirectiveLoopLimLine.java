package org.digiplex.bukkitplugin.commander.scripting.lines.directives;

import org.digiplex.bukkitplugin.commander.scripting.ScriptEnvironment;
import org.digiplex.bukkitplugin.commander.scripting.exceptions.BadScriptException;
import org.digiplex.bukkitplugin.commander.scripting.lines.ScriptLine;

public class ScriptDirectiveLoopLimLine extends ScriptLine {
	int looplim;
	
	public ScriptDirectiveLoopLimLine(int num) {
		this.looplim = num;
	}	
	
	@Override public void execute(ScriptEnvironment env) throws BadScriptException {
		env.setLoopLimit(looplim);
	}

	@Override public void verify() throws BadScriptException {}
	
	@Override public String toString() {
		return "Directive[looplim="+looplim+"]";
	}

	@Override public boolean isConstruct() {return false;}
	@Override public boolean isDirective() {return true;}

	@Override public boolean requiresNextLine() {return false;}
	@Override public boolean requiresPreviousConstruct() {return false;}

}
