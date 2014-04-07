package smadja.homeAutomation.model.helper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Properties;

import smadja.homeAutomation.model.HomeAutomationException;
import smadja.homeAutomation.model.HomeElement;
import smadja.homeAutomation.model.mapper.HomeElementDbMapper;

public class HomeElementHelper {

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
				initElement(confFile.getParentFile(), prop, elt);
				return elt;
			} else {
				throw new HomeAutomationException("Invalid plugin class : " + className);
			}
		} catch (IOException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			throw new HomeAutomationException("Cannot create plugin class " + e.getMessage(), e);
		}

	}

	public static void saveConf(HomeElement elt, File confFile) throws HomeAutomationException {
		if (confFile == null) {
			throw new IllegalArgumentException("Configuration file must not be null");
		}
		Properties prop = new Properties();
		try {
			prop.setProperty("classname", elt.getClass().getName());
			prop.setProperty("label", elt.getLabel());
			prop.setProperty("queue", elt.getQueue());
			if (elt.getDbMapper() != null) {
				prop.put("mapper.classname", elt.getDbMapper().getClass().getName());
			}
			prop.store(new OutputStreamWriter(new FileOutputStream(confFile), "UTF-8"), (String) null);
		} catch (IOException e) {
			throw new HomeAutomationException("Cannot create plugin class " + e.getMessage(), e);
		}
	}

	public static HomeElementDbMapper getMapperInstance(String mapperClassname) throws HomeAutomationException {
		try {
			Class<?> clazz = Class.forName(mapperClassname);
			if (HomeElementDbMapper.class.isAssignableFrom(clazz)) {
				Object pluginObj = clazz.newInstance();
				HomeElementDbMapper mapper = (HomeElementDbMapper) pluginObj;
				return mapper;
			} else {
				throw new HomeAutomationException("Invalid mapper class : " + mapperClassname);
			}
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			throw new HomeAutomationException("Cannot create plugin class " + e.getMessage(), e);
		}
	}

	public static void initElement(File configDirectory, Properties confProp, HomeElement elt) throws HomeAutomationException {
		if (configDirectory == null) {
			throw new IllegalArgumentException("Configuration directory must not be null.");
		}
		elt.setConfigDirectory(configDirectory);
		elt.setId(configDirectory.getName());
		elt.setLabel(confProp.getProperty("label"));
		elt.setQueue(confProp.getProperty("queue"));
		String mapperClassname = confProp.getProperty("mapper.classname");
		if (mapperClassname != null && !"".equals(mapperClassname.trim())) {
			elt.setDbMapper(HomeElementHelper.getMapperInstance(mapperClassname));
		}
	}
}
