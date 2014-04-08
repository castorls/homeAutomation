package smadja.homeAutomation.model;

import java.io.File;

import org.apache.log4j.Logger;

import smadja.homeAutomation.model.helper.HomeElementHelper;
import smadja.homeAutomation.model.mapper.HomeElementDbMapper;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(  
	    use = JsonTypeInfo.Id.NAME,  
	    include = JsonTypeInfo.As.PROPERTY,  
	    property = "type")  
	@JsonSubTypes({  
	    @Type(value = GenericSensor.class, name = "GenericSensor"),  
	    @Type(value = GenericActuator.class, name = "GenericActuator") }) 
public abstract class HomeElement implements Comparable<HomeElement> {

	private static Logger logger = Logger.getLogger(HomeElement.class);
	
	private String id;
	private String label;
	private String queue;
	private File configDirectory;
	private String dbMapperClass;
	@JsonIgnore
	private HomeElementDbMapper dbMapper;

	public HomeElement() {

	}

	public HomeElement(HomeElement other) {
		this.id = other.id == null ? null : new String(other.id);
		this.label = other.label == null ? null : new String(other.label);
		this.queue = other.queue == null ? null : new String(other.queue);
		this.configDirectory = other.configDirectory;
		this.dbMapper = other.dbMapper;
		this.dbMapperClass = other.dbMapperClass;
	}

	public HomeElementDbMapper getDbMapper() {
		return dbMapper;
	}

	public void setDbMapper(HomeElementDbMapper dbMapper) {
		this.dbMapper = dbMapper;
		this.dbMapperClass = dbMapper == null ? null:dbMapper.getClass().getName();
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

	public boolean shouldSendAction(String action) {
		// by default accept all actions
		return true;
	}

	public void setLastedMessageId(String action, String correlationId) {
		// nothing to do
	}

	public String getDbMapperClass() {
		return dbMapperClass;
	}

	public void setDbMapperClass(String dbMapperClass) {
		this.dbMapperClass = dbMapperClass;
		try {
			this.dbMapper = HomeElementHelper.getMapperInstance(dbMapperClass);
		} catch (HomeAutomationException e) {
			logger.warn("Cannot create dbMapper instance for "+dbMapperClass, e);
		}
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		HomeElement other = (HomeElement) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}
