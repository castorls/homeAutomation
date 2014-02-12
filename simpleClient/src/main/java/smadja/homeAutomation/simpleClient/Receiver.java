package smadja.homeAutomation.simpleClient;

import java.io.IOException;
import java.security.Security;
import java.util.Properties;

import javax.jms.BytesMessage;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.log4j.Logger;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import smadja.homeAutomation.model.MessageHelper;

import com.fasterxml.jackson.core.JsonProcessingException;

public class Receiver extends AbstractClient {

	private static Logger logger = Logger.getLogger(Receiver.class);

	protected String receiveQueue;
	protected long receiveTimeout;

	public static void main(String[] args) {
		Security.addProvider(new BouncyCastleProvider());
		Receiver app = new Receiver();
		if (app.init(app.getProps())) {
			app.doJob(args);
		}
	}

	@Override
	public boolean init(Properties props) {
		if (!super.init(props)) {
			return false;
		}
		receiveQueue = props.getProperty("receiveQueue");
		String receiveStr = props.getProperty("receiveTimeout");
		receiveTimeout = receiveStr == null ? 1000L : Integer.parseInt(receiveStr);
		return true;
	}

	@Override
	public void doJob(String[] args) {
		Connection connection = null;
		try {
			ConnectionFactory factory = new ActiveMQConnectionFactory("tcp://" + host + ":" + port);
			connection = factory.createConnection(user, password);
			Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
			Queue receiveDest = session.createQueue(receiveQueue);
			MessageConsumer consumer = session.createConsumer(receiveDest);
			connection.start();
			Message tempMsg = (Message) consumer.receive(receiveTimeout);
			while (tempMsg != null) {
				smadja.homeAutomation.model.Message homeMsg = null;
				if (tempMsg instanceof BytesMessage) {
					BytesMessage byteMessage = (BytesMessage) tempMsg;
					byte[] byteArr = new byte[(int) byteMessage.getBodyLength()];
					byteMessage.readBytes(byteArr);
					String msg = new String(byteArr, "UTF-8");
					try {
						homeMsg = MessageHelper.deserialize(msg);
					} catch (JsonProcessingException e) {
						logger.error(e.getMessage(), e);
					}
				}else if (tempMsg instanceof TextMessage) {
					TextMessage textMessage = (TextMessage) tempMsg;
					try {
						homeMsg = MessageHelper.deserialize(textMessage.getText());
					} catch (JsonProcessingException e) {
						logger.error(e.getMessage(), e);
					}
				}
				if (homeMsg == null) {
					logger.debug("Invalid message in queue --> ROLLBACK");
				} else {
					tempMsg.acknowledge();
					logger.info("Received : " + homeMsg.toString());
				}
				tempMsg = (Message) consumer.receive(receiveTimeout);
			}

		} catch (JMSException e) {
			logger.error(e.getMessage(), e);
		} catch (IOException e) {
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
