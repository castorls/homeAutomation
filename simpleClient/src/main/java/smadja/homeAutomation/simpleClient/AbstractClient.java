package smadja.homeAutomation.simpleClient;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

import smadja.homeAutomation.model.Parameter;

public abstract class AbstractClient {

	private static final String CLIENT_PROP = "client.prop";
	private static Logger logger = Logger.getLogger(AbstractClient.class);

	public void doJob(String[] args) {
		Parameter parameter = init(getProps());
		if (parameter != null) {
			doInternalJob(parameter, args);
		}
		
	}
	
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

	public Parameter init(Properties props) {
		if (props == null) {
			return null;
		}
		String emitter = props.getProperty("emitter");
		String user = props.getProperty("user");
		String password = props.getProperty("password");
		String host = props.getProperty("host");
		String portStr = props.getProperty("port");
		int port = portStr == null ? 61616 : Integer.parseInt(portStr);
		return new Parameter(user, emitter, password, host, port);
	}

	abstract public void doInternalJob(Parameter parameter, String[] args);

}
