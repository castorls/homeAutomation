package admin.console.beans;

import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;

import server.ServerClient;
import smadja.homeAutomation.model.GenericSensor;
import smadja.homeAutomation.model.HomeAutomationException;
import smadja.homeAutomation.model.HomeElement;
import smadja.homeAutomation.model.Position;

public class SensorsBean extends ElementsBean {

	public static String sensorsListKey = "admin_console_beans_sensorsList";
	static Logger logger = Logger.getLogger(SensorsBean.class);

	public String doNew() {
		Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
		SensorBean elt = new SensorBean();
		elt.getPositions().add(new Position());
		sessionMap.put("editedElt", elt);
		return "success";
	}


	public String saveCurrentBean() {
		Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
		SensorBean sensorBean = (SensorBean) sessionMap.get("editedElt");
		if (sensorBean != null) {
			ServerClient server = ServerClient.getInstance();
			try {
				GenericSensor sensor = sensorBean.getSensor();
				Position found = null;
				for(Position pos : sensor.getPositions()){
					if(pos.getLevel() == 0 && pos.getX() == 0 && pos.getY() == 0){
						found = pos;
					}
				}
				if(found != null){
					sensor.getPositions().remove(found);
				}
				server.validateSensor(sensor, sensorBean.isNewInstance());
				server.saveSensor(sensor, sensorBean.isNewInstance());
				sessionMap.remove("editedElt");
			} catch (HomeAutomationException e) {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(e.getMessage()));
				// ErrorBean.addException(e);
				return "errorEdit";
			}
		}
		doSearch(".*");
		return "success";
	}


	@Override
	String getListKey() {
		return sensorsListKey;
	}


	@Override
	ElementBean getElementBean(HomeElement elt) {
		return new SensorBean(elt);
	}


	@Override
	boolean filterElement(HomeElement elt) {
		return  elt instanceof GenericSensor;
	}
	
}