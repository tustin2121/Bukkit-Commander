package org.digiplex.bukkitplugin.commander.replacement;

import java.util.Properties;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.digiplex.bukkitplugin.commander.scripting.BadScriptException;
import org.digiplex.bukkitplugin.commander.scripting.ScriptEnvironment;

public abstract class ReplacementPair {
	protected String regexString;
	protected Pattern regex;
	protected String replacement;
	
	protected ReplacementPair(String regex) throws PatternSyntaxException{
		this.regex = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		this.regexString = regex;
		//this.replacement = replacement;
	}
	
	public Pattern getRegex() {return regex;}
	public String getRegexString() {return regexString;}
	public void setRegexOptions(String regexOpts) {
		if (regexOpts == null || regexOpts.isEmpty()) return;
		
		char[] ro = regexOpts.toCharArray();
		boolean caseSensitive = false;
		boolean literal = false;
		
		for (int i = 0; i < ro.length; i++){
			switch(ro[i]){
			case 's': caseSensitive = true; break;
			case 'l': literal = true; break;
			}
		}
		
		//recompile pattern with the new options
		regex = Pattern.compile(regexString, 
					((caseSensitive)?0:Pattern.CASE_INSENSITIVE) |
					((literal)?Pattern.LITERAL:0)
				);
	}
	
	/** Performs effects of this replacement for replaced commands. 
	 * Execute in a command replacement context. 
	 * @throws BadScriptException */
	public abstract void executeEffects(ScriptEnvironment e) throws BadScriptException;
	/** Retrieves a replacement string for insertion into chat. 
	 * Execute in a chat or string replacement context. */
	public abstract String executeString(ScriptEnvironment e);
	
	public boolean playerWillVanish() { return false; }
	public String predicateString() { return "==> "+replacement; }
	
	@Override public String toString() {
		return "ReplacementPair ["+regexString+"]";
	}
	@Override public int hashCode() {
		return regexString.hashCode();
	}

	protected Properties parseOpts(String opts) {
		Properties p = new Properties();
		String[] kvpairs = opts.split(",");
		
		for (String kv : kvpairs){
			if (kv.contains("=")){
				String[] o = kv.split("=");
				p.setProperty(o[0], o[1]);
			} else {
				p.setProperty(kv, "true");
			}
		}
		
		return p;
	}
	
	// Abstract options getters for Replacement Pairs - to get options specified in the [brackets]
	public boolean getBooleanOption(String optionName){ return false; }
	public int getIntOption(String optionName){ return 0; }
}
