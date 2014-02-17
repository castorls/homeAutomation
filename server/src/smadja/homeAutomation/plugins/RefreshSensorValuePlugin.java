package smadja.homeAutomation.plugins;

import java.io.File;
import java.util.List;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import smadja.homeAutomation.model.GenericSensor;
import smadja.homeAutomation.model.HomeElement;
import smadja.homeAutomation.model.JmsHelper;
import smadja.homeAutomation.model.Message;
import smadja.homeAutomation.server.Plugin;
import smadja.homeAutomation.server.Server;
import smadja.homeAutomation.utils.PluginHelper;

public class RefreshSensorValuePlugin extends Plugin {

	private static Logger logger = Logger.getLogger(RefreshSensorValuePlugin.class);
	private long refreshDelay = 60 * 1000L;
	private Timer timer = null;

	public RefreshSensorValuePlugin(String id, Server server) {
		super(id, server);
	}

	@Override
	public boolean onMessage(Message msg, boolean shouldAcknowledge) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void init(File pluginDirectory, Properties confProp) {
		String refreshValue = confProp.getProperty("refreshDelay");
		this.refreshDelay = Long.parseLong(refreshValue);
	}

	@Override
	public void start() {
		final Server server = getServer();
		Timer timer = new Timer();
		final long expiration = this.refreshDelay + 1000L;
		timer.scheduleAtFixedRate(new TimerTask() {
			public void run() {
				List<HomeElement> eltList = PluginHelper.filterElementList(server.getHomeElementList(), GenericSensor.class);
				if (eltList.isEmpty()) {
					return;
				}
				logger.info("Refresh sensor value");
				for (HomeElement elt : eltList) {
					JmsHelper.sendHomeMessage(server.getQueue(elt.getQueue()), elt.getId(), "refresh", null, expiration);
				}
			}
		}, 1000L, refreshDelay);
	}

	@Override
	public void stop() {
		if (timer != null) {
			timer.cancel();
		}
	}

}
