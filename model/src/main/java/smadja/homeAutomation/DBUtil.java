package smadja.homeAutomation;

import java.beans.PropertyVetoException;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import smadja.homeAutomation.model.HomeAutomationException;

import com.mchange.v2.c3p0.ComboPooledDataSource;

public class DBUtil {

	private static Logger logger = Logger.getLogger(DBUtil.class);

	private static ComboPooledDataSource cpds = null;

	public static void init(Properties props, List<File> sqlList) throws HomeAutomationException {
		Connection connection = null;
		try {
			cpds = new ComboPooledDataSource();
			cpds.setDriverClass(props.getProperty("db.driver")); // loads the
																	// jdbc
																	// driver
			cpds.setJdbcUrl(props.getProperty("db.jdbcUrl"));
			cpds.setUser(props.getProperty("db.user"));
			cpds.setPassword(props.getProperty("db.password"));

			// the settings below are optional -- c3p0 can work with defaults
			cpds.setMinPoolSize(Integer.parseInt(props.getProperty("db.minPoolSize")));
			cpds.setAcquireIncrement(Integer.parseInt(props.getProperty("db.acquireIncrement")));
			cpds.setMaxPoolSize(Integer.parseInt(props.getProperty("db.maxPoolSize")));

			// The DataSource cpds is now a fully configured and usable pooled
			// DataSource

			connection = cpds.getConnection();
			connection.setAutoCommit(false);
			Statement statement = connection.createStatement();
			for (File sqlFile : sqlList) {
				logger.info("Execute init SQL from " + sqlFile);
				String sql = FileUtils.readFileToString(sqlFile);
				statement.execute(sql);
			}
			connection.commit();

		} catch (PropertyVetoException | IOException | SQLException e) {
			if (connection != null) {
				try {
					connection.rollback();
				} catch (SQLException e1) {
					// nothing to do
				}
			}
			throw new HomeAutomationException(e.getMessage(), e);
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					// nothing to do
				}
			}

		}
	}

	public static Connection getConnection() throws HomeAutomationException {
		try {
			return cpds == null ? null : cpds.getConnection();
		} catch (SQLException e) {
			throw new HomeAutomationException(e.getMessage(), e);
		}

	}

}
