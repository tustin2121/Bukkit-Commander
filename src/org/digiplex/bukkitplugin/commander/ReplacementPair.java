package org.digiplex.bukkitplugin.commander;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class ReplacementPair {
	private String regexString;
	private Pattern regex;
	private String replacement;
	
	public ReplacementPair(String regex, String replacement) throws PatternSyntaxException{
		this.regex = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		this.regexString = regex;
		this.replacement = replacement;
	}
	
	public Pattern getRegex() {return regex;}
	public String getRegexString() {return regexString;}
	public String getReplacement() {return replacement;}
	
	
	@Override public String toString() {
		return regexString + " ==> "+replacement;
	}
	
	@Override public int hashCode() {
		return regexString.hashCode() + replacement.hashCode();
	}
}
