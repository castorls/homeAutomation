package smadja.homeAutomation.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import smadja.homeAutomation.DBUtil;
import smadja.homeAutomation.model.GenericSensor;
import smadja.homeAutomation.model.HomeAutomationException;
import smadja.homeAutomation.model.HomeElement;
import smadja.homeAutomation.model.HomeMessageListener;
import smadja.homeAutomation.model.JSONHelper;
import smadja.homeAutomation.model.JmsReceiver;
import smadja.homeAutomation.model.Message;
import smadja.homeAutomation.model.Parameter;
import smadja.homeAutomation.model.QueueParameter;
import smadja.homeAutomation.model.QueueReceiverParameter;
import smadja.homeAutomation.model.helper.HomeElementHelper;
import smadja.homeAutomation.model.mapper.HomeElementDbMapper;

public class Server {

	public static final String PLUGINS = "plugins";
	public static final String CONF_PROP = "conf.prop";
	public static final String ELEMENTS = "elements";
	public static final String ACTION_QUEUE = "ACTION_QUEUE";
	public static final String SENSOR_QUEUE = "SENSOR_QUEUE";

	private static Server instance = null;
	private static Logger logger = Logger.getLogger(Server.class);

	private Parameter serverParameter = null;
	private Properties serverProp = null;
	private File appHome = null;
	private Set<Plugin> pluginsList = new HashSet<Plugin>();
	private Set<HomeElement> eltsSet = new HashSet<HomeElement>();
	private List<Thread> threadList = new ArrayList<Thread>();
	private List<JmsReceiver> receiverList = new ArrayList<JmsReceiver>();
	private Map<String, QueueParameter> queueMap = new HashMap<String, QueueParameter>();
	private ExecutorService es = null;

	private Server() {
		// nothing to do
	}

	public static Server getInstance() {
		if (instance == null) {
			try {
				Server instance_srv = new Server();
				instance_srv.init();
				instance = instance_srv;
			} catch (HomeAutomationException e) {
				logger.error(e.getMessage(), e);
			}
		}
		return instance;
	}

	public void init() throws HomeAutomationException {
		appHome = getHomeAutomationHome();
		serverProp = new Properties();
		File serverPropFile = new File(new File(appHome, "conf"), "server.prop");
		if (!appHome.exists()) {
			throw new HomeAutomationException("Invalid HOMEAUTOMATION_HOME/conf/server.prop. It must be a valid configuration file.");
		}
		try {
			serverProp.load(new InputStreamReader(new FileInputStream(serverPropFile), "UTF-8"));
			serverParameter = init(serverProp);
			initElements();
			initDb();
			initPlugins();
			initQueues();
		} catch (IOException e) {
			throw new HomeAutomationException("Invalid HOMEAUTOMATION_HOME/conf/server.prop." + e.getMessage(), e);
		}
	}

	public static File getHomeAutomationHome() throws HomeAutomationException {
		String applicationHome = System.getenv("HOMEAUTOMATION_HOME");
		if (applicationHome == null || "".equals(applicationHome.trim())) {
			throw new HomeAutomationException("Invalid HOMEAUTOMATION_HOME env variable. It must be a valid configuration directory.");
		}
		File appHomeFile = new File(applicationHome);
		if (!appHomeFile.exists() || !appHomeFile.isDirectory()) {
			throw new HomeAutomationException("Invalid HOMEAUTOMATION_HOME env variable. It must be a valid configuration directory.");
		}
		return appHomeFile;
	}

	private void initDb() throws HomeAutomationException {
		List<File> sqlList = new ArrayList<File>();
		if (!eltsSet.isEmpty()) {
			for (HomeElement elt : eltsSet) {
				if (elt != null) {
					File[] children = elt.getConfigDirectory().listFiles();
					if (children != null && children.length > 0) {
						for (File child : children) {
							if (child.getName().endsWith(".sql")) {
								sqlList.add(child);
							}
						}
					}
				}
			}
		}
		DBUtil.init(serverProp, sqlList);
	}

