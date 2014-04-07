package smadja.homeAutomation.server.rest;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import smadja.homeAutomation.DBUtil;
import smadja.homeAutomation.model.GenericSensor;
import smadja.homeAutomation.model.HistoryData;
import smadja.homeAutomation.model.HomeAutomationException;
import smadja.homeAutomation.model.HomeElement;
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
	@Path("/homeElementById/{id}")
	@Produces("application/json")
	public HomeElement getHomeElementById(@PathParam("id") String id) {
		return Server.getInstance().getHomeElementById(id);
	}
	

	@GET
	@Path("/sensorHistory/{id}")
	@Produces("application/json")
	public List<HistoryData> getSensorHistory(@PathParam("id") String id) throws HomeAutomationException {
		if (id == null || "".equals(id.trim())) {
			return null;
		}
		Server server = Server.getInstance();
		HomeElement elt = server.getHomeElementById(id);
		if (elt == null) {
			return null;
		}
		Connection connection = DBUtil.getConnection();
		try {
			List<HistoryData> historyList = elt.getDbMapper().getHistoryData(elt, connection);
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
	@Consumes("application/json")
	public void deleteHomeElementById(@PathParam("id") String id) throws HomeAutomationException {
		Server.getInstance().deleteHomeElementById(id);
	}
	
	@POST
	@Path("/homeElement/validate")
	@Consumes("application/json")
	public void validateSensor(@QueryParam("sensor") String sensor, @QueryParam("newInstance") boolean newInstance) throws HomeAutomationException {
		GenericSensor sensorObj;
		try {
			sensorObj = new ObjectMapper().readValue(sensor, GenericSensor.class);
			Server.getInstance().validateSensor(sensorObj, newInstance);
		} catch (IOException e) {
			throw new HomeAutomationException(e.getMessage(), e);
		}
	}

	@POST
	@Path("/homeElement/save")
	@Consumes("application/json")
	public void saveSensor(@QueryParam("sensor") String sensor) throws HomeAutomationException {
		GenericSensor sensorObj;
		try {
			sensorObj = new ObjectMapper().readValue(sensor, GenericSensor.class);
			Server.getInstance().saveSensor(sensorObj);
		} catch (IOException e) {
			throw new HomeAutomationException(e.getMessage(), e);
		}
	}

}
