package org.digiplex.bukkitplugin.commander.replacement;

import java.util.regex.PatternSyntaxException;

import org.digiplex.bukkitplugin.commander.scripting.BadScriptException;
import org.digiplex.bukkitplugin.commander.scripting.ScriptBlock;
import org.digiplex.bukkitplugin.commander.scripting.ScriptEnvironment;

public class ReplacementScript extends ReplacementPair {
	private String scriptAlias;
	private ScriptBlock block;
	
	public ReplacementScript(String regex, ScriptBlock block) throws PatternSyntaxException {
		super(regex);
		this.block = block;
		this.scriptAlias = regex;
	}
	
	public ReplacementScript(String regex, ScriptBlock block, String alias) {
		super (regex);
		this.block = block;
		this.scriptAlias = alias;
	}
	
	public String predicateString() { return "==[script]==> { "+scriptAlias+" }"; }
	
	@Override public void executeEffects(ScriptEnvironment e) throws BadScriptException {
		block.execute(e);
	}
	
	@Override public String executeString(ScriptEnvironment e) {
		return "";
	}

}
