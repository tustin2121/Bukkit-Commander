package org.digiplex.bukkitplugin.commander.replacement;

import org.bukkit.event.player.PlayerChatEvent;

public class ReplacementString extends ReplacementPair {
	protected String replacement;
	
	public ReplacementString(String regex, String replacement) {
		super(regex);
		this.replacement = replacement;
	}

	@Override public String executeReplacement(PlayerChatEvent e) {
		if (e == null) return replacement; //NOTE: e is null in case of console commands
		
		return replacement
			.replaceAll("(?<!\\\\)\\$p", e.getPlayer().getName()); //replace "$p" but not "\$p"
		
		/*
		 * Note: the regex "(?<!a)b" tests to see if there is a 'b' that is not
		 * preceeded by an 'a'. This is called a negative look-back. There are also
		 * look-aheads in the form "(?=positive lookahead)", "(?!negative lookahead)"
		 * and "(?<=positive lookbehind)", "(?<!negative lookbehind)"
		 */
	}

}
