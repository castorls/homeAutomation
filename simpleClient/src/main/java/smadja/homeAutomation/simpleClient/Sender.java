package smadja.homeAutomation.simpleClient;

import java.security.Security;
import java.util.Date;
import java.util.Properties;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.log4j.Logger;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import smadja.homeAutomation.model.Message;
import smadja.homeAutomation.model.MessageHelper;

import com.fasterxml.jackson.core.JsonProcessingException;

public class Sender extends AbstractClient {

	private static Logger logger = Logger.getLogger(Sender.class);

	protected String destinationQueue;
	protected boolean isPersistent;

	public static void main(String[] args) {
		Security.addProvider(new BouncyCastleProvider());
		Sender app = new Sender();
		if (app.init(app.getProps())) {
			app.doJob(args);
		}
	}

	@Override
	public boolean init(Properties props) {
		if (!super.init(props)) {
			return false;
		}
		destinationQueue = props.getProperty("destinationQueue");
		String persistentStr = props.getProperty("isPersistent");
		isPersistent = persistentStr == null ? true : Boolean.parseBoolean(persistentStr);
		return true;
	}

	public void doJob(String[] args) {
		Connection connection = null;
		try {
			ConnectionFactory factory = new ActiveMQConnectionFactory("tcp://" + host + ":" + port);

			connection = factory.createConnection(user, password);
			connection.start();
			Session session = connection.createSession(true, Session.CLIENT_ACKNOWLEDGE);
			Destination dest = session.createQueue(destinationQueue);
			MessageProducer producer = session.createProducer(dest);
			if (isPersistent) {
				producer.setDeliveryMode(DeliveryMode.PERSISTENT);
			} else {
				producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
			}
			Message msg = new Message();
			msg.setContent(args[0]);
			msg.setEventDate(new Date());
			msg.setEmitter(emitter);
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
