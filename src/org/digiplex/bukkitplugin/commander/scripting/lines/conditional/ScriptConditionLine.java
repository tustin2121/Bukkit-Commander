package org.digiplex.bukkitplugin.commander.scripting.lines.conditional;

import org.digiplex.bukkitplugin.commander.scripting.Executable;
import org.digiplex.bukkitplugin.commander.scripting.ScriptEnvironment;
import org.digiplex.bukkitplugin.commander.scripting.exceptions.BadScriptException;
import org.digiplex.bukkitplugin.commander.scripting.lines.ScriptElseLine;
import org.digiplex.bukkitplugin.commander.scripting.lines.ScriptLine;

/**
 * Conditions are defined thusly: "\[(if)\s+([^\]])\]"
 * 
 * The line [if condition] defines a conditional. The next line is the true block. If the
 * next line is a open curly brace, it is a block, going to the next matched curly brace.
 * after that, the line [else] defines the false block, going by the same rules.
 * @author timpittman
 */
public abstract class ScriptConditionLine extends ScriptLine {
	protected boolean not;
	
	protected Executable trueBlock;
	protected Executable falseBlock;
	
	@Override public void execute(ScriptEnvironment env) throws BadScriptException {
		if (trueBlock == null)
			throw new BadScriptException("If statement does not have a true block!");
		
		boolean condres = this.executeCondition(env);
		if (not) condres = !condres;
		
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
	
	protected abstract boolean executeCondition(ScriptEnvironment env) throws BadScriptException;
	
	public boolean inNotMode() { return not; }
	public void setNotMode(boolean not) { this.not = not; }
	
	@Override public boolean giveNextLine(Executable script) {
		if (trueBlock == null) {
			trueBlock = script;
		} else if (falseBlock == null) {
			falseBlock = script;
		} else {
			return false;
		}
		return true;
	}
	
	@Override public boolean isConstruct() {return true;}
	@Override public boolean isDirective() {return false;}

	@Override public boolean requiresNextLine() {
		return trueBlock == null;
	}
	@Override public boolean requiresPreviousConstruct() {return false;}
	
	@Override public void verify() throws BadScriptException {
		//verify
		if (trueBlock == null)
			throw new BadScriptException("Condition has no true block!", lineno);
		
		trueBlock.verify();
		if (falseBlock != null) {
			falseBlock.verify(); //recursively verifies
			
			//clean up after everything is verified
			if (falseBlock instanceof ScriptElseLine) {
				falseBlock = ((ScriptElseLine) falseBlock).getEncapsulatedLine(); 
			}
		}
	}
}
