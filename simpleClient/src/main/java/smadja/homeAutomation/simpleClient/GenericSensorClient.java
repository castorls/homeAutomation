package smadja.homeAutomation.simpleClient;

import java.util.Date;
import java.util.Properties;

import javax.jms.JMSException;

import org.apache.log4j.Logger;

import smadja.homeAutomation.model.HomeMessageListener;
import smadja.homeAutomation.model.JmsHelper;
import smadja.homeAutomation.model.JmsReceiver;
import smadja.homeAutomation.model.Message;
import smadja.homeAutomation.model.Parameter;
import smadja.homeAutomation.model.QueueParameter;
import smadja.homeAutomation.model.QueueReceiverParameter;

public class GenericSensorClient extends Receiver {

	private static Logger logger = Logger.getLogger(GenericSensorClient.class);
	private QueueParameter queueParam = null;

	@Override
	public Parameter init(Properties props) {
		Parameter parameter = super.init(props);
		if (parameter == null) {
			return null;
		}
		queueParam = new QueueParameter(parameter);

		queueParam.setDestinationQueue(props.getProperty("destinationQueue"));
		String persistentStr = props.getProperty("isPersistent");
		queueParam.setPersistent(persistentStr == null ? true : Boolean.parseBoolean(persistentStr));
		return super.init(props);
	}
	
	@Override
	public void doInternalJob(Parameter parameter, String[] args) {
		//clear queue
		HomeMessageListener[] listeners = { new HomeMessageListener() {			
			@Override
			public boolean onMessage(Message msg, boolean shouldAcknowledge) {
				return true;
			}
		} };
		JmsReceiver receiver = new JmsReceiver((QueueReceiverParameter) parameter, listeners, false);
		Thread t = new Thread(receiver);
		t.run();
		//send connect message
		Message msg = getEmptyReturnMessage(null);
		msg.setTransientFlag(false);
		msg.setContent("Connect");
		JmsHelper.sendHomeMessage((Parameter) queueParam, "Core", msg, null, 5000);
		super.doInternalJob(parameter, args);
	}

	@Override
	public boolean onMessage(Message msg, boolean shouldAcknowledge) {
		logger.debug("Received message : "+msg.toString());
		if (queueParam == null) {
			return false;
		}
		if(!getId().equals(msg.getTargetElement())){
			return false;
		}
		Message returnedMessage = generateValueMessage(msg);
		if (returnedMessage == null) {
			return false;
		}
		try {
			logger.debug("resent response message : "+returnedMessage.toString());
			JmsHelper.sendHomeMessage((Parameter) queueParam, msg.getEmitter(), returnedMessage, msg.getJmsMessage().getJMSMessageID(), getDefaultExpiration());
		} catch (JMSException e) {
			logger.warn(e.getMessage(), e);
			return false;
		}
		return true;
	}

	public long getDefaultExpiration() {
		return 60*10*1000L;
	}

	protected String getId() {
		if(queueParam == null){
			return null;
		}
		return queueParam.getEmitter();
	}

	public Message generateValueMessage(Message msg) {
		return null;
	}

	protected Message getEmptyReturnMessage(Message msg) {
		Message returnedMsg = new Message();
		returnedMsg.setEmitter(getId());
		returnedMsg.setEventDate(new Date());
		returnedMsg.setTargetElement(msg == null ? null : msg.getEmitter());
		return returnedMsg;
	}
}
