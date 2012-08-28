package org.digiplex.bukkitplugin.commander.scripting.lines.construct;

import org.digiplex.bukkitplugin.commander.scripting.Executable;
import org.digiplex.bukkitplugin.commander.scripting.ScriptEnvironment;
import org.digiplex.bukkitplugin.commander.scripting.exceptions.BadScriptException;
import org.digiplex.bukkitplugin.commander.scripting.lines.ScriptElseLine;
import org.digiplex.bukkitplugin.commander.scripting.lines.ScriptLine;
import org.digiplex.bukkitplugin.commander.scripting.lines.conditions.ScriptCondition;

public class ScriptIfConstruct extends ScriptLine {
	protected ScriptCondition condition;
	
	protected Executable trueBlock;
	protected Executable falseBlock;
	
	public ScriptIfConstruct(ScriptCondition condition) {
		this.condition = condition;
	}

	@Override public void execute(ScriptEnvironment env) throws BadScriptException {		
		boolean condres = condition.testCondition(env);
		
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
	
	@Override public String toString() {
		return "If["+condition+"]";
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
