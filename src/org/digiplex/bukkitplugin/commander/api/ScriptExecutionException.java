package org.digiplex.bukkitplugin.commander.api;

public class ScriptExecutionException extends Exception {
	private static final long serialVersionUID = -3356952384739680150L;
	public ScriptExecutionException() {}
	public ScriptExecutionException(String msg) {super(msg);}
	public ScriptExecutionException(Throwable ex) {super(ex);}
	public ScriptExecutionException(String msg, Throwable ex) {super(msg, ex);}
}
