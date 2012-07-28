package org.digiplex.bukkitplugin.commander.scripting.lines;

import org.digiplex.bukkitplugin.commander.scripting.ScriptEnvironment;
import org.digiplex.bukkitplugin.commander.scripting.ScriptLine;

public class ScriptVarAssignmentLine extends ScriptLine {
	String varname, literal;
	
	public ScriptVarAssignmentLine(String var, String literal) {
		this.varname = var;
		this.literal = literal;
	}
	
	@Override public void execute(ScriptEnvironment env) {
		String command = env.substituteTokens(literal);
//		if (env.getMatcher() != null)
//			command = env.getMatcher().replaceFirst(command);
		
		env.setVariableValue(varname, command);
	}

	@Override public boolean isConstruct() {return false;}
	@Override public boolean isDirective() {return false;}

	@Override public boolean requiresNextLine() {return false;}
	@Override public boolean requiresPreviousConstruct() {return false;}

}
