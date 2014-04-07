package admin.console.beans;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;

import server.ServerClient;
import smadja.homeAutomation.model.GenericSensor;
import smadja.homeAutomation.model.HomeAutomationException;
import smadja.homeAutomation.model.HomeElement;

import com.fasterxml.jackson.core.JsonProcessingException;

public class SensorsBean {

	public static String sensorsListKey = "admin_console_beans_sensorsList";
	private static Logger logger = Logger.getLogger(SensorsBean.class);

	public String doNew() {
		Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
		sessionMap.put("editedElt", new SensorBean());
		return "success";
	}

	public String doList() {
		doSearch(".*");
		return "success";
	}

	private void doSearch(String searchRegex) {
		ServerClient server = ServerClient.getInstance();
		Set<HomeElement> eltSet = null;
		try {
			eltSet = server.getHomeElementSet();
		} catch (HomeAutomationException e) {
			logger.warn(e.getMessage(), e);
		}
		List<SensorBean> sensorsList = new ArrayList<SensorBean>();
		if (eltSet != null && !eltSet.isEmpty()) {
			for (HomeElement elt : eltSet) {
				if (elt instanceof GenericSensor && elt.getId().toLowerCase().matches(searchRegex)) {
					sensorsList.add(new SensorBean((GenericSensor) elt));
				}
			}
		}
		FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(sensorsListKey, sensorsList);
	}

	public String doSearch() {
		return "todo";
	}

	public String edit() {
		Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
		String eltId = (String) FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("eltId");
		SensorBean elt = null;
		@SuppressWarnings("unchecked")
		List<SensorBean> sensorsList = (List<SensorBean>) sessionMap.get(sensorsListKey);
		if (sensorsList != null && !sensorsList.isEmpty()) {
			for (SensorBean tmpElt : sensorsList) {
				if (tmpElt.getId().equals(eltId)) {
					elt = tmpElt;
					break;
				}
			}
		}
		if (elt == null) {
			HomeElement tmpElt = null;
			try {
				tmpElt = ServerClient.getInstance().getHomeElementById(eltId);
			} catch (HomeAutomationException e) {
				logger.warn(e.getMessage(), e);
			}
			if (tmpElt != null) {
				elt = new SensorBean((GenericSensor) tmpElt);
			}
		}
		sessionMap.put("editedElt", elt);
		return "success";
	}

	public String cancelCurrentBeanEdition() {
		Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
		sessionMap.remove("editedElt");
		doSearch(".*");
		return "success";
	}

	public String saveCurrentBean() {
		Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
		SensorBean sensorBean = (SensorBean) sessionMap.get("editedElt");
		if (sensorBean != null) {
			ServerClient server = ServerClient.getInstance();
			try {
				GenericSensor sensor = sensorBean.getSensor();

				server.validateSensor(sensor, sensorBean.isNewInstance());
				server.saveSensor(sensor);
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

	public String history() {
		Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
		String eltId = (String) FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("eltId");
		SensorBean elt = null;
		@SuppressWarnings("unchecked")
		List<SensorBean> sensorsList = (List<SensorBean>) sessionMap.get(sensorsListKey);
		if (sensorsList != null && !sensorsList.isEmpty()) {
			for (SensorBean tmpElt : sensorsList) {
				if (tmpElt.getId().equals(eltId)) {
					elt = tmpElt;
					break;
				}
			}
		}
		if (elt == null) {
			HomeElement tmpElt = null;
			try {
				tmpElt = ServerClient.getInstance().getHomeElementById(eltId);
			} catch (HomeAutomationException e) {
				logger.warn(e.getMessage(), e);
			}
			if (tmpElt != null) {
				elt = new SensorBean((GenericSensor) tmpElt);
			}
		}
		sessionMap.put("editedElt", elt);
		return "success";
	}

	public String delete() {
		Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
		String eltId = (String) FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("eltId");
		try {
			ServerClient.getInstance().deleteHomeElementById(eltId);
			sessionMap.remove(sensorsListKey);
		} catch (HomeAutomationException e) {
			ErrorBean.addException(e);
			return "error";
		}
		return "success";
	}

	public void renderHistoryJson() throws IOException {
		try {
			FacesContext facesContext = FacesContext.getCurrentInstance();
			ExternalContext externalContext = facesContext.getExternalContext();
			externalContext.setResponseContentType("application/json");
			externalContext.setResponseCharacterEncoding("UTF-8");
			externalContext.getResponseOutputWriter().write(ServerClient.getInstance().getSensorHistoryJson(externalContext.getRequestParameterMap().get("eltId")));
			facesContext.responseComplete();
		} catch (HomeAutomationException | JsonProcessingException e) {
			throw new IOException(e.getMessage(), e);
		}
	}
	
}