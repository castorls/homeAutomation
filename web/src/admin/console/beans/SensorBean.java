package admin.console.beans;

import java.io.Serializable;
import java.util.Iterator;

import org.apache.log4j.Logger;

import smadja.homeAutomation.model.GenericSensor;
import smadja.homeAutomation.model.HomeAutomationException;
import smadja.homeAutomation.model.HomeElement;
import smadja.homeAutomation.model.Position;
import smadja.homeAutomation.model.helper.HomeElementHelper;
import smadja.homeAutomation.model.mapper.HomeElementDbMapper;

public class SensorBean extends ElementBean implements Serializable {

	private static final long serialVersionUID = 3635134589850300090L;
	private static Logger logger = Logger.getLogger(SensorBean.class);

	public SensorBean() {
		super();
	}

	public SensorBean(HomeElement elt) {
		super(elt);
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
		sensor.setPositions(this.positions);
		if (sensor.getPositions() != null && !sensor.getPositions().isEmpty()) {
			for (Iterator<Position> iterator = sensor.getPositions().iterator(); iterator.hasNext();) {
				Position pos =iterator.next();
				if (pos.getLevel() == 0 && pos.getX() == null && pos.getY() == null) {
					iterator.remove();
				}
			}
		}
		return sensor;
	}

}
