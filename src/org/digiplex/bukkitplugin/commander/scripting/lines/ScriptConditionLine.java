package org.digiplex.bukkitplugin.commander.scripting.lines;

import org.digiplex.bukkitplugin.commander.scripting.BadScriptException;
import org.digiplex.bukkitplugin.commander.scripting.Executable;
import org.digiplex.bukkitplugin.commander.scripting.ScriptEnvironment;
import org.digiplex.bukkitplugin.commander.scripting.ScriptLine;

/**
 * Conditions are defined thusly: "\[(if)\s+([^\]])\]"
 * 
 * The line [if condition] defines a conditional. The next line is the true block. If the
 * next line is a open curly brace, it is a block, going to the next matched curly brace.
 * after that, the line [else] defines the false block, going by the same rules.
 * @author timpittman
 */
public class ScriptConditionLine extends ScriptLine {
	String condition; 
	String comparitor;
	
	Executable trueBlock;
	Executable falseBlock;
	
	public ScriptConditionLine(String command) {
		
	}
	
	@Override public void execute(ScriptEnvironment env) throws BadScriptException {
		boolean condres = true; //TODO if condition is true
		
		/* how this works:
		 * if the condition result is true, the true statement is run. If that statement is a block, the whole block
		 * is run. If the condition is false and there's a false block, it runs the false statment. This could be an
		 * elseif construct, which is a subclass of this condition class. In that case, the condition is run, which
		 * could run its true or its false block. False blocks are chained in an else-if fashion this way. 
		 */
		if (condres)
			trueBlock.execute(env);
		else {
			if (falseBlock != null) falseBlock.execute(env);
		}
	}
	
	@Override public boolean isConstruct() {return true;}
	@Override public boolean isDirective() {return false;}

	@Override public boolean requiresNextLine() {return true;}
	@Override public boolean requiresPreviousConstruct() {return false;}
}
