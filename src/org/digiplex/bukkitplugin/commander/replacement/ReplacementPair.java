package org.digiplex.bukkitplugin.commander.replacement;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.bukkit.event.player.PlayerChatEvent;

public abstract class ReplacementPair {
	protected String regexString;
	protected Pattern regex;
	//private String replacement;
	
	protected ReplacementPair(String regex) throws PatternSyntaxException{
		this.regex = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		this.regexString = regex;
		//this.replacement = replacement;
	}
	
	public Pattern getRegex() {return regex;}
	public String getRegexString() {return regexString;}
	//public String getReplacement() {return replacement;}
	
	public abstract String executeReplacement(PlayerChatEvent e);
	
	
	@Override public String toString() {
		return "ReplacementPair ["+regexString+"]";
	}
	@Override public int hashCode() {
		return regexString.hashCode();
	}
}
