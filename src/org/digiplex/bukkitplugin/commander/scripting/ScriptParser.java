package org.digiplex.bukkitplugin.commander.scripting;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.digiplex.bukkitplugin.commander.scripting.lines.ScriptCommandLine;
import org.digiplex.bukkitplugin.commander.scripting.lines.ScriptConditionLine;
import org.digiplex.bukkitplugin.commander.scripting.lines.ScriptDirectiveEchoLine;
import org.digiplex.bukkitplugin.commander.scripting.lines.ScriptVarAssignmentLine;
import org.digiplex.bukkitplugin.commander.scripting.lines.ScriptVarIncrementLine;
import org.digiplex.bukkitplugin.commander.scripting.lines.conditional.ScriptHasConstruct;
import org.digiplex.bukkitplugin.commander.scripting.lines.conditional.ScriptIfVarCompareConstruct;
import org.digiplex.bukkitplugin.commander.scripting.lines.conditional.ScriptIfVarEqualsConstruct;

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
public class ScriptParser {
	private static enum ParseState {
		NORMAL,
		MAKING_BLOCK,
	}
	
	public static Executable parseScript(String[] script) throws BadScriptException {
		return parseScript(script, 0);
	}
	
	public static Executable parseScript(String script) throws BadScriptException {
		ScriptLine sl = parseScriptLine(script);
		if (sl.isConstruct()) throw new BadScriptException("Statement cannot stand alone, requires block.");
		if (sl.isDirective()) throw new BadScriptException("Directive cannot stand alone.");
		return sl;
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////////////
	
	private static ScriptBlock parseScript(String[] script, int lineno) throws BadScriptException {
		ParseState state = ParseState.NORMAL;
		ArrayList<Executable> lines = new ArrayList<Executable>();
		String curr = null;
		
		ScriptLine lastConstruct = null;
		ArrayList<String> blockLines = new ArrayList<String>();
		int linesave = 0;
		int blockDeep = 0;
		
		
		for (int i = 0; i < script.length; i++, lineno++) {
			switch (state) {
			case NORMAL: {
				curr = script[i].trim();
				
				//check if this line starts a block, either at the end of the line or on it's own 
				if (curr.endsWith("{") && !curr.endsWith("\\{")) { //don't do it if it is escaped
					state = ParseState.MAKING_BLOCK;
					blockDeep = 1; linesave = lineno;
					if (curr.equals("{")) continue; //don't insert this line
					
					curr = curr.substring(0, curr.indexOf('{')-1).trim();
				} else if (curr.equals("}")) {
					throw new BadScriptException("Illegal close brace!", lineno);
				}
				
				//parse script line now
				ScriptLine sl = parseScriptLine(curr);
				
				//determine who owns this line
				if (lastConstruct != null && lastConstruct.requiresNextLine()) {
					//if there is a last construct that requires another line, give it to it
					lastConstruct.giveNextLine(sl);
				} else if (sl.isConstruct()) {
					//otherwise, if this is a construct, set this as the last one, optionally giving it to the previous
					if (sl.requiresPreviousConstruct()) {
						if (lastConstruct == null) throw new BadScriptException("Encountered construct that has no parent!", lineno);
						lastConstruct.giveNextLine(sl);
					}
					lastConstruct = sl;
					lines.add(sl); //also add to list
				} else {
					//otherwise, just add the line
					lines.add(sl);
				}
			} break;
			case MAKING_BLOCK: {
				curr = script[i].trim();
				
				if (curr.endsWith("{") && !curr.endsWith("\\{")) {
					blockDeep++;
				} else if (curr.equals("}")) {
					if (--blockDeep == 0) { //pre-decrement
						state = ParseState.NORMAL;
						String[] str = blockLines.toArray(new String[blockLines.size()]);
						blockLines.clear();
						ScriptBlock sb = parseScript(str, linesave);
						
						if (lastConstruct != null && lastConstruct.requiresNextLine()) {
							//if there is a last construct that requires another line, give it to it
							lastConstruct.giveNextLine(sb);
						} else {
							lines.add(sb);
						}
					}
				} else {
					blockLines.add(curr);
				}
			} break;
			
			}
		}
		if (state != ParseState.NORMAL) {
			if (blockDeep > 0) throw new BadScriptException("Unbalanced braces -- too many open braces!");
			throw new BadScriptException("Ended parsing in illegal state!");
		}
		
		return new ScriptBlock(lines);
	}
	
	/**
	 * Makes a ScriptLine instance based on the parsing of the line. The returned ScriptLine may not be 
	 * completely defined when it is returned; constructs require the line defined after them
	 * @param line
	 * @return
	 * @throws BadScriptException 
	 */
	private static ScriptLine parseScriptLine(String line) throws BadScriptException{
		line = line.trim();
		switch(line.charAt(0)){
		case '[': return parseConstruct(line);
		case '@': return parseVariable(line);
		case '?': return parseDirective(line);
		default: return new ScriptCommandLine(line);
		}
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private static final Pattern CON_OVERALL = Pattern.compile("\\[(\\!?[a-zA-Z]+)\\s+([^\\]]*)\\]");
	private static final Pattern CON_EQUAL = Pattern.compile("\\@(\\w+)\\s+\\=\\s+(.+)");
	private static final Pattern CON_COMPARE = Pattern.compile("\\@(\\w+)\\s+(<|>|<=|>=)\\s+(.+)");
	
	private static ScriptLine parseConstruct(String line) throws BadScriptException{
		Matcher m = CON_OVERALL.matcher(line);
		if (!m.matches()) throw new BadScriptException("Badly formatted Construct!");
		String conname = m.group(1);
		String params = m.group(2);
		
		ScriptConditionLine l = null;
		if (conname.matches("\\!?if")){
			if ( (m = CON_EQUAL.matcher(params)).matches() ) {
				String var = m.group(1);
				String eq = m.group(2);
				
				l = new ScriptIfVarEqualsConstruct(var, eq);
			} else if ( (m = CON_COMPARE.matcher(params)).matches() ) {
				String var = m.group(1);
				String op = m.group(2);
				String eq = m.group(3);
				
				boolean gtb = op.startsWith(">"); //> or >=
				boolean eqb = op.endsWith("="); //>= or <=
				l = new ScriptIfVarCompareConstruct(var, eq, gtb, eqb);
			}
			l.setNotMode(conname.startsWith("!"));
		} else if (conname.matches("\\!?has")) {
			l = new ScriptHasConstruct(params);
		} else {
			throw new BadScriptException("Unknown construct: "+conname);
		}
		return l;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private static final Pattern ASSIGN_LOCAL = Pattern.compile("\\@([a-zA-Z0-9]+)\\s+=\\s+(.*)");
	private static final Pattern ASSIGN_GLOBAL = Pattern.compile("\\@([a-zA-Z0-9]+)\\s+\\:=\\s+(.*)");
	private static final Pattern ASSIGN_INCREMENT = Pattern.compile("\\@([a-zA-Z0-9]+)\\s+\\+\\+");
	private static final Pattern ASSIGN_DECREMENT = Pattern.compile("\\@([a-zA-Z0-9]+)\\s+\\-\\-");
	
	private static ScriptLine parseVariable(String line) throws BadScriptException{
		Matcher m;
		
		ScriptLine l;
		if ( (m = ASSIGN_LOCAL.matcher(line)).matches() ) {
			String variable = m.group(1);
			String literal = m.group(2);
			
			l = new ScriptVarAssignmentLine(variable, literal);
		} else if ( (m = ASSIGN_GLOBAL.matcher(line)).matches() ) {
			String variable = m.group(1);
			String literal = m.group(2);
			
			l = new ScriptVarAssignmentLine(variable, literal, true); //global assignment
		} else if ( (m = ASSIGN_INCREMENT.matcher(line)).matches() ) {
			String variable = m.group(1);
			
			l = new ScriptVarIncrementLine(variable);
		} else if ( (m = ASSIGN_DECREMENT.matcher(line)).matches() ) {
			String variable = m.group(1);
			
			l = new ScriptVarIncrementLine(variable, true); //decrement
		} else {
			throw new BadScriptException("Variable assignment not properly formatted");
		}
		return l;
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
}
