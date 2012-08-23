package org.digiplex.bukkitplugin.commander.scripting;

import java.util.HashMap;
import java.util.List;
import java.util.regex.MatchResult;

import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.digiplex.bukkitplugin.commander.scripting.env.GameEnvironment;

/**
 * This object holds the environment that scripts are executed in. Scripts use the objects
 * in this to execute.
 * @author timpittman
 */
public class ScriptEnvironment {
	private ScriptEnvironment parent;
	private HashMap<String, Object> vars;
	
	private Server server;
	private CommandSender commandSender;
	private EchoControl wrappedSender;
	private MatchResult match = null;
	
	private int looplim = 200;
	private int runlim = 10, runcount = 0;
	private boolean continueOnError = false;
	private Object commandReturn;
	private boolean commandFound;
	private Exception commandError;
	
	private HashMap<String, List<String>> collections;
	
	public ScriptEnvironment(){
		vars = new HashMap<String, Object>();
		collections = new HashMap<String, List<String>>(2);
	}
	
	public Object getVariableValue(String name){
		Object o = vars.get(name);
		if (o == null && parent != null)
			return parent.getVariableValue(name); //tail call?
		else
			return o;
	}
	
	public void setVariableValue(String name, Object obj){
		//check if the parent already has this variable defined, and if not, then set it ourselves
		if (parent == null || !parent.setVarFromChild(name, obj))
			vars.put(name, obj);
	}
	public void setVariableGlobally(String name, Object obj) {
		if (parent != null) {
			vars.remove(name); //for it to be accessed globally, we need to remove all instances below it
			parent.setVariableGlobally(name, obj);
		} else {
			vars.put(name, obj);
		}
	}
	
	private boolean setVarFromChild(String name, Object obj){
		//special function that sets variables only if they already exist
		if (vars.containsKey(name)) {
			vars.put(name, obj); return true;
		} else if (parent != null) {
			return parent.setVarFromChild(name, obj);
		} else return false;
	}
	
	public String pushCollection(List<String> collection){
		String id = "{s"+Integer.toHexString(collection.hashCode())+"}";
		setCollectionForId(id, collection);
		return id;
	}
	public void setCollectionForId(String id, List<String> collection){
		ScriptEnvironment p = this;
		while (p.parent != null) //go to top-most environment
			p = p.parent;
		p.collections.put(id, collection); //collections are global, its the variables that might lose scope
	}
	public List<String> getCollection(String id) {
		ScriptEnvironment p = parent;
		while (p.parent != null) //go to top-most environment
			p = p.parent;
		return p.collections.get(id);
	}
	
	public Server getServer() {return server;}
	public void setServer(Server server) {this.server = server;}
	
	public CommandSender getCommandSender() {return wrappedSender;}
	public void setCommandSender(CommandSender commandSender) {
		this.commandSender = commandSender;
		if (commandSender instanceof Player)
			this.wrappedSender = new EchoControlPlayer((Player)commandSender);
		else
			this.wrappedSender = new EchoControlSender(commandSender);
	}
	
	public MatchResult getMatch() {return match;}
	public void setMatch(MatchResult match) {this.match = match;}
	
	public Player getPlayer() {
		if (commandSender instanceof Player) return (Player)wrappedSender;
		return null;
	}
	
	
	public void setCommandResults(boolean found) {
		this.commandFound = found;
		this.commandError = null;
		this.commandReturn = null;
	}
	public void setCommandResultsFound(Object returnobj) {
		this.commandFound = true;
		this.commandError = null;
		this.commandReturn = returnobj;
	}
	public void setCommandResultsError(Exception ex) {
		this.commandFound = false;
		this.commandError = ex;
		this.commandReturn = null;
	}
	
	public Object getCommandReturn() { return commandReturn; }
	public void setCommandReturn(Object commandReturn) { this.commandReturn = commandReturn; }
	
	public boolean getCommandFound() {return commandFound;}
	public void setCommandFound(boolean commandFound) {this.commandFound = commandFound;}
	
	public boolean didLastCommandError() { return commandError != null; }
	public Exception getCommandError() {return commandError;}
	public void setCommandError(Exception commandError) {this.commandError = commandError;}
	
