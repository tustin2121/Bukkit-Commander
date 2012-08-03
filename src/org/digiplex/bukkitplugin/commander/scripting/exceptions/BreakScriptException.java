package org.digiplex.bukkitplugin.commander.scripting.exceptions;

/**
 * Yes, this is REALLY BAD FORM, but without adding a whole bunch of return checks in everything,
 * this is the easiest way to implement a loop breaking or script breaking line.
 * 
 * This exception is thrown when execution hits a [break] statement. This will throw this 
 * exception up to the next loop construct, which will catch it and continue on its way.
 * If this is caught at the top level of the script, everything will continue on as if the
 * script returned normally.
 * @author tpittman
 */
public class BreakScriptException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	public BreakScriptException() {}
}
