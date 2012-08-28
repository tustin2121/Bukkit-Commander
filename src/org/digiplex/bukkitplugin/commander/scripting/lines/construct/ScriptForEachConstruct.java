package org.digiplex.bukkitplugin.commander.scripting.lines.construct;

import static org.digiplex.bukkitplugin.commander.CommanderEngine.printDebug;

import java.util.List;

import org.digiplex.bukkitplugin.commander.scripting.Executable;
import org.digiplex.bukkitplugin.commander.scripting.ScriptEnvironment;
import org.digiplex.bukkitplugin.commander.scripting.exceptions.BadScriptException;
import org.digiplex.bukkitplugin.commander.scripting.exceptions.BreakScriptException;
import org.digiplex.bukkitplugin.commander.scripting.lines.ScriptLine;

public class ScriptForEachConstruct extends ScriptLine {
	Executable body;
	String varname, rhs;
	
	public ScriptForEachConstruct(String var, String rhs) {
		this.varname = var;
		this.rhs = rhs;
	}

	@Override public void execute(ScriptEnvironment env) throws BadScriptException {
		List<String> collection;
		String cmd = env.substituteTokens(rhs);
		
		printDebug("construct", "for each start => %s (%s)", rhs, cmd);
		
		if (cmd.matches("\\{s([0-9a-fA-F]+)\\}")) { //there is a collection id there and there alone
			collection = env.getCollection(cmd);
			if (collection == null)
				throw new BadScriptException("Collection id does not exist!");
		} else {
			throw new BadScriptException("Given something not a collection! Cannot iterate!");
		}
		
		env = env.getChild(); //entering new scope
		for (String s : collection) {
			printDebug("construct", "for each next => @%s set to %s", varname, s);
			
			env.setVariableValue(varname, s);
			try {
				body.execute(env);
			} catch (BreakScriptException ex) {
				break; //catch and break the loop, moving on
			}
		}
		
		printDebug("construct", "for each end");
	}

	@Override public void verify() throws BadScriptException {
		if (body == null)
			throw new BadScriptException("For-Each has no body!");
	}
	
	@Override public boolean giveNextLine(Executable script) throws BadScriptException {
		if (body == null)
			this.body = script;
		else
			throw new BadScriptException("Loop constructs cannot accept 'else' statements.");
		return true;
	}
	
	@Override public String toString() {
		return "ForEach[]";
	}

	@Override public boolean isConstruct() {return true;}
	@Override public boolean isDirective() {return false;}
	
	@Override public boolean requiresNextLine() {return body == null;}
	@Override public boolean requiresPreviousConstruct() {return false;}

}
