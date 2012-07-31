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

}
