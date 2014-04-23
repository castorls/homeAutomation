package admin.console.beans;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;

import server.ServerClient;
import smadja.homeAutomation.model.GenericSensor;
import smadja.homeAutomation.model.HomeAutomationException;
import smadja.homeAutomation.model.HomeElement;
import smadja.homeAutomation.model.Position;

import com.fasterxml.jackson.core.JsonProcessingException;

public abstract class ElementsBean {

	static Logger logger = Logger.getLogger(ElementsBean.class);

	public ElementsBean() {
		super();
	}

	abstract String getListKey();

	abstract ElementBean getElementBean(HomeElement elt);
	
	abstract boolean filterElement(HomeElement elt);

	public String doList() {
		doSearch(".*");
		return "success";
	}

	protected void doSearch(String searchRegex) {
		ServerClient server = ServerClient.getInstance();
		Set<HomeElement> eltSet = null;
		try {
			eltSet = server.getHomeElementSet();
		} catch (HomeAutomationException e) {
			logger.warn(e.getMessage(), e);
		}
		List<ElementBean> eltsList = new ArrayList<ElementBean>();
		if (eltSet != null && !eltSet.isEmpty()) {
			for (HomeElement elt : eltSet) {
				if (filterElement(elt) && elt.getId().toLowerCase().matches(searchRegex)) {
					eltsList.add(getElementBean(elt));
				}
			}
		}
		FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(getListKey(), eltsList);
	}

	public String doSearch() {
		return "todo";
	}

	public String history() {
		Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
		String eltId = (String) FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("eltId");
		ElementBean elt = null;
		@SuppressWarnings("unchecked")
		List<ElementBean> eltList = (List<ElementBean>) sessionMap.get(getListKey());
		if (eltList != null && !eltList.isEmpty()) {
			for (ElementBean tmpElt : eltList) {
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
				elt = getElementBean(tmpElt);
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
			sessionMap.remove(getListKey());
			return doList();
		} catch (HomeAutomationException e) {
			ErrorBean.addException(e);
			return "error";
		}
	}

	public void renderHistoryJson() throws IOException {
		try {
			FacesContext facesContext = FacesContext.getCurrentInstance();
			ExternalContext externalContext = facesContext.getExternalContext();
			externalContext.setResponseContentType("application/json");
			externalContext.setResponseCharacterEncoding("UTF-8");
			String sensorHistoryJson = ServerClient.getInstance().getElementHistoryJson(externalContext.getRequestParameterMap().get("eltId"));
			if (sensorHistoryJson == null) {
				sensorHistoryJson = "{\"error\":\"no data to display\"}";
			}
			externalContext.getResponseOutputWriter().write(sensorHistoryJson);
			facesContext.responseComplete();
		} catch (HomeAutomationException | JsonProcessingException e) {
			throw new IOException(e.getMessage(), e);
		}
	}

	public String edit() {
		Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
		String eltId = (String) FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("eltId");
		ElementBean elt = null;
		@SuppressWarnings("unchecked")
		List<ElementBean> eltsList = (List<ElementBean>) sessionMap.get(getListKey());
		if (eltsList != null && !eltsList.isEmpty()) {
			for (ElementBean tmpElt : eltsList) {
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
				elt = getElementBean((GenericSensor) tmpElt);
			}
		}
		elt.getPositions().add(new Position());
		sessionMap.put("editedElt", elt);
		return "success";
	}

	public String cancelCurrentBeanEdition() {
		Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
		sessionMap.remove("editedElt");
		doSearch(".*");
		return "success";
	}

}