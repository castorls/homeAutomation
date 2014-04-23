package admin.console.beans;

import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import server.ServerClient;
import smadja.homeAutomation.model.GenericActuator;
import smadja.homeAutomation.model.GenericSensor;
import smadja.homeAutomation.model.HomeAutomationException;
import smadja.homeAutomation.model.HomeElement;

public class ActuatorsBean extends ElementsBean {

	public static String actuatorsListKey = "admin_console_beans_actuatorsList";

	public String doNew() {
		Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
		sessionMap.put("editedElt", new ActuatorBean());
		return "success";
	}


	public String saveCurrentBean() {
		Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
		ActuatorBean actuatorBean = (ActuatorBean) sessionMap.get("editedElt");
		if (actuatorBean != null) {
			ServerClient server = ServerClient.getInstance();
			try {
				GenericActuator actuator = actuatorBean.getActuator();

				server.validateActuator(actuator, actuatorBean.isNewInstance());
				server.saveActuator(actuator, actuatorBean.isNewInstance());
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
		return actuatorsListKey;
	}


	@Override
	ElementBean getElementBean(HomeElement elt) {
		return new SensorBean(elt);
	}
	
	@Override
	boolean filterElement(HomeElement elt) {
		return  elt instanceof GenericActuator;
	}
}
