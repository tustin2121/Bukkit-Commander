package org.digiplex.bukkitplugin.commander.scripting.lines.directives;

import org.digiplex.bukkitplugin.commander.scripting.ScriptEnvironment;
import org.digiplex.bukkitplugin.commander.scripting.exceptions.BadScriptException;
import org.digiplex.bukkitplugin.commander.scripting.lines.ScriptLine;

public class ScriptDirectiveRunLimLine extends ScriptLine {
	public static final int HARDLIMIT = 200;
	int runlim;
	
	public ScriptDirectiveRunLimLine(int num) throws BadScriptException {
		if (num > HARDLIMIT)
			throw new BadScriptException("Run limit cannot exceed "+HARDLIMIT+"!");
		this.runlim = num;
	}	
	
	@Override public void execute(ScriptEnvironment env) throws BadScriptException {
		env.setRunLimit(runlim);
	}

	@Override public void verify() throws BadScriptException {}
	
	@Override public String toString() {
		return "Directive[runlim="+runlim+"]";
	}

	@Override public boolean isConstruct() {return false;}
	@Override public boolean isDirective() {return true;}

	@Override public boolean requiresNextLine() {return false;}
	@Override public boolean requiresPreviousConstruct() {return false;}

}