	private void initQueues() {
		String mainBroker = serverProp.getProperty("jms.mainBroker");
		logger.info("Initialisation of main broker queue '" + mainBroker + "'");
		List<HomeMessageListener> listenersList = getPluginMessageListeners();
		QueueReceiverParameter queueReceiverParameter = new QueueReceiverParameter(serverParameter);
		queueReceiverParameter.setReceiveQueue(mainBroker);
		queueReceiverParameter.setReceiveTimeout(Long.parseLong(serverProp.getProperty("jms.defaultreceiveTimeout")));
		JmsReceiver receiver = new JmsReceiver((QueueReceiverParameter) queueReceiverParameter, (HomeMessageListener[]) listenersList.toArray(new HomeMessageListener[listenersList.size()]), true);
		receiverList.add(receiver);
		Thread t = new Thread(receiver);
		threadList.add(t);

		// init sensor sending queue
		{
			String sensorQueue = serverProp.getProperty("jms.sensorQueue");
			logger.info("Initialisation of sensor queue '" + sensorQueue + "'");
			QueueParameter queueParameter = new QueueParameter(serverParameter);
			queueParameter.setDestinationQueue(sensorQueue);
			String persistentStr = serverProp.getProperty("isPersistent");
			queueParameter.setPersistent(persistentStr == null ? true : Boolean.parseBoolean(persistentStr));
			queueMap.put(sensorQueue, queueParameter);
			queueMap.put(SENSOR_QUEUE, queueParameter);
		}

		// init action sending queue
		{
			String actionQueue = serverProp.getProperty("jms.actionQueue");
			logger.info("Initialisation of action queue '" + actionQueue + "'");
			QueueParameter queueParameter = new QueueParameter(serverParameter);
			queueParameter.setDestinationQueue(actionQueue);
			String persistentStr = serverProp.getProperty("isPersistent");
			queueParameter.setPersistent(persistentStr == null ? true : Boolean.parseBoolean(persistentStr));
			queueMap.put(actionQueue, queueParameter);
			queueMap.put(ACTION_QUEUE, queueParameter);
		}
	}

	private List<HomeMessageListener> getPluginMessageListeners() {
		if (pluginsList.isEmpty()) {
			return new ArrayList<HomeMessageListener>();
		}
		List<HomeMessageListener> listenerList = new ArrayList<HomeMessageListener>();
		for (Plugin plugin : pluginsList) {
			HomeMessageListener homeMessageListener = (HomeMessageListener) plugin;
			if (homeMessageListener != null) {
				listenerList.add(homeMessageListener);
			}
		}
		return listenerList;
	}

	private Parameter init(Properties props) {
		if (props == null) {
			return null;
		}
		String emitter = "Core";
		String user = props.getProperty("jms.user");
		String password = props.getProperty("jms.password");
		String host = props.getProperty("jms.host");
		String portStr = props.getProperty("jms.port");
		int port = portStr == null ? 61616 : Integer.parseInt(portStr);
		return new Parameter(user, emitter, password, host, port);
	}

	private void initPlugins() throws HomeAutomationException {
		File pluginDir = getPluginsDir();
		List<File> pluginConfList = getConfigurationsList(pluginDir);
		if (!pluginConfList.isEmpty()) {
			for (File confFile : pluginConfList) {
				logger.info("Initialize plugin " + confFile.getParent());
				Plugin plugin = PluginBuilder.build(confFile, this);
				pluginsList.add(plugin);
			}
		}
	}

	public File getPluginsDir() {
		File pluginDir = new File(appHome, PLUGINS);
		return pluginDir;
	}

	private void initElements() throws HomeAutomationException {
		File elementsDir = getElementsDir();
		List<File> eltsConfList = getConfigurationsList(elementsDir);
		if (!eltsConfList.isEmpty()) {
			for (File confFile : eltsConfList) {
				logger.info("Initialize element " + confFile.getParent());
				HomeElement elt = HomeElementHelper.build(confFile);
				eltsSet.add(elt);
			}
		}
	}

	public File getElementsDir() {
		File elementsDir = new File(appHome, ELEMENTS);
		return elementsDir;
	}

	private List<File> getConfigurationsList(File pluginDir) {
		List<File> pluginList = new ArrayList<File>();
		File[] dirList = pluginDir.listFiles();
		if (dirList == null) {
			return pluginList;
		}
		for (File child : dirList) {
			if (child.isDirectory()) {
				File confFile = new File(child, CONF_PROP);
				if (confFile.exists()) {
					pluginList.add(confFile);
				}
			}
		}
		return pluginList;
	}

