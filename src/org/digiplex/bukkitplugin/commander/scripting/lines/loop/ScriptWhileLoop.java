package org.digiplex.bukkitplugin.commander.scripting.lines.loop;

import org.digiplex.bukkitplugin.commander.scripting.Executable;
import org.digiplex.bukkitplugin.commander.scripting.ScriptEnvironment;
import org.digiplex.bukkitplugin.commander.scripting.exceptions.BadScriptException;
import org.digiplex.bukkitplugin.commander.scripting.exceptions.BreakScriptException;
import org.digiplex.bukkitplugin.commander.scripting.lines.ScriptLine;
import org.digiplex.bukkitplugin.commander.scripting.lines.conditional.ScriptConditionLine;

public class ScriptWhileLoop extends ScriptLine {
	ScriptConditionLine condition;
	Executable loopline;
	
	public ScriptWhileLoop(ScriptConditionLine condition) {
		this.condition = condition;
	}
	
	@Override public void execute(ScriptEnvironment env) throws BadScriptException {
		env = env.getChild();
		
		int loopnum = 0;
		int looplimit = env.getLoopLimit();
		
		while (condition.executeCondition(env)) {
			try {
				loopline.execute(env);
			} catch (BreakScriptException ex) {
				break; //break the loop and continue execution
			}
			
			loopnum++;
			if (loopnum > looplimit)
				throw new BadScriptException("Loop has reached its legal limit without breaking!");
		}
		
	}

	@Override public void verify() throws BadScriptException {
		if (loopline == null)
			throw new BadScriptException("While loop has no body!");
	}
	
	@Override public boolean giveNextLine(Executable script) throws BadScriptException {
		if (loopline == null)
			loopline = script;
		else
			throw new BadScriptException("Loop constructs cannot accept 'else' statements.");
		return true;
	}
	
	@Override public String toString() {
		return "While["+condition+"]";
	}
	
	@Override public boolean isConstruct() {return true;}
	@Override public boolean isDirective() {return false;}

	@Override public boolean requiresNextLine() {return loopline == null;}
	@Override public boolean requiresPreviousConstruct() {return false;}

}
