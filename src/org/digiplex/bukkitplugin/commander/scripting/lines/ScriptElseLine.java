package org.digiplex.bukkitplugin.commander.scripting.lines;

import org.digiplex.bukkitplugin.commander.scripting.BadScriptException;
import org.digiplex.bukkitplugin.commander.scripting.Executable;
import org.digiplex.bukkitplugin.commander.scripting.ScriptEnvironment;

public class ScriptElseLine extends ScriptLine {
	Executable line;
	
	public ScriptElseLine(Executable scriptLine) {
		this.line = scriptLine;
	}

	public Executable getEncapsulatedLine() {
		return line;
	}
	
	@Override public void execute(ScriptEnvironment env) throws BadScriptException {
		if (line == null) throw new BadScriptException("No line to execute!");
		line.execute(env);
	}
	
	@Override public boolean giveNextLine(Executable script) {
		if (line == null) {
			line = script;
		} else if (line instanceof ScriptLine) {
			return ((ScriptLine) line).giveNextLine(script);
		}
		return false;
	}
	
	@Override public boolean isConstruct() {
		if (line instanceof ScriptLine) {
			return ((ScriptLine) line).isConstruct();
		}
		return true;
	}
	@Override public boolean isDirective() {
		if (line instanceof ScriptLine) {
			return ((ScriptLine) line).isDirective();
		}
		return false;
	}

	@Override public boolean requiresNextLine() {
		if (line instanceof ScriptLine) { //null instanceof X == false
			return ((ScriptLine) line).requiresNextLine();
		}
		return line == null;
	}
	@Override public boolean requiresPreviousConstruct() {return true;}
	
	@Override public String toString() {
		return "[else : "+line+"]";
	}
	
	@Override public void verify() throws BadScriptException {
		if (line == null)
			throw new BadScriptException("Else line has no encapsulated block!", lineno);
		
		line.verify();
	}

}
