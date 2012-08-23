package org.digiplex.bukkitplugin.commander.replacement;

import java.util.regex.PatternSyntaxException;

import org.digiplex.bukkitplugin.commander.scripting.Executable;
import org.digiplex.bukkitplugin.commander.scripting.ScriptEnvironment;
import org.digiplex.bukkitplugin.commander.scripting.ScriptParser;
import org.digiplex.bukkitplugin.commander.scripting.exceptions.BadScriptException;

/**
 * Force command, even in chat. This means that the regex will be detected, and won't be replaced,
 * but echoed back while also executing the command. Unless the "cutoff" replacement option is supplied,
 * in which case the player is cut off, the rest of his chat message dropped, and the command executed.
 * @author timpittman
 */
public class ReplacementCommand extends ReplacementPair {
	Executable script;
	int cutoff = -1;

	public ReplacementCommand(String regex, String replacement) throws PatternSyntaxException, BadScriptException {
		super(regex);
		script = ScriptParser.parseScript(replacement); //new ScriptLine(replacement);
	}
	
	public String predicateString() { return "==[cmd]==> "+replacement; }
	
	@Override public int getIntOption(String optionName) {
		if (optionName.equals("cutoff")) return cutoff;
		return super.getIntOption(optionName);
	}
	
	@Override public void executeEffects(ScriptEnvironment e) throws BadScriptException {
		script.execute(e);
	}

	@Override public String executeString(ScriptEnvironment e) {
		Object o = e.getVariableValue("__repl__");
		if (o instanceof String)
			return o.toString();
		return "$0"; //default
	}

}
