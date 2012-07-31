package org.digiplex.bukkitplugin.commander.scripting.lines.conditional;

import org.digiplex.bukkitplugin.commander.scripting.BadScriptException;
import org.digiplex.bukkitplugin.commander.scripting.ScriptEnvironment;
import org.digiplex.bukkitplugin.commander.scripting.lines.ScriptConditionLine;

public class ScriptHasConstruct extends ScriptConditionLine {
	String permission;
	
	public ScriptHasConstruct(String permission) {
		this.permission = permission;
	}
	
	@Override protected boolean executeCondition(ScriptEnvironment env) throws BadScriptException {
		return env.getCommandSender().hasPermission(permission);
	}
	
	@Override public String toString() {
		return "Condition["+((not)?"!":" ")+"has "+permission+" ]";
	}
	
	@Override public void verify() throws BadScriptException {
		if (permission == null) //probably unneeded
			throw new BadScriptException("Has construct has no permission!", lineno);
		
		super.verify();
	}
}
