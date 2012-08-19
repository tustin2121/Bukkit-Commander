package org.digiplex.bukkitplugin.commander.api;

import org.digiplex.bukkitplugin.commander.replacement.ReplacementPair;

/**
 * And Opaque wrapper around Commander's replacement pairs, 
 * for use by plugins outside of the Commander engine.
 * @author Tim
 *
 */
public class CmdrReplacement {
	private ReplacementPair pair;
	
	//default, accessible from the package only
	CmdrReplacement(ReplacementPair rp) {
		this.pair = rp;
	}
	
	
}
