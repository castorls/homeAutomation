package smadja.homeAutomation.plugins;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Period;
import net.fortuna.ical4j.model.PeriodList;
import net.fortuna.ical4j.model.component.VEvent;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.log4j.Logger;

import smadja.homeAutomation.model.HomeAutomationException;
import smadja.homeAutomation.model.JmsHelper;
import smadja.homeAutomation.model.Message;
import smadja.homeAutomation.model.QueueParameter;
import smadja.homeAutomation.plugins.ics.ICSCache;
import smadja.homeAutomation.plugins.ics.ICSConfiguration;
import smadja.homeAutomation.server.Plugin;
import smadja.homeAutomation.server.Server;

public class ICSPlugin extends Plugin {

	private static Logger logger = Logger.getLogger(ICSPlugin.class);
	private long refreshDelay = 30 * 60 * 1000L;
	private Timer timer = null;
	private Timer minuteTimer = null;
	private List<ICSConfiguration> icsList = new ArrayList<ICSConfiguration>();
	private ICSCache cache = new ICSCache();

	public ICSPlugin(String id, Server server) {
		super(id, server);
	}

	@Override
	public boolean onMessage(Message msg, boolean shouldAcknowledge) {
		if (msg.getTargetElement().equals(this.getClass().getName())) {
			if (msg.getContent().contains("refreshContent")) {
				refreshFromICS();
				return true;
			}
		}
		return false;
	}

	@Override
	public void init(File pluginDirectory, Properties confProp) throws HomeAutomationException {
		String refreshValue = confProp.getProperty("refreshDelay");
		this.refreshDelay = Long.parseLong(refreshValue);
		for (Object key : confProp.keySet()) {
			String keyStr = (String) key;
			if (keyStr.startsWith("icsURL.")) {
				try {
					icsList.add(new ICSConfiguration(new URL(confProp.getProperty(keyStr)), confProp.getProperty(keyStr + ".user"), confProp.getProperty(keyStr + ".password")));
				} catch (MalformedURLException e) {
					logger.warn("Cannot create ICS configuration : " + e.getMessage(), e);
				}
			}
		}
	}

	@Override
	public void start() {
		super.start();
		timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			public void run() {
				refreshFromICS();
			}
		}, 1000L, refreshDelay);
		minuteTimer = new Timer();
		java.util.Calendar cal = java.util.Calendar.getInstance();
		minuteTimer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				logger.debug("Triggering minute timer");
				Date startDate = new Date();
				Date endDate = new Date(System.currentTimeMillis() + 59 * 1000L);
				{
					List<VEvent> eventList = cache.get(startDate, endDate, true);
					if (!eventList.isEmpty()) {
						for (VEvent event : eventList) {
							sendVEventMessage(event, true);
						}
					}
				}
				{
					List<VEvent> eventList = cache.get(startDate, endDate, false);
					if (!eventList.isEmpty()) {
						for (VEvent event : eventList) {
							sendVEventMessage(event, false);
						}
					}
				}
			}
		}, (55 - cal.get(java.util.Calendar.SECOND)) * 1000L , 60 * 1000L);
	}

	private void sendVEventMessage(VEvent event, boolean isStart) {
		Server server = getServer();
		long expiration = event.getEndDate().getDate().getTime() - event.getStartDate().getDate().getTime();
		String description = event.getDescription() == null ? null : event.getDescription().getValue();
		if (description != null && !"".equals(description.trim())) {
			logger.debug("Send "+(isStart?"start":"end")+" messages for " + description);
			String[] contentArray = description.split("\n");
			QueueParameter parameter = server.getQueue(Server.ACTION_QUEUE);
			parameter = new QueueParameter(parameter);
			parameter.setEmitter("ICSPlugin");
			for (String content : contentArray) {
				String[] elts = content.split(";");
				String target = elts[0];
				String action = "{trigger:"+(isStart?"start ":"end ")+",value:";
				if (elts.length > 1) {
					action += elts[1];
				}
				action += "}";
				JmsHelper.sendHomeMessage(parameter, target, action, null, expiration);
			}
		}
	}

	@Override
	public void stop() {
		super.stop();
		timer.cancel();
	}

	private void refreshFromICS() {
		logger.info("Refresh from ICS content");
		for (ICSConfiguration conf : icsList) {
			refreshFromConfiguration(conf);
		}
	}

	private void refreshFromConfiguration(ICSConfiguration conf) {
		URL url = conf.getUrl();
		HttpHost target = new HttpHost(url.getHost(), url.getPort(), url.getProtocol());
		CloseableHttpClient httpclient = null;
		if (conf.getUser() != null) {
			CredentialsProvider credsProvider = new BasicCredentialsProvider();
			credsProvider.setCredentials(new AuthScope(target.getHostName(), target.getPort()), new UsernamePasswordCredentials("username", "password"));
			httpclient = HttpClients.custom().setDefaultCredentialsProvider(credsProvider).build();
		} else {
			httpclient = HttpClients.custom().build();
		}
		try {
			// Create AuthCache instance
			AuthCache authCache = new BasicAuthCache();
			// Generate BASIC scheme object and add it to the local
			// auth cache
			BasicScheme basicAuth = new BasicScheme();
			authCache.put(target, basicAuth);

			// Add AuthCache to the execution context
			HttpClientContext localContext = HttpClientContext.create();
			localContext.setAuthCache(authCache);

			HttpGet httpget = new HttpGet(url.toURI());
			CloseableHttpResponse response = httpclient.execute(target, httpget, localContext);
			try {
				String cacheKey = conf.getUrl().toString() + "_" + conf.getUser();
				CalendarBuilder builder = new CalendarBuilder();
				Calendar calendar = builder.build(response.getEntity().getContent());
				DateTime start = new DateTime(new Date());
				DateTime end = new DateTime(new Date(System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000L));
				Period nextWeek = new Period(start, end);
				parseCalendar(calendar, cacheKey, nextWeek);
			} catch (IllegalStateException | ParserException e) {
				logger.warn("Cannot get ICS content from " + url.toString() + " : " + e.getMessage(), e);
			} finally {
				response.close();
			}
		} catch (URISyntaxException | IOException e) {
			logger.warn("Cannot get ICS content from " + url.toString() + " : " + e.getMessage(), e);
		} finally {
			try {
				httpclient.close();
			} catch (IOException e) {
				// nothing to do
			}
		}

	}

	private void parseCalendar(Calendar calendar, String cacheKey, Period nextWeek) {
		if (calendar.getComponents().isEmpty()) {
			cache.clear(cacheKey);
			return;
		}
		for (Iterator<?> iterator = calendar.getComponents().iterator(); iterator.hasNext();) {
			Object componentObj = iterator.next();
			if (componentObj instanceof VEvent) {
				VEvent event = (VEvent) componentObj;
				PeriodList periodList = event.calculateRecurrenceSet(nextWeek);
				if (periodList.isEmpty()) {
					continue;
				}
				for (Object periodObj : periodList) {
					Period period = (Period) periodObj;
					cache.add(cacheKey, period.getStart(), period.getEnd(), event);
				}
			}

		}
	}
}
