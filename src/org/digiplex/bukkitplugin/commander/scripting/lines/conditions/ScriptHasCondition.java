package org.digiplex.bukkitplugin.commander.scripting.lines.conditions;

import org.bukkit.entity.Player;
import org.digiplex.bukkitplugin.commander.scripting.ScriptEnvironment;
import org.digiplex.bukkitplugin.commander.scripting.exceptions.BadScriptException;

public class ScriptHasCondition extends ScriptCondition {
	String player = null, permission;
	
	public ScriptHasCondition(String player, String permission) throws BadScriptException {
		if (permission == null || permission.isEmpty()) 
			throw new BadScriptException("Has condition has no permission!");
		
		this.player = player;
		this.permission = permission;
	}
	
	@Override public boolean executeCondition(ScriptEnvironment env) throws BadScriptException {
		if (player == null)
			return env.getCommandSender().hasPermission(permission);
		else {
			Player p = env.getServer().getPlayer(player);
			if (p == null) 
				return this.inNotMode(); //this makes certain that false is returned even if we are not-ing the result
			return p.hasPermission(permission);
		}
	}
	
	@Override public String toString() {
		return "Condition["+((not)?"!":" ")+" "+player+" has "+permission+" ]";
	}
}
