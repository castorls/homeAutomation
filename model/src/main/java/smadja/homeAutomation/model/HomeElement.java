package smadja.homeAutomation.model;

import java.io.File;
import java.util.Properties;

import smadja.homeAutomation.model.mapper.HomeElementDbMapper;

public abstract class HomeElement implements Comparable<HomeElement> {

	private String id;
	private String label;
	private String queue;
	private File configDirectory;
	private HomeElementDbMapper dbMapper;

	public HomeElement() {

	}

	public HomeElement(HomeElement other) {
		this.id = other.id == null ? null : new String(other.id);
		this.label = other.label == null ? null : new String(other.label);
		this.queue = other.queue == null ? null : new String(other.queue);
		this.configDirectory = other.configDirectory;
		this.dbMapper = other.dbMapper;
	}

	public HomeElementDbMapper getDbMapper() {
		return dbMapper;
	}

	public void setDbMapper(HomeElementDbMapper dbMapper) {
		this.dbMapper = dbMapper;
	}

	public File getConfigDirectory() {
		return configDirectory;
	}

	public void setConfigDirectory(File configDirectory) {
		this.configDirectory = configDirectory;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getQueue() {
		return queue;
	}

	public void setQueue(String queue) {
		this.queue = queue;
	}

	public void init(File configDirectory, Properties confProp) throws HomeAutomationException {
		if (configDirectory == null) {
			throw new IllegalArgumentException("Configuration directory must not be null.");
		}
		this.configDirectory = configDirectory;
		this.id = configDirectory.getName();
		this.label = confProp.getProperty("label");
		this.queue = confProp.getProperty("queue");
		String mapperClassname = confProp.getProperty("mapper.classname");
		if (mapperClassname != null && !"".equals(mapperClassname.trim())) {
			try {
				Class<?> clazz = Class.forName(mapperClassname);
				if (HomeElementDbMapper.class.isAssignableFrom(clazz)) {
					Object pluginObj = clazz.newInstance();
					HomeElementDbMapper mapper = (HomeElementDbMapper) pluginObj;
					this.dbMapper = mapper;
				} else {
					throw new HomeAutomationException("Invalid mapper class : " + mapperClassname);
				}
			} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
				throw new HomeAutomationException("Cannot create plugin class " + e.getMessage(), e);
			}
		}
	}

	public boolean shouldSendAction(String action) {
		// by default accept all actions
		return true;
	}

	public void setLastedMessageId(String action, String correlationId) {
		// nothing to do
	}

	@Override
	public int compareTo(HomeElement o) {
		if (o == null) {
			return -1;
		}
		String oId = o.getId();
		if (this.id == null) {
			return oId == null ? 0 : 1;
		}
		return id.compareTo(oId);
	}

}
