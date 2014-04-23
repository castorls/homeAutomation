package server;

import java.util.List;
import java.util.Set;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;

import smadja.homeAutomation.model.GenericActuator;
import smadja.homeAutomation.model.GenericSensor;
import smadja.homeAutomation.model.HistoryData;
import smadja.homeAutomation.model.HomeAutomationException;
import smadja.homeAutomation.model.HomeElement;
import smadja.homeAutomation.model.JSONHelper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ServerClient {

	// private final Logger logger = Logger.getLogger(ServerClient.class);
	private static ServerClient instance = null;

	private ServerClient() {
		// nothing to do
	}

	public static ServerClient getInstance() {
		if (instance == null) {
			instance = new ServerClient();
		}
		return instance;
	}

	public WebTarget getClient() {
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target(System.getenv("SERVER_REST_URL"));
		return target;
	}

	public Set<HomeElement> getHomeElementSet() throws HomeAutomationException {
		WebTarget target = getClient().path("/main/homeElementSet");
		Set<HomeElement> responseSet = target.request(MediaType.APPLICATION_JSON).get(new GenericType<Set<HomeElement>>() {
		});
		return responseSet;
	}

	public HomeElement getHomeElementById(String eltId) throws HomeAutomationException {
		WebTarget target = getClient().path("/main/homeElementById/" + eltId);
		HomeElement elt = target.request(MediaType.APPLICATION_JSON_TYPE).get(HomeElement.class);
		return elt;
	}

	public void deleteHomeElementById(String eltId) throws HomeAutomationException {
		WebTarget target = getClient().path("/main/homeElement/" + eltId);
		target.request(MediaType.APPLICATION_JSON_TYPE).delete();
	}

	public String getElementHistoryJson(String eltId) throws HomeAutomationException {
		WebTarget target = getClient().path("/main/elementHistory/" + eltId);
		List<HistoryData> responseList = target.request(MediaType.APPLICATION_JSON).get(new GenericType<List<HistoryData>>() {
		});
		if (responseList != null) {
			try {
				return JSONHelper.serialize(responseList);
			} catch (JsonProcessingException e) {
				throw new HomeAutomationException(e.getMessage(), e);
			}
		}
		return null;
	}

	public void validateSensor(GenericSensor sensor, boolean newInstance) throws HomeAutomationException {
		try {
			WebTarget target = getClient().path("/main/homeElement/validateSensor");
			Form form = new Form();
			form.param("sensor", new ObjectMapper().writeValueAsString(sensor));
			form.param("newInstance", Boolean.toString(newInstance));
			target.request(MediaType.APPLICATION_JSON_TYPE).header("Content-type", "application/json").post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));
		} catch (JsonProcessingException e) {
			throw new HomeAutomationException(e.getMessage(), e);
		}

	}

	public void saveSensor(GenericSensor sensor, boolean newInstance) throws HomeAutomationException {
		try {
			WebTarget target = getClient().path("/main/homeElement/saveSensor");
			Form form = new Form();
			form.param("sensor", new ObjectMapper().writeValueAsString(sensor));
			form.param("newInstance", Boolean.toString(newInstance));
			target.request(MediaType.APPLICATION_JSON_TYPE).header("Content-type", "application/json").post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));
		} catch (JsonProcessingException e) {
			throw new HomeAutomationException(e.getMessage(), e);
		}

	}

	public void validateActuator(GenericActuator actuator, boolean newInstance) throws HomeAutomationException {
		try {
			WebTarget target = getClient().path("/main/homeElement/validateActuator");
			Form form = new Form();
			form.param("actuator", new ObjectMapper().writeValueAsString(actuator));
			form.param("newInstance", Boolean.toString(newInstance));
			target.request(MediaType.APPLICATION_JSON_TYPE).header("Content-type", "application/json").post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));
		} catch (JsonProcessingException e) {
			throw new HomeAutomationException(e.getMessage(), e);
		}

	}

	public void saveActuator(GenericActuator actuator, boolean newInstance) throws HomeAutomationException {
		try {
			WebTarget target = getClient().path("/main/homeElement/saveActuator");
			Form form = new Form();
			form.param("actuator", new ObjectMapper().writeValueAsString(actuator));
			form.param("newInstance", Boolean.toString(newInstance));
			target.request(MediaType.APPLICATION_JSON_TYPE).header("Content-type", "application/json").post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));
		} catch (JsonProcessingException e) {
			throw new HomeAutomationException(e.getMessage(), e);
		}
	}

	public int getLevelCount() {
		WebTarget target = getClient().path("/main/config/levelCount");
		Integer nbLevel = target.request(MediaType.APPLICATION_JSON_TYPE).get(Integer.class);
		return nbLevel.intValue();
	}
}