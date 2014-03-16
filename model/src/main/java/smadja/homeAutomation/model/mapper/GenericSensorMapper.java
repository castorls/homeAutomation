package smadja.homeAutomation.model.mapper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;

import org.apache.log4j.Logger;

import smadja.homeAutomation.model.HomeAutomationException;
import smadja.homeAutomation.model.HomeElement;
import smadja.homeAutomation.model.Message;
import smadja.homeAutomation.model.MessageHelper;

public class GenericSensorMapper implements HomeElementDbMapper {

	private static Logger logger = Logger.getLogger(GenericSensorMapper.class);

	@Override
	public void saveValue(HomeElement homeElt, Message msg, Connection connection) throws HomeAutomationException {
		double doubleValue = 0;
		try {
			doubleValue = MessageHelper.getDoubleValue(msg);
		} catch (ParseException e) {
			logger.debug("Cannot get the double value, the message will be ignored");
			return;
		}
		String tableName = "sensor_" + homeElt.getId();
		StringBuilder buffer = new StringBuilder();
		buffer.append("BEGIN;\n");
		buffer.append("LOCK TABLE ").append(tableName).append(" IN SHARE ROW EXCLUSIVE MODE;\n");
		buffer.append("WITH upsert AS (UPDATE ").append(tableName).append(" SET value=? WHERE eventDate=? RETURNING *) INSERT INTO ").append(tableName).append(" (eventDate, value) SELECT ?, ? WHERE NOT EXISTS (SELECT * FROM upsert);");
		buffer.append("COMMIT;");
		try {
			PreparedStatement statement = connection.prepareStatement(buffer.toString());
			java.sql.Timestamp timestamp = new java.sql.Timestamp(msg.getEventDate().getTime());
			statement.setDouble(1, doubleValue);
			statement.setTimestamp(2, timestamp);
			statement.setTimestamp(3, timestamp);
			statement.setDouble(4, doubleValue);
			statement.execute();
		} catch (SQLException e) {
			logger.debug(e.getMessage(), e);
			throw new HomeAutomationException("Cannot execute update value for " + homeElt.getId() + ":" + e.getMessage(), e);
		}
	}

}
