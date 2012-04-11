package org.digiplex.bukkitplugin.commander.scripting;

/**
 * Conditions are defined thusly: "\[(if)\s+([^\]])\]"
 * 
 * The line [if condition] defines a conditional. The next line is the true block. If the
 * next line is a open curly brace, it is a block, going to the next matched curly brace.
 * after that, the line [else] defines the false block, going by the same rules.
 * @author timpittman
 */
public class ScriptConditionLine implements Executable {
	String condition; 
	String comparitor;
	
	ScriptBlock trueBlock;
	ScriptBlock falseBlock;
	
	public ScriptConditionLine(String command) {
		// TODO Auto-generated constructor stub
	}
	
	@Override public void execute(ScriptEnvironment env) {
		
	}
}
