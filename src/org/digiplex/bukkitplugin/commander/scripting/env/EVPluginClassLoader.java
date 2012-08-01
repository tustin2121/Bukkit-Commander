package org.digiplex.bukkitplugin.commander.scripting.env;

/**
 * A special class loader that gets a folder in which to find jar files which hold classes to load.
 * At any point, more jar files may be added to its list, though none may be removed.
 * 
 * TODO 
 * @author tpittman
 */
public class EVPluginClassLoader extends ClassLoader {
	public EVPluginClassLoader() {
		super(ClassLoader.getSystemClassLoader());
	}
	
	@Override protected Class<?> findClass(String name) throws ClassNotFoundException {
		return super.findClass(name); //TODO
	}
}
