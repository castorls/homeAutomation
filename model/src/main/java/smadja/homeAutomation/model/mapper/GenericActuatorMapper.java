package smadja.homeAutomation.model.mapper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import smadja.homeAutomation.model.HistoryData;
import smadja.homeAutomation.model.HomeAutomationException;
import smadja.homeAutomation.model.HomeElement;
import smadja.homeAutomation.model.JSONHelper;
import smadja.homeAutomation.model.Message;

public class GenericActuatorMapper implements HomeElementDbMapper {

	private static Logger logger = Logger.getLogger(GenericActuatorMapper.class);

	@Override
	public void saveValue(HomeElement homeElt, Message msg, Connection connection) throws HomeAutomationException {
		String value = JSONHelper.getValue(msg);
		String tableName = getTableName(homeElt);
		StringBuilder buffer = new StringBuilder();
		buffer.append("BEGIN;\n");
		buffer.append("LOCK TABLE ").append(tableName).append(" IN SHARE ROW EXCLUSIVE MODE;\n");
		buffer.append("WITH upsert AS (UPDATE ").append(tableName).append(" SET state=? WHERE eventdate=? RETURNING *) INSERT INTO ").append(tableName).append(" (eventdate, state) SELECT ?, ? WHERE NOT EXISTS (SELECT * FROM upsert);");
		buffer.append("COMMIT;");
		try {
			PreparedStatement statement = connection.prepareStatement(buffer.toString());
			java.sql.Timestamp timestamp = new java.sql.Timestamp(msg.getEventDate().getTime());
			statement.setString(1, value);
			statement.setTimestamp(2, timestamp);
			statement.setTimestamp(3, timestamp);
			statement.setString(4, value);
			statement.execute();
		} catch (SQLException e) {
			logger.debug(e.getMessage(), e);
			throw new HomeAutomationException("Cannot execute update state for " + homeElt.getId() + ":" + e.getMessage(), e);
		}
	}

	private String getTableName(HomeElement homeElt) {
		String tableName = "actuator_" + homeElt.getId();
		return tableName;
	}

	@Override
	public List<HistoryData> getHistoryData(HomeElement homeElt, Connection connection) throws HomeAutomationException {
		String tableName = getTableName(homeElt);
		StringBuilder buffer = new StringBuilder();
		buffer.append("select eventdate, state from ").append(tableName).append(" where eventdate > (now() - 7* interval '1 day');");
		try {
			PreparedStatement statement = connection.prepareStatement(buffer.toString());
			ResultSet rs = statement.executeQuery();
			List<HistoryData> returnList = new ArrayList<HistoryData>();
			while (rs.next()) {
				Date date = rs.getTimestamp("eventdate");
				String value = rs.getString("state");
				returnList.add(new HistoryData(date, value));
			}
			return returnList;
		} catch (SQLException e) {
			logger.debug(e.getMessage(), e);
			throw new HomeAutomationException("Cannot execute history request for " + homeElt.getId() + ":" + e.getMessage(), e);
		}
	}

}
