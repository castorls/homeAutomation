package smadja.homeAutomation.plugins.rules;

import org.apache.log4j.Logger;

import smadja.homeAutomation.model.HomeAutomationException;
import smadja.homeAutomation.model.HomeElement;
import smadja.homeAutomation.model.JmsHelper;
import smadja.homeAutomation.model.Message;
import smadja.homeAutomation.server.Server;

public abstract class Rule {

	private static Logger logger = Logger.getLogger(Rule.class); 
	
	public abstract void process(Message msg, Server server) throws HomeAutomationException;
	
	protected void sendActionMessage(Message msg, String action, Server server, long expiration ){
		String targetElement = msg.getTargetElement();
		HomeElement elt = server.getHomeElementById(targetElement);
		if (elt != null && !elt.shouldSendAction(action)) {
			logger.warn("Home element '" + targetElement + "' has denied action '" + action + "'");
			return;
		}
		String id = JmsHelper.sendHomeMessage(server.getQueue(Server.ACTION_QUEUE), targetElement, msg, null, expiration);
		if (elt != null) {
			elt.setLastedMessageId(action, id);
		}
	}

}
