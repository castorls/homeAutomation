package smadja.homeAutomation.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

import smadja.homeAutomation.model.HomeElement;

public class HomeElementBuilder {

	public static HomeElement build(File confFile) throws HomeAutomationException {
		if (confFile == null) {
			throw new IllegalArgumentException("Configuration file must not be null");
		}
		Properties prop = new Properties();
		try {
			prop.load(new InputStreamReader(new FileInputStream(confFile), "UTF-8"));
			String className = prop.getProperty("classname");
			Class<?> clazz = Class.forName(className);
			if (HomeElement.class.isAssignableFrom(clazz)) {
				Object pluginObj = clazz.newInstance();
				HomeElement elt = (HomeElement) pluginObj;
				elt.init(confFile.getParentFile(),prop);
				return elt;
			}
			else{
				throw new HomeAutomationException("Invalid plugin class : "+className);
			}
		} catch (IOException e) {
			throw new HomeAutomationException("Cannot read plugin configuration file " + e.getMessage(), e);
		} catch (ClassNotFoundException e) {
			throw new HomeAutomationException("Cannot create plugin class " + e.getMessage(), e);
		} catch (InstantiationException e) {
			throw new HomeAutomationException("Cannot create plugin class " + e.getMessage(), e);
		} catch (IllegalAccessException e) {
			throw new HomeAutomationException("Cannot create plugin class " + e.getMessage(), e);
		}

	}

}
