package admin.console.beans;

import org.apache.log4j.Logger;

import smadja.homeAutomation.model.GenericActuator;
import smadja.homeAutomation.model.HomeAutomationException;
import smadja.homeAutomation.model.HomeElement;
import smadja.homeAutomation.model.helper.HomeElementHelper;
import smadja.homeAutomation.model.mapper.HomeElementDbMapper;

public class ActuatorBean extends ElementBean {

	private static final long serialVersionUID = -370762476231053671L;
	private static Logger logger = Logger.getLogger(ActuatorBean.class);

	public ActuatorBean() {
		super();
	}

	public ActuatorBean(HomeElement elt) {
		super(elt);
	}

	public GenericActuator getActuator() {
		GenericActuator actuator = new GenericActuator();
		actuator.setId(this.id);
		actuator.setLabel(this.label);
		actuator.setQueue(this.queue);
		actuator.setValue(this.value);
		HomeElementDbMapper mapperInstance = null;
		if (this.dbMapperClass != null) {
			try {
				mapperInstance = HomeElementHelper.getMapperInstance(this.dbMapperClass.getName());
			} catch (HomeAutomationException e) {
				logger.warn(e.getMessage(), e);
			}
		}
		actuator.setDbMapper(mapperInstance);
		actuator.setConfigDirectory(this.configDirectory);
		return actuator;
	}

}
