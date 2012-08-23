package org.digiplex.bukkitplugin.commander.api;

import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.command.CommandSender;
import org.digiplex.bukkitplugin.commander.replacement.ReplacementPair;
import org.digiplex.bukkitplugin.commander.scripting.ScriptEnvironment;
import org.digiplex.bukkitplugin.commander.scripting.exceptions.BadScriptException;

/**
 * And Opaque wrapper around Commander's replacement pairs, 
 * for use by plugins outside of the Commander engine.
 * @author Tim
 *
 */
public class CmdrReplacement {
	private ReplacementPair pair;
	
	//default, accessible from the package only
	CmdrReplacement(ReplacementPair rp) {
		this.pair = rp;
	}
	
	public Pattern getRegex() {return pair.getRegex(); }
	public String getRegexString() {return pair.getRegexString(); }
	
	public void checkAndExecute(CommandSender sender, String str) throws ScriptExecutionException {
		Matcher m = pair.getRegex().matcher(str);
		if (m.matches()) {
			ScriptEnvironment env = new ScriptEnvironment(); {
				env.setCommandSender(sender);
				env.setServer(sender.getServer());
				env.setMatch(m.toMatchResult());
			}
			try {
				pair.executeEffects(env);
			} catch (BadScriptException e) {
				throw new ScriptExecutionException(e);
			}
		}
	}
	
	public void executeEffects(CommandSender sender, MatchResult matcher) throws ScriptExecutionException {
		ScriptEnvironment env = new ScriptEnvironment(); {
			env.setCommandSender(sender);
			env.setServer(sender.getServer());
			env.setMatch(matcher);
		}
		try {
			pair.executeEffects(env);
		} catch (BadScriptException e) {
			throw new ScriptExecutionException(e);
		}
	}
	
	public String executeString(CommandSender sender, MatchResult matcher) {
		ScriptEnvironment env = new ScriptEnvironment(); {
			env.setCommandSender(sender);
			env.setServer(sender.getServer());
			env.setMatch(matcher);
		}
		return pair.executeString(env);
	}
}
