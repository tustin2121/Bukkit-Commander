package org.digiplex.bukkitplugin.commander.replacement;

import org.digiplex.bukkitplugin.commander.scripting.ScriptEnvironment;
import org.digiplex.bukkitplugin.commander.scripting.exceptions.BadScriptException;

public class ReplacementString extends ReplacementPair {
	
	public ReplacementString(String regex, String replacement) {
		super(regex);
		this.replacement = replacement;
	}
	
	@Override public void executeEffects(ScriptEnvironment e) {
	//	String command = e.getMatcher().replaceFirst(this.executeString(e));
	//	e.getServer().dispatchCommand(e.getCommandSender(), command);
	}
	
	@Override public String executeString(ScriptEnvironment e) throws BadScriptException {
		return e.substituteTokens(replacement);
	}

}
