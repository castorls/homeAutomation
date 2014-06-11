package admin.console.beans;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import smadja.homeAutomation.model.HomeElement;
import smadja.homeAutomation.model.Position;
import smadja.homeAutomation.model.mapper.HomeElementDbMapper;

public class ElementBean implements Serializable {

	private static final long serialVersionUID = -4298868227926357752L;
	protected String id;
	protected String label;
	protected String queue;
	protected Double value;
	protected File configDirectory;
	protected Class<? extends HomeElementDbMapper> dbMapperClass;
	protected boolean newInstance = false;
	protected List<Position> positions = new ArrayList<Position>();

	public ElementBean() {
		super();
		this.newInstance = true;
	}

	public ElementBean(HomeElement elt) {
		super();
		this.id = elt.getId() == null ? null : new String(elt.getId());
		this.label = elt.getLabel() == null ? null : new String(elt.getLabel());
		this.queue = elt.getQueue() == null ? null : new String(elt.getQueue());
		this.value = elt.getValue() == null ? null : new Double(elt.getValue());
		this.configDirectory = elt.getConfigDirectory() == null ? null : new File(elt.getConfigDirectory().getAbsolutePath());
		this.dbMapperClass = elt.getDbMapper() == null ? null : elt.getDbMapper().getClass();
		this.positions = elt.getPositions() == null ? null : elt.getPositions();
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

	public List<Position> getPositions() {
		return positions;
	}

	public void setPositions(List<Position> positions) {
		this.positions = positions;
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

}