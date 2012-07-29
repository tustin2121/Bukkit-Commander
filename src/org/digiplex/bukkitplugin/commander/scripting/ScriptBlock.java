package org.digiplex.bukkitplugin.commander.scripting;

import java.util.ArrayList;

import org.digiplex.bukkitplugin.commander.CommanderPlugin;

public class ScriptBlock implements Executable {
	private ArrayList<Executable> commands;
	private String alias;
	
	public ScriptBlock(ArrayList<String> sl) throws BadScriptException {
		this(sl.toArray(new String[sl.size()]));
	}
	
	public ScriptBlock(ArrayList<String> sl, String alias) throws BadScriptException {
		this(sl.toArray(new String[sl.size()]));
		this.alias = alias;
	}
	
	public ScriptBlock(String[] scriptLines, String alias) throws BadScriptException {
		this(scriptLines);
		this.alias = alias;
	}
	
	public ScriptBlock(String[] scriptLines) throws BadScriptException {
		commands = new ArrayList<Executable>();
		
		int bracecount = 0;
		ArrayList<String> workingSubblock = null;
		for (String line : scriptLines){ //parse
			line = line.trim();
			if (line.equals("{")){ //block brace
				if (bracecount++ == 0){ //post increment
					workingSubblock = new ArrayList<String>();
				}
			} else if (line.equals("}")){
				if (--bracecount == 0){ //pre increment
					ScriptBlock sb = new ScriptBlock(workingSubblock);
					commands.add(sb);
					workingSubblock = null;
				}
				if (bracecount < 0) throw new BadScriptException("Error parsing script! Unbalanced braces - too many close braces!");
			} else {
				
				
				if (bracecount == 0){
					ScriptLine sl = ScriptLine.parseScriptLine(line);
					commands.add(sl);
				} else {
					workingSubblock.add(line);
				}
				
			}
		}
		//check to make sure we closed all the braces properly
		if (bracecount != 0) throw new BadScriptException("Error parsing script! Unbalanced braces - too many open braces!");
	}
	
	public String getAlias() {return alias;}
	public void setAlias(String alias) {this.alias = alias;}
	
	@Override public String toString() {
		return "Script[alias="+alias+",lines="+commands.size()+"]";
	}
	
	@Override public void execute(ScriptEnvironment env) {
		if (CommanderPlugin.instance.scriptDebugMode)
			CommanderPlugin.Log.info("[Commander:DEBUG:startScript] ");
		
		env = env.getChild(); //get the child environment, to keep scoping
		for (Executable ex : commands){
			ex.execute(env);
		}
		
		if (CommanderPlugin.instance.scriptDebugMode)
			CommanderPlugin.Log.info("[Commander:DEBUG:endScript] ");
	}
	
}
