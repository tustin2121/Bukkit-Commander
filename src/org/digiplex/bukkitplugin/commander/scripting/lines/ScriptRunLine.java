package org.digiplex.bukkitplugin.commander.scripting.lines;

import org.digiplex.bukkitplugin.commander.CommanderPlugin;
import org.digiplex.bukkitplugin.commander.scripting.ScriptBlock;
import org.digiplex.bukkitplugin.commander.scripting.ScriptEnvironment;
import org.digiplex.bukkitplugin.commander.scripting.exceptions.BadScriptException;
import org.digiplex.bukkitplugin.commander.scripting.exceptions.BreakScriptException;

public class ScriptRunLine extends ScriptLine {
	String blockalias;
	
	public ScriptRunLine(String alias) {
		this.blockalias = alias;
	}
	
	@Override public void execute(ScriptEnvironment env) throws BadScriptException {
		ScriptBlock block = CommanderPlugin.getScript(blockalias);
		if (block == null)
			throw new BadScriptException("No stored script has the alias \""+blockalias+"\"!");
		
		try {
			block.execute(env.getChild());
		} catch (BreakScriptException ex) {} //catch break out here, continue merrily
	}

	@Override public void verify() throws BadScriptException {}

	@Override public boolean isConstruct() {return false;}
	@Override public boolean isDirective() {return false;}

	@Override public boolean requiresNextLine() {return false;}
	@Override public boolean requiresPreviousConstruct() {return false;}

}
