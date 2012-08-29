package org.digiplex.bukkitplugin.commander.api;

/**
 * Represents an exception thrown from plugin EVMs because the given path was too short.
 * Commander will take the message and print it to the console before returning null,
 * which is standard handling procedure. 
 * @author tpittman
 * @since 2.0.1
 */
public class BadEVPathException extends Exception {
	private static final long serialVersionUID = -8086988428281161944L;
	public BadEVPathException() {}
	public BadEVPathException(String arg0) {super(arg0);}
}
