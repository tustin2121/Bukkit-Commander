package org.digiplex.bukkitplugin.commander.scripting.lines.loop;

import org.digiplex.bukkitplugin.commander.scripting.Executable;
import org.digiplex.bukkitplugin.commander.scripting.ScriptEnvironment;
import org.digiplex.bukkitplugin.commander.scripting.exceptions.BadScriptException;
import org.digiplex.bukkitplugin.commander.scripting.exceptions.BreakScriptException;
import org.digiplex.bukkitplugin.commander.scripting.lines.ScriptLine;

public class ScriptLoopConstruct extends ScriptLine {
	int start, end, step;
	String varname, endvar;
	Executable loopline;
	
	public ScriptLoopConstruct(String varname, int start, int end, int step) throws BadScriptException {
		if (step == 0) throw new BadScriptException("Loop's step must be non-zero!");
		if (start == end) throw new BadScriptException("Loop's start and end cannot be the same!");
		if (start < end && step < 0) throw new BadScriptException("Loop's step must be a positive when counting upward!");
		if (start > end && step > 0) throw new BadScriptException("Loop's step must be a negative when counting downward!");
		
		this.start = start; this.end = end; this.step = step;
	}
	
	public ScriptLoopConstruct(String varname, int start, String endvar, int step) throws BadScriptException {
		if (step == 0) throw new BadScriptException("Loop's step must be non-zero!");
	}
	
	@Override public void execute(ScriptEnvironment env) throws BadScriptException {
		env = env.getChild(); //entering new scope
		env.setVariableValue(varname, start);
		for (int i = start; i > end; i += step) {
//			Object o = env.getVariableValue(varname);
//			if (!(o instanceof Integer))
//				throw new BadScriptException("Looping variable is not (or is no longer) an integer!", lineno);
			
			env.setVariableValue(varname, i);
			//The Int Loop doesn't care if you step on its variable, it will overwrite on the next loop
			//it only provides its variable to you to read. This is by design.
			try {
				loopline.execute(env);
			} catch (BreakScriptException ex) {
				break; //break the loop and continue execution
			}
		}
	}

	@Override public void verify() throws BadScriptException {
		if (loopline == null)
			throw new BadScriptException("Loop has no body!");
	}

	@Override public boolean isConstruct() {return true;}
	@Override public boolean isDirective() {return false;}

	@Override public boolean requiresNextLine() {return loopline == null;}
	@Override public boolean requiresPreviousConstruct() {return false;}

}
