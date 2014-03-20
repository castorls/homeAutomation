package admin.console.beans;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;

import smadja.homeAutomation.model.GenericSensor;
import smadja.homeAutomation.model.HomeAutomationException;
import smadja.homeAutomation.model.HomeElement;
import smadja.homeAutomation.server.Server;

public class SensorsBean {

	public static String sensorsListKey = "admin_console_beans_sensorsList";

	public String doNew() {
		Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
		sessionMap.put("editedElt", new GenericSensor());
		return "success";
	}

	public String doList() {
		doSearch(".*");
		return "success";
	}

	private void doSearch(String searchRegex) {
		Server server = Server.getInstance();
		List<HomeElement> eltList = server.getHomeElementList();
		List<HomeElement> sensorsList = new ArrayList<HomeElement>();
		if (eltList != null && !eltList.isEmpty()) {
			for (HomeElement elt : eltList) {
				if (elt instanceof GenericSensor && elt.getId().toLowerCase().matches(searchRegex)) {
					sensorsList.add(new GenericSensor((GenericSensor) elt));
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
		HomeElement elt = null;
		@SuppressWarnings("unchecked")
		List<HomeElement> sensorsList = (List<HomeElement>) sessionMap.get(sensorsListKey);
		if (sensorsList != null && !sensorsList.isEmpty()) {
			for (HomeElement tmpElt : sensorsList) {
				if (tmpElt.getId().equals(eltId)) {
					elt = tmpElt;
					break;
				}
			}
		}
		if (elt == null) {
			elt = Server.getInstance().getHomeElementById(eltId);
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
		GenericSensor sensor = (GenericSensor) sessionMap.get("editedElt");
		if (sensor != null) {
			Server server = Server.getInstance();
			try {
				server.validateSensor(sensor);
				server.saveSensor(sensor);
				sessionMap.remove("editedElt");
			} catch (HomeAutomationException e) {
				ErrorBean.addException(e);
				return "error";
			}
		}
		doSearch(".*");
		return "success";
	}
}