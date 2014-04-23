package smadja.homeAutomation.plugins.rules;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import smadja.homeAutomation.model.HomeAutomationException;
import smadja.homeAutomation.model.Message;
import smadja.homeAutomation.model.JSONHelper;
import smadja.homeAutomation.server.Server;

import com.fasterxml.jackson.core.JsonProcessingException;

public class GenericRule extends Rule {
	private static Logger logger = Logger.getLogger(GenericRule.class);

	private Pattern emitterRegex = null;
	private Double thresholdValue = null;
	private String operator = null;
	private String sender = null;
	private Double action = null;
	private long defaultExpiration = 10 * 1000L;

	public enum Operators {
		LOWER, LOWER_OR_EQUALS, UPPER, UPPER_OR_EQUALS, EQUALS, DIFFERENT
	};

	public GenericRule(File ruleFile) throws HomeAutomationException {
		Properties prop = new Properties();
		try {
			prop.load(new InputStreamReader(new FileInputStream(ruleFile), "UTF-8"));
			emitterRegex = Pattern.compile(prop.getProperty("emitter"));
			thresholdValue = Double.parseDouble(prop.getProperty("threshold"));
			operator = prop.getProperty("operator");
			sender = prop.getProperty("sender");
			action = Double.parseDouble(prop.getProperty("action"));
			defaultExpiration = Long.parseLong(prop.getProperty("expiration"));
		} catch (IOException e) {
			logger.warn(e.getMessage(), e);
			throw new HomeAutomationException("Cannot initialize generic rule '" + ruleFile.getName() + "'");
		}

	}

	@Override
	public void process(Message msg, Server server) throws HomeAutomationException {
		String emitter = msg.getEmitter();
		if (emitterRegex.matcher(emitter).matches()) {
			try {
				Double value = JSONHelper.getDoubleValue(msg);
				if (operator.equalsIgnoreCase(Operators.EQUALS.name()) && thresholdValue.equals(value)) {
					sendMessageToSender(emitter, server, Operators.EQUALS, value);
				} else if (operator.equalsIgnoreCase(Operators.DIFFERENT.name()) && !thresholdValue.equals(value)) {
					sendMessageToSender(emitter, server, Operators.DIFFERENT, value);
				} else if (operator.equalsIgnoreCase(Operators.LOWER.name()) && value.doubleValue() < thresholdValue.doubleValue()) {
					sendMessageToSender(emitter, server, Operators.LOWER, value);
				} else if (operator.equalsIgnoreCase(Operators.LOWER_OR_EQUALS.name()) && value.doubleValue() <= thresholdValue.doubleValue()) {
					sendMessageToSender(emitter, server, Operators.LOWER_OR_EQUALS, value);
				} else if (operator.equalsIgnoreCase(Operators.UPPER.name()) && value.doubleValue() > thresholdValue.doubleValue()) {
					sendMessageToSender(emitter, server, Operators.UPPER, value);
				} else if (operator.equalsIgnoreCase(Operators.UPPER_OR_EQUALS.name()) && value.doubleValue() >= thresholdValue.doubleValue()) {
					sendMessageToSender(emitter, server, Operators.UPPER_OR_EQUALS, value);
				}
			} catch (ParseException e) {
				logger.trace("invalid value : " + e.getMessage());
			} catch (JsonProcessingException e2) {
				logger.warn("Cannot process the message : " + e2.getMessage());
			}
		} else {
			logger.trace("Emiter '" + emitter + "' doesn't match '" + emitterRegex.pattern() + "'");
		}

	}

	private void sendMessageToSender(String emitter, Server server, Operators operator, Double value) throws JsonProcessingException {
		logger.debug("Rule executed : send '" + action + "' to " + sender);
		Message returnedMsg = new Message();
		returnedMsg.setEmitter(emitter);
		returnedMsg.setEventDate(new Date());
		returnedMsg.setTargetElement(sender);
		Map<String, String> contentMap = new HashMap<String, String>();
		contentMap.put("ACTION", action == null ? "0" : action.toString());
		contentMap.put("OPERATOR", operator.name());
		contentMap.put("VALUE", value.toString());
		returnedMsg.setContent(JSONHelper.getMapper().writeValueAsString(contentMap));
		long expiration = System.currentTimeMillis() + defaultExpiration * 1000L;
		sendActionMessage(returnedMsg, action, server, expiration);
	}

}
