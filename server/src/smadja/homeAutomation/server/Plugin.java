package smadja.homeAutomation.server;

import java.io.File;
import java.util.Properties;

import org.apache.log4j.Logger;

import smadja.homeAutomation.model.HomeAutomationException;
import smadja.homeAutomation.model.HomeMessageListener;

public abstract class Plugin implements HomeMessageListener {

	private static Logger logger = Logger.getLogger(Plugin.class);

	private String id;
	private Server server;

	public Plugin(String id, Server server) {
		this.id = id;
		this.server = server;
	}

	public Server getServer() {
		return server;
	}

	public void setServer(Server server) {
		this.server = server;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public abstract void init(File pluginDirectory, Properties confProp) throws HomeAutomationException;

	public void start() {
		logger.info("Start " + getId() + " Plugin");
	}

	public void stop() {
		logger.info("stop " + getId() + " Plugin");
	}
}
