package smadja.homeAutomation.simpleClient;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

public abstract class AbstractClient {

	private static final String CLIENT_PROP = "client.prop";
	private static Logger logger = Logger.getLogger(AbstractClient.class);
	protected String user;
	protected String emitter;
	protected String password;
	protected String host;
	protected int port;

	public Properties getProps() {
		InputStream propInputStream = this.getClass().getClassLoader().getResourceAsStream(CLIENT_PROP);
		if (propInputStream == null) {
			logger.error("Cannot retrieve " + CLIENT_PROP + " from ClassLoader");
			return null;
		}
		Properties props = new Properties();
		try {
			props.load(propInputStream);
			return props;
		} catch (IOException e) {
			logger.error("Cannot load " + CLIENT_PROP);
			logger.error(e.getMessage(), e);
		}
		return null;
	}

	public boolean init(Properties props) {
		if (props == null) {
			return false;
		}
		emitter = props.getProperty("emitter");
		user = props.getProperty("user");
		password = props.getProperty("password");
		host = props.getProperty("host");
		String portStr = props.getProperty("port");
		port = portStr == null ? 61616 : Integer.parseInt(portStr);
		return true;
	}

	abstract public void doJob(String[] args);

}
