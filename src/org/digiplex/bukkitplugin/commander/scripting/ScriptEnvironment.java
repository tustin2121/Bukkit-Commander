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
	private HashMap<String, Object> vars;
	
	private Server server;
	private CommandSender commandSender;
	private EchoControl wrappedSender;
	private MatchResult match;
	
	public ScriptEnvironment(){
		vars = new HashMap<String, Object>();
	}
	
	public Object getVariableValue(String name){
		return vars.get(name);
	}
	public void setVariableValue(String name, Object obj){
		vars.put(name, obj);
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
			case '\\': i++; continue; //skip over escape character
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
						if (Character.isWhitespace(c)) break;
					}
					varb.append(c);
				}
				Object varval = this.getVariableValue(varb.toString());
				sb.append(varval);
				if (!inBrace) sb.append(' '); //since we consumed the space above, put it back in 
				continue;
			default:
				sb.append(c);
			}
			
		}
/*		
		if (match != null){ //move the group replacement to here, since it causes some trouble...
			str = str.replaceAll("\\$0", match.group())
					.replaceAll("\\$1", match.group(1)).replaceAll("\\$2", match.group(2)).replaceAll("\\$3", match.group(3))
					.replaceAll("\\$4", match.group(4)).replaceAll("\\$5", match.group(5)).replaceAll("\\$6", match.group(6))
					.replaceAll("\\$7", match.group(7)).replaceAll("\\$8", match.group(8)).replaceAll("\\$9", match.group(9));
		}
		
		if (commandSender instanceof Player) {
			//player name. Replace "$p" but not "\$p"
			str = str.replaceAll("(?<!\\\\)\\$p", commandSender.getName()).replaceAll("\\\\$p", "$p"); 
		}
*/	
		return sb.toString();
	}
}
