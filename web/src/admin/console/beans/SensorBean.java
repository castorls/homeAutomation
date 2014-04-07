package admin.console.beans;

import java.io.File;

import org.apache.log4j.Logger;

import smadja.homeAutomation.model.GenericSensor;
import smadja.homeAutomation.model.HomeAutomationException;
import smadja.homeAutomation.model.helper.HomeElementHelper;
import smadja.homeAutomation.model.mapper.HomeElementDbMapper;

public class SensorBean {

	private static Logger logger = Logger.getLogger(SensorBean.class);

	private String id;
	private String label;
	private String queue;
	private Double value;
	private File configDirectory;
	private Class<? extends HomeElementDbMapper> dbMapperClass;
	private boolean newInstance = false;

	public SensorBean() {
		this.newInstance = true;
	}

	public SensorBean(GenericSensor sensor) {
		this.id = sensor.getId() == null ? null : new String(sensor.getId());
		this.label = sensor.getLabel() == null ? null : new String(sensor.getLabel());
		this.queue = sensor.getQueue() == null ? null : new String(sensor.getQueue());
		this.value = sensor.getValue() == null ? null : new Double(sensor.getValue());
		this.configDirectory = sensor.getConfigDirectory() == null ? null : new File(sensor.getConfigDirectory().getAbsolutePath());
		this.dbMapperClass = sensor.getDbMapper() == null ? null : sensor.getDbMapper().getClass();
		this.newInstance = false;
	}

	public boolean isNewInstance() {
		return newInstance;
	}

	public void setNewInstance(boolean newInstance) {
		this.newInstance = newInstance;
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

	public Double getValue() {
		return value;
	}

	public void setValue(Double value) {
		this.value = value;
	}

	public File getConfigDirectory() {
		return configDirectory;
	}

	public void setConfigDirectory(File configDirectory) {
		this.configDirectory = configDirectory;
	}

	@SuppressWarnings("unchecked")
	public Class<? extends HomeElementDbMapper> getDbMapperClass() {
		if(dbMapperClass == null){
			try {
				return (Class<? extends HomeElementDbMapper>) Class.forName("smadja.homeAutomation.model.mapper.GenericSensorMapper");
			} catch (ClassNotFoundException e) {
				return null;
			}
		}
		return dbMapperClass;
	}

	public void setDbMapperClass(Class<? extends HomeElementDbMapper> dbMapperClass) {
		this.dbMapperClass = dbMapperClass;
	}

	public GenericSensor getSensor() {
		GenericSensor sensor = new GenericSensor();
		sensor.setId(this.id);
		sensor.setLabel(this.label);
		sensor.setQueue(this.queue);
		sensor.setValue(this.value);
		HomeElementDbMapper mapperInstance = null;
		if (this.dbMapperClass != null) {
			try {
				mapperInstance = HomeElementHelper.getMapperInstance(this.dbMapperClass.getName());
			} catch (HomeAutomationException e) {
				logger.warn(e.getMessage(), e);
			}
		}
		sensor.setDbMapper(mapperInstance);
		sensor.setConfigDirectory(this.configDirectory);
		return sensor;
	}

}
