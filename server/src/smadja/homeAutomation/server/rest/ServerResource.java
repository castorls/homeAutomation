package smadja.homeAutomation.server.rest;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import smadja.homeAutomation.DBUtil;
import smadja.homeAutomation.model.GenericActuator;
import smadja.homeAutomation.model.GenericSensor;
import smadja.homeAutomation.model.HistoryData;
import smadja.homeAutomation.model.HomeAutomationException;
import smadja.homeAutomation.model.HomeElement;
import smadja.homeAutomation.model.mapper.HomeElementDbMapper;
import smadja.homeAutomation.server.Server;

import com.fasterxml.jackson.databind.ObjectMapper;

@Path("main")
public class ServerResource {

	@GET
	@Path("/homeElementSet")
	@Produces("application/json")
	public Set<HomeElement> getHomeElementSet() {
		return Server.getInstance().getHomeElementSet();
	}
	
	@GET
	@Path("/config/levelCount")
	@Produces("application/json")
	public int getLevelCount() {
		return Server.getInstance().getLevelCount();
	}

	@GET
	@Path("/homeElementById/{id}")
	@Produces("application/json")
	public HomeElement getHomeElementById(@PathParam("id") String id) {
		return Server.getInstance().getHomeElementById(id);
	}

	@GET
	@Path("/elementHistory/{id}")
	@Produces("application/json")
	public List<HistoryData> getElementHistory(@PathParam("id") String id) throws HomeAutomationException {
		if (id == null || "".equals(id.trim())) {
			return null;
		}
		Server server = Server.getInstance();
		HomeElement elt = server.getHomeElementById(id);
		if (elt == null) {
			return null;
		}
		HomeElementDbMapper dbMapper = elt.getDbMapper();
		if (dbMapper == null) {
			return null;
		}
		Connection connection = DBUtil.getConnection();
		try {
			List<HistoryData> historyList = dbMapper.getHistoryData(elt, connection);
			return historyList;
		} catch (HomeAutomationException e) {
			throw new HomeAutomationException(e.getMessage(), e);
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e1) {
					// nothing to do
				}
			}
		}

	}

	@DELETE
	@Path("/homeElement/{id}")
	public void deleteHomeElementById(@PathParam("id") String id) throws HomeAutomationException {
		Server.getInstance().deleteHomeElementById(id);
	}

	@POST
	@Path("/homeElement/validateSensor")
	@Consumes("application/x-www-form-urlencoded")
	public void validateSensor(@FormParam("sensor") String sensor, @FormParam("newInstance") boolean newInstance) throws HomeAutomationException {
		GenericSensor sensorObj;
		try {
			sensorObj = new ObjectMapper().readValue(sensor, GenericSensor.class);
			Server.getInstance().validateSensor(sensorObj, newInstance);
		} catch (IOException e) {
			throw new HomeAutomationException(e.getMessage(), e);
		}
	}

	@POST
	@Path("/homeElement/saveSensor")
	@Consumes("application/x-www-form-urlencoded")
	public void saveSensor(@FormParam("sensor") String sensor, @FormParam("newInstance") boolean newInstance) throws HomeAutomationException {
		GenericSensor sensorObj;
		try {
			sensorObj = new ObjectMapper().readValue(sensor, GenericSensor.class);
			Server instance = Server.getInstance();
			instance.saveSensor(sensorObj);
			if (newInstance) {
				HomeElement newElt = instance.getHomeElementById(sensorObj.getId());
				if (newElt != null) {
					sensorObj.getDbMapper().generateInitPostgresqlSQL(sensorObj);
				} else {
					throw new HomeAutomationException("Invalid new sensor with id '" + sensorObj.getId() + "'");
				}
			}
		} catch (IOException e) {
			throw new HomeAutomationException(e.getMessage(), e);
		}
	}

	@POST
	@Path("/homeElement/validateActuator")
	@Consumes("application/x-www-form-urlencoded")
	public void validateActuator(@FormParam("actuator") String actuator, @FormParam("newInstance") boolean newInstance) throws HomeAutomationException {
		GenericActuator actuatorObj;
		try {
			actuatorObj = new ObjectMapper().readValue(actuator, GenericActuator.class);
			Server.getInstance().validateActuator(actuatorObj, newInstance);
		} catch (IOException e) {
			throw new HomeAutomationException(e.getMessage(), e);
		}
	}

	@POST
	@Path("/homeElement/saveActuator")
	@Consumes("application/x-www-form-urlencoded")
	public void saveActuator(@FormParam("actuator") String actuator, @FormParam("newInstance") boolean newInstance) throws HomeAutomationException {
		GenericActuator actuatorObj;
		try {
			actuatorObj = new ObjectMapper().readValue(actuator, GenericActuator.class);
			Server instance = Server.getInstance();
			instance.saveActuator(actuatorObj);
			if (newInstance) {
				HomeElement newElt = instance.getHomeElementById(actuatorObj.getId());
				if (newElt != null) {
					actuatorObj.getDbMapper().generateInitPostgresqlSQL(actuatorObj);
				} else {
					throw new HomeAutomationException("Invalid new actuator with id '" + actuatorObj.getId() + "'");
				}
			}
		} catch (IOException e) {
			throw new HomeAutomationException(e.getMessage(), e);
		}
	}

}
