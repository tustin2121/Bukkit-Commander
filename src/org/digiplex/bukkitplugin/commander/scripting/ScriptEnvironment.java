package org.digiplex.bukkitplugin.commander.scripting;

import java.util.HashMap;
import java.util.regex.MatchResult;

import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
	private MatchResult match;
	
	public ScriptEnvironment(){
		vars = new HashMap<String, Object>();
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
	private boolean setVarFromChild(String name, Object obj){
		//special function that sets variables only if they already exist
		if (vars.containsKey(name)) {
			vars.put(name, obj); return true;
		} else return false;
	}
	
	
	public Server getServer() {return server;}
	public void setServer(Server server) {this.server = server;}
	
	public CommandSender getCommandSender() {
	//	return commandSender;
		return wrappedSender;
	}
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
				case 'p': case 'P':
					if (!(commandSender instanceof Player)) {
						sb.append('$').append(c); break;
					}
					sb.append(commandSender.getName());
				}
				continue;
			case '@': i++; //skip over character
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
				continue;
			default:
				sb.append(c);
			}			
		}

		return sb.toString();
	}
}
