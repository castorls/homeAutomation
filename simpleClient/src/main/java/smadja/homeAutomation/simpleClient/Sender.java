package smadja.homeAutomation.simpleClient;

import java.security.Security;
import java.util.Date;
import java.util.Properties;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.log4j.Logger;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import smadja.homeAutomation.model.JmsHelper;
import smadja.homeAutomation.model.Message;
import smadja.homeAutomation.model.MessageHelper;
import smadja.homeAutomation.model.Parameter;
import smadja.homeAutomation.model.QueueParameter;

import com.fasterxml.jackson.core.JsonProcessingException;

public class Sender extends AbstractClient {

	private static Logger logger = Logger.getLogger(Sender.class);


	public static void main(String[] args) {
		Security.addProvider(new BouncyCastleProvider());
		Sender app = new Sender();
		app.doJob(args);
	}


	@Override
	public Parameter init(Properties props) {
		Parameter parameter = super.init(props); 
		if (parameter == null) {
			return null;
		}
		QueueParameter queueParam = new QueueParameter(parameter);

		queueParam.setDestinationQueue(props.getProperty("destinationQueue"));
		String persistentStr= props.getProperty("isPersistent");
		queueParam.setPersistent(persistentStr == null ? true : Boolean.parseBoolean(persistentStr));
		return queueParam;
	}

	public void doInternalJob(Parameter parameter, String[] args) {
		Connection connection = null;
		try {
			connection = JmsHelper.initConnection(parameter);			
			Session session = connection.createSession(true, Session.CLIENT_ACKNOWLEDGE);
			MessageProducer producer = JmsHelper.initQueueProducer(session, (QueueParameter) parameter);
			connection.start();
			Message msg = new Message();
			msg.setContent(args[0]);
			msg.setEventDate(new Date());
			msg.setEmitter(parameter.getEmitter());
			TextMessage jmsMsg = session.createTextMessage();
			jmsMsg.setText(MessageHelper.serialize(msg));
			producer.send(jmsMsg);
			session.commit();
		} catch (JMSException e) {
			logger.error(e.getMessage(), e);
		} catch (JsonProcessingException e) {
			logger.error(e.getMessage(), e);
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (JMSException e) {
					// nothing to do
				}
			}
		}

	}
}
