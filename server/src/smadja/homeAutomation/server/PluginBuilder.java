package smadja.homeAutomation.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.util.Properties;

public class PluginBuilder {

	public static Plugin build(File confFile, Server server) throws HomeAutomationException {
		if (confFile == null) {
			throw new IllegalArgumentException("Configuration file must not be null");
		}
		Properties prop = new Properties();
		try {
			prop.load(new InputStreamReader(new FileInputStream(confFile), "UTF-8"));
			String className = prop.getProperty("classname");
			Class<?> clazz = Class.forName(className);
			if (Plugin.class.isAssignableFrom(clazz)) {
				File parentFile = confFile.getParentFile();
				Object pluginObj = clazz.getConstructor(String.class, Server.class).newInstance(parentFile.getName(), server);
				Plugin plugin = (Plugin) pluginObj;
				plugin.init(parentFile, prop);
				return plugin;
			} else {
				throw new HomeAutomationException("Invalid plugin class : " + className);
			}
		} catch (IOException e) {
			throw new HomeAutomationException("Cannot read plugin configuration file " + e.getMessage(), e);
		} catch (ClassNotFoundException e) {
			throw new HomeAutomationException("Cannot create plugin class " + e.getMessage(), e);
		} catch (InstantiationException e) {
			throw new HomeAutomationException("Cannot create plugin class " + e.getMessage(), e);
		} catch (IllegalAccessException e) {
			throw new HomeAutomationException("Cannot create plugin class " + e.getMessage(), e);
		} catch (IllegalArgumentException e) {
			throw new HomeAutomationException("Cannot create plugin class " + e.getMessage(), e);
		} catch (InvocationTargetException e) {
			throw new HomeAutomationException("Cannot create plugin class " + e.getMessage(), e);
		} catch (NoSuchMethodException e) {
			throw new HomeAutomationException("Cannot create plugin class " + e.getMessage(), e);
		} catch (SecurityException e) {
			throw new HomeAutomationException("Cannot create plugin class " + e.getMessage(), e);
		}

	}

}
