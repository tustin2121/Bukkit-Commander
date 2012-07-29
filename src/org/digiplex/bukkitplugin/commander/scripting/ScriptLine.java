package org.digiplex.bukkitplugin.commander.scripting;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.digiplex.bukkitplugin.commander.scripting.lines.ScriptCommandLine;
import org.digiplex.bukkitplugin.commander.scripting.lines.ScriptDirectiveEchoLine;
import org.digiplex.bukkitplugin.commander.scripting.lines.ScriptVarAssignmentLine;


/**
 * TODO: make script lines abstract, and have a method that will make the line based on what it parses:
 *  if the line begins with [ then it is a construct:
 *     if constructs:
 *        [if @var = literal] = if construct: if a variable equals something
 *        [!if @var = literal] = not if construct: if a variable is not equal to something
 *        [has "permission"] = has construct: if the current sender has the specified permission
 *        [!has "permission"] = has not contruct: if the current sender does not have the specified permission
 *     for constructs:
 *        [foreach @var in supportedCollection] = for each construct: for each in a supported collection (players, online players)
 *     switch constructs:
 *        [switch @var] and [case #], [else] = switch, on the variable
 *        [random # to #] and [case #], [case #-#], [case <#], [case >#] = same as switch, but with a random number 
 *  if the line begins with a @ then it is a variable method:
 *     @var = a number, string, or supported object with a name : assignment
 *     @var++ : increase var if number
 *     
 *  if the line begins with a ? then it is a scripting environment directive
 *     ?echo on/off = turns on/off echoing back messages from commands - the built-in echo command ignores it
 *  if the line begins with "say", 
 *  else:
 *     the line is a normal command to be executed by the current CommandSender, or mod if it begins with "sudo" 
 * @author timpittman
 */
public abstract class ScriptLine implements Executable {
	
	/**
	 * Makes a ScriptLine instance based on the parsing of the line. The returned ScriptLine may not be 
	 * completely defined when it is returned; constructs require the line defined after them
	 * @param line
	 * @return
	 */
	public static ScriptLine parseScriptLine(String line){
		line = line.trim();
		switch(line.charAt(0)){
	//	case '[': return parseConstruct(line);
	//	case '@': return parseVariable(line);
		case '?': return parseDirective(line);
		default: return new ScriptCommandLine(line);
		}
	}
	
	private static ScriptLine parseConstruct(String line){
		final Pattern p = Pattern.compile("\\[([!a-zA-Z]+) ([^\\]]*)\\]", Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(line);
		String conname = m.group(1);
		String params = m.group(2);
		
		ScriptLine l = null;
		if (conname.matches("\\!?if")){
			
		}
		return l;
	}
	
	private static ScriptLine parseVariable(String line){
		final Pattern p = Pattern.compile("\\@([a-zA-Z][a-zA-Z0-9]*)\\s+=\\s+(.*)", Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(line);
		String variable = m.group(1);
		String literal = m.group(2);
		
		ScriptLine l = new ScriptVarAssignmentLine(variable, literal);
		return l; //TODO more
	}
	
	private static ScriptLine parseDirective(String line){
		final Pattern p = Pattern.compile("\\?(.*)", Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(line);
		String dir = m.group(1);
		
		ScriptLine l = null;
		if (dir.startsWith("echo")){
			boolean b = dir.matches("(?i)echo (on|true|1|yes)");
			l = new ScriptDirectiveEchoLine(b);
		}
		return l;
	}
	
	///////////////////////////////////////////////////////////
	
	public abstract boolean isConstruct();
	public abstract boolean isDirective();
	
	public abstract boolean requiresNextLine();
	public abstract boolean requiresPreviousConstruct();
	
	public boolean giveNextLine(Executable script){
		return false;
	}
	
}
