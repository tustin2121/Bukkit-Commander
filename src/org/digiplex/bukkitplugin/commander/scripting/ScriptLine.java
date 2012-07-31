package org.digiplex.bukkitplugin.commander.scripting;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.digiplex.bukkitplugin.commander.scripting.lines.ScriptCommandLine;
import org.digiplex.bukkitplugin.commander.scripting.lines.ScriptDirectiveEchoLine;
import org.digiplex.bukkitplugin.commander.scripting.lines.ScriptVarAssignmentLine;
import org.digiplex.bukkitplugin.commander.scripting.lines.ScriptVarIncrementLine;


/**
 * TODO: make script lines abstract, and have a method that will make the line based on what it parses:
 *  if the line begins with [ then it is a construct:
 *     conditional constructs:
 *        [if @var = literal] = if construct: if a variable equals something
 *        [!if @var = literal] = not if construct: if a variable is not equal to something
 *        [has "permission"] = has construct: if the current sender has the specified permission
 *        [!has "permission"] = has not construct: if the current sender does not have the specified permission
 *     loop constructs:
 *        [foreach @var in supportedCollection] = for each construct: for each in a supported collection (players, online players)
 *        [loop @var = # to #] = loop construct: loop a number of times from first # to second # in the given variable
 *        [loop @var = # to # step #] = loop step construct: loop as above, but stepping third # at a time
 *     case constructs:
 *        [switch @var] and [case #], [else] = switch, on the variable
 *        [random # to #] and [case #], [case #-#], [case <#], [case >#] = same as switch, but with a random number 
 *  if the line begins with a @ then it is a variable method:
 *     @var = a number, string, or supported object with a name : assignment
 *     @var := a number, string, etc : global assignment, forcing assignment to original environment, wipes var from all children below
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
	
	
	
	///////////////////////////////////////////////////////////
	
	public abstract boolean isConstruct();
	public abstract boolean isDirective();
	
	public abstract boolean requiresNextLine();
	public abstract boolean requiresPreviousConstruct();
	
	public boolean giveNextLine(Executable script){
		return false;
	}
	
}
