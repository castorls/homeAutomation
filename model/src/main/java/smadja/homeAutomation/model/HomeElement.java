package smadja.homeAutomation.model;

import java.io.File;
import java.util.Properties;

public abstract class HomeElement {

	private String id;
	private String label;
	private String queue;
	private File configDirectory;

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
	
	
	public void init(File configDirectory, Properties confProp){		
		if(configDirectory == null){
			throw new IllegalArgumentException("Configuration directory must not be null.");
		}
		this.configDirectory = configDirectory;
		this.id = configDirectory.getName();
		this.label = confProp.getProperty("label");
		this.queue = confProp.getProperty("queue");
	}
}