	public void startAndWait() {
		if (!pluginsList.isEmpty()) {
			for (Plugin plugin : pluginsList) {
				try {
					plugin.start();
				} catch (Exception e) {
					logger.warn(e.getMessage(), e);
				}
			}
		}

		if (threadList.isEmpty()) {
			logger.info("No plugin defined, the process will stop.");
			return;
		}

		logger.info("Start receiver threads.");
		es = Executors.newCachedThreadPool();
		for (Thread thread : threadList) {
			es.execute(thread);
		}
		es.shutdown();
		boolean finished = false;
		while (!finished) {
			try {
				finished = es.awaitTermination(1, TimeUnit.MINUTES);
			} catch (InterruptedException e) {
				logger.error(e.getMessage(), e);
			}

		}
	}

	public void shutdown() {
		logger.info("Shutdow all threads and plugins.");
		if (es != null) {
			if (!receiverList.isEmpty()) {
				for (JmsReceiver receiver : receiverList) {
					receiver.stop();
				}
			}
			es.shutdown();
		}
		if (!pluginsList.isEmpty()) {
			for (Plugin plugin : pluginsList) {
				try {
					plugin.stop();
				} catch (Exception e) {
					logger.warn(e.getMessage(), e);
				}
			}
		}

	}

	public Set<HomeElement> getHomeElementSet() {
		return eltsSet;
	}

	public QueueParameter getQueue(String queueName) {
		return queueMap.get(queueName);
	}

	public static void main(String[] args) {
		final Server server = Server.getInstance();
		if (server != null) {
			Runtime.getRuntime().addShutdownHook(new Thread() {
				public void run() {
					server.shutdown();
				}
			});
			server.startAndWait();
		}
	}

	public HomeElement getHomeElementById(String id) {
		if (id == null || id.trim().equals("")) {
			return null;
		}
		Set<HomeElement> eltSet = getHomeElementSet();
		if (eltSet.isEmpty()) {
			return null;
		}
		for (HomeElement elt : eltSet) {
			if (id.equals(elt.getId())) {
				return elt;
			}
		}
		return null;
	}

	public void saveValue(HomeElement homeElt, Message msg) throws HomeAutomationException {
		if (homeElt == null || msg == null) {
			return;
		}
		if (homeElt instanceof GenericSensor) {
			try {
				((GenericSensor) homeElt).setValue(JSONHelper.getDoubleValue(msg));
			} catch (ParseException e) {
				logger.debug(e.getMessage(), e);
			}
		}
		HomeElementDbMapper mapper = homeElt.getDbMapper();
		if (mapper == null) {
			return;
		}
		Connection connection = DBUtil.getConnection();
		try {
			mapper.saveValue(homeElt, msg, connection);
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

	public void validateSensor(GenericSensor sensor, boolean isNew) throws HomeAutomationException {
		// check if element are empty
		String id = sensor.getId();
		if (id == null || "".equals(id.trim())) {
			throw new HomeAutomationException("Id must not be an empty string.");
		}
		if (sensor.getLabel() == null || "".equals(sensor.getLabel().trim())) {
			throw new HomeAutomationException("Label must not be an empty string.");
		}

		// check if id is unique
		if (isNew) {
			Set<HomeElement> eltSet = getHomeElementSet();
			if (eltSet != null && !eltSet.isEmpty()) {
				for (HomeElement elt : eltSet) {
					if (id.equals(elt.getId())) {
						throw new HomeAutomationException("Id '" + id + "' already exist in the configuration.");
					}
				}
			}
		}
	}

	public synchronized void saveSensor(GenericSensor sensor) throws HomeAutomationException {
		File dirFile = new File(getElementsDir(), sensor.getId());
		if (!dirFile.exists() && !dirFile.mkdirs()) {
			throw new HomeAutomationException("Cannot create configuration directory '" + dirFile.getAbsolutePath() + "'");
		}
		File confFile = new File(dirFile, CONF_PROP);
		sensor.setConfigDirectory(dirFile);
		HomeElementHelper.saveConf(sensor, confFile);
		eltsSet.remove(sensor);
		eltsSet.add(sensor);
	}

	public void deleteHomeElementById(String eltId) throws HomeAutomationException {
		File dirFile = new File(getElementsDir(), eltId);
		try {
			FileUtils.deleteDirectory(dirFile);
			eltsSet.remove(getHomeElementById(eltId));
		} catch (IOException e) {
			throw new HomeAutomationException("Cannot delete configuration directory '" + dirFile.getAbsolutePath() + "' : "+e.getMessage());
		}
	}
}