	public boolean shouldContinueOnError() {return continueOnError;}
	public void setContinueOnError(boolean continueOnError) {this.continueOnError = continueOnError;}
	
	public int getLoopLimit() {return looplim;}
	public void setLoopLimit(int looplim) {this.looplim = looplim;}
	
	public int getRunLimit() {return runlim;}
	public void setRunLimit(int runlim) {this.runlim = runlim;}
	public int getCurrentRunCount() {return runcount;}
	public boolean incrementRunCount() { return ++runcount >= runlim;}
	public boolean decrementRunCount() { return --runcount <= 0; }
	
	////////////////////////////////////////////////
	
	/**
	 * Returns a child environment for this instance. Children will inherit all variables of their
	 * parents and will modify existing variables, but will create their own variables, which will
	 * be lost when the child is no longer used.
	 * @return
	 */
	public ScriptEnvironment getChild() {
		ScriptEnvironment child = new ScriptEnvironment();
		child.commandSender = this.commandSender;
		child.server = this.server;
		child.match = this.match;
		child.wrappedSender = this.wrappedSender;
		
		child.continueOnError = this.continueOnError;
		child.looplim = this.looplim;
		child.runlim = this.runlim;
		child.runcount = this.runcount;
		
		child.commandError = this.commandError;
		child.commandFound = this.commandFound;
		child.commandReturn = this.commandReturn;
		
		child.parent = this;
		return child;
	}
	
	////////////////////////////////////////////////
	
	public String substituteTokens(String str){
		/* Note: the regex "(?<!a)b" tests to see if there is a 'b' that is not
		 * preceeded by an 'a'. This is called a negative look-back. There are also
		 * look-aheads in the form "(?=positive lookahead)", "(?!negative lookahead)"
		 * and "(?<=positive lookbehind)", "(?<!negative lookbehind)"
		 */
		
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < str.length(); i++){
			char c = str.charAt(i);
			switch (c){
			case '\\': i++; //skip over escape character
				c = str.charAt(i);
				sb.append(c);
				continue; 
			case '$': i++; //skip over character
				c = str.charAt(i);
				
				switch(c){
				case '0': case '1': case '2': case '3': case '4': 
				case '5': case '6': case '7': case '8': case '9':
					if (match == null) {
						sb.append('$').append(c);
						break;
					}
					int gnum = c - '0'; //convert the char to the actual int value
					sb.append(match.group(gnum));
					break;
				case 'p': case 'P':
					if (!(commandSender instanceof Player)) {
						sb.append('$').append(c); break;
					}
					sb.append(commandSender.getName());
					break;
				case '(': { //environment properties
					i++; //skip over brace
					StringBuffer varb = new StringBuffer();
					c = str.charAt(i);
					
					for (; i < str.length(); i++){ //grab the variable name
						c = str.charAt(i);
						if (c == ')') break;
						varb.append(c);
					}
					Object varval = GameEnvironment.getEnvironmentVariable(varb.toString(), this);
					if (varval == null) sb.append("\u00D8"); //special "empty set" character for null values
					else if (varval instanceof List) {
						@SuppressWarnings("unchecked") 
						String id = pushCollection((List<String>) varval);
						
						sb.append(id);
					}
					else sb.append(varval);
				} break;
				}
				continue;
			case '@': { i++; //skip over character
				StringBuffer varb = new StringBuffer();
				c = str.charAt(i);
				boolean inBrace = (c == '{');
				if (inBrace) i++; //skip over {
				
				for (; i < str.length(); i++){ //grab the variable name
					c = str.charAt(i);
					if (inBrace) {
						if (c == '}') break;
					} else {
						if (!Character.isLetterOrDigit(c)) break;
					}
					varb.append(c);
				}
				Object varval = this.getVariableValue(varb.toString());
				if (varval == null) sb.append("\u00D8"); //special "empty set" character for null values
				else sb.append(varval);
				if (!inBrace && i != str.length()) sb.append(c); //since we consumed the dividing character above, put it back in
					//also, check to see if we didn't break because we ran past the end
			} continue;
			default:
				sb.append(c); break;
			}			
		}

		return sb.toString();
	}
}
