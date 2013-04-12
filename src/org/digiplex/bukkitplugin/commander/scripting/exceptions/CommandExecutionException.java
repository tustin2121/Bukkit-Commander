package org.digiplex.bukkitplugin.commander.scripting.exceptions;

/**
 * Represents when an command called from a Commander script throws an exception.
 * This exception class wraps the thrown exception so we can better handle such cases.
 * @author tustin2121
 */
public class CommandExecutionException extends RuntimeException {
	private static final long serialVersionUID = 5306833203475848968L;
	
	public CommandExecutionException() {}
	public CommandExecutionException(String arg0) {
		super(arg0);
	}
	public CommandExecutionException(Throwable arg0) {
		super(arg0);
	}
	public CommandExecutionException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

}
