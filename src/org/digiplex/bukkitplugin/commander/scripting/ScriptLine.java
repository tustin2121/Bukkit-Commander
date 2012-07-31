package org.digiplex.bukkitplugin.commander.scripting;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.digiplex.bukkitplugin.commander.scripting.lines.ScriptCommandLine;
import org.digiplex.bukkitplugin.commander.scripting.lines.ScriptDirectiveEchoLine;
import org.digiplex.bukkitplugin.commander.scripting.lines.ScriptVarAssignmentLine;
import org.digiplex.bukkitplugin.commander.scripting.lines.ScriptVarIncrementLine;


public abstract class ScriptLine implements Executable {
	
	
	
	///////////////////////////////////////////////////////////
	
	public abstract boolean isConstruct();
	public abstract boolean isDirective();
	
	public abstract boolean requiresNextLine();
	public abstract boolean requiresPreviousConstruct();
	
	public boolean giveNextLine(Executable script){
		return false;
	}
	
}
