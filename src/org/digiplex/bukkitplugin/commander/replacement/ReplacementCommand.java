package org.digiplex.bukkitplugin.commander.replacement;

import java.util.Properties;
import java.util.regex.PatternSyntaxException;

import org.digiplex.bukkitplugin.commander.scripting.ScriptEnvironment;
import org.digiplex.bukkitplugin.commander.scripting.ScriptLine;

/**
 * Force command, even in chat. This means that the regex will be detected, and won't be replaced,
 * but echoed back while also executing the command. Unless the "cutoff" replacement option is supplied,
 * in which case the player is cut off, the rest of his chat message dropped, and the command executed.
 * @author timpittman
 */
public class ReplacementCommand extends ReplacementPair {
	ScriptLine script;
	boolean cutoff = false;

	public ReplacementCommand(String regex, String replacement, String options) throws PatternSyntaxException {
		super(regex);
		script = new ScriptLine(replacement);
		if (options != null && !options.isEmpty()){
			Properties p = parseOpts(options);
			cutoff = Boolean.parseBoolean(p.getProperty("cutoff", "false"));
		//	CommanderPlugin.Log.info("["+options+"] cutoff = "+cutoff);
		}
	}
	
	public ReplacementCommand(String regex, String replacement) {
		this(regex, replacement, null);
	}
	
	public String predicateString() { return "==[cmd]==> "+replacement; }
	
	@Override public boolean playerWillVanish() {
		return cutoff;
	}

	@Override public void executeEffects(ScriptEnvironment e) {
		script.execute(e);
	}

	@Override public String executeString(ScriptEnvironment e) {
		return "$0";
	}

}
