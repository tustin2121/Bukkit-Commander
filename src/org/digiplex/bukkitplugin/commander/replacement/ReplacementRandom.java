package org.digiplex.bukkitplugin.commander.replacement;

import java.util.Random;
import java.util.regex.PatternSyntaxException;

import org.digiplex.bukkitplugin.commander.scripting.ScriptEnvironment;

public class ReplacementRandom extends ReplacementPair {
	private static final Random random = new Random();
	private String[] replacementArray;
	
	public ReplacementRandom(String regex, String replacement) throws PatternSyntaxException {
		super(regex);
		this.replacement = replacement;
		this.replacementArray = replacement.trim().split(";");
	}
	
	@Override public String predicateString() {
		return "==> one of ("+replacement+")";
	}

	@Override public void executeEffects(ScriptEnvironment e) {}

	@Override public String executeString(ScriptEnvironment e) {
		int rnd = random.nextInt(replacementArray.length);
		return replacementArray[rnd];
	}

}
