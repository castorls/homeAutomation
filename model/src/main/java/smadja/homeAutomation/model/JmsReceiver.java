package smadja.homeAutomation.model;

import java.io.IOException;

import javax.jms.BytesMessage;
import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;

public class JmsReceiver implements Runnable {

	private static Logger logger = Logger.getLogger(JmsReceiver.class);

	private boolean shouldContinue = false;
	private QueueReceiverParameter parameter;
	private HomeMessageListener[] listeners;

	public JmsReceiver(QueueReceiverParameter parameter, HomeMessageListener[] listeners, boolean doLoop) {
		this.shouldContinue = doLoop;
		this.parameter = parameter;
		this.listeners = listeners;
	}

	public synchronized void stop() {
		shouldContinue = false;
	}

	@Override
	public void run() {
		Connection connection = null;
		try {
			connection = JmsHelper.initConnection(parameter);
			Session session = connection.createSession(true, Session.CLIENT_ACKNOWLEDGE);
			MessageConsumer consumer = JmsHelper.initQueueReceiver(session, parameter);
			connection.start();
			Message tempMsg = (Message) consumer.receive(parameter.getReceiveTimeout());
			while (tempMsg != null || shouldContinue) {
				if (tempMsg == null) {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						// nothing to do
					}
					tempMsg = (Message) consumer.receive(parameter.getReceiveTimeout());
					continue;
				}
				smadja.homeAutomation.model.Message homeMsg = null;
				if (tempMsg instanceof BytesMessage) {
					BytesMessage byteMessage = (BytesMessage) tempMsg;
					byte[] byteArr = new byte[(int) byteMessage.getBodyLength()];
					byteMessage.readBytes(byteArr);
					String msg = new String(byteArr, "UTF-8");
					try {
						homeMsg = MessageHelper.deserialize(msg);
						homeMsg.setJmsMessage(tempMsg);
					} catch (JsonProcessingException e) {
						logger.error(e.getMessage(), e);
					}
				} else if (tempMsg instanceof TextMessage) {
					TextMessage textMessage = (TextMessage) tempMsg;
					try {
						homeMsg = MessageHelper.deserialize(textMessage.getText());
						homeMsg.setJmsMessage(tempMsg);
					} catch (JsonProcessingException e) {
						logger.error(e.getMessage(), e);
					}
				}
				if (homeMsg == null) {
					logger.debug("Invalid message in queue --> ROLLBACK");
				} else {
					logger.debug("Received : " + homeMsg.toString());
					boolean shouldAcknowlegde = false;
					for (HomeMessageListener listener : listeners) {
						try {
							shouldAcknowlegde = listener.onMessage(homeMsg, shouldAcknowlegde);
						} catch (Exception e) {
							logger.error(e.getMessage(), e);
						}
					}
					if (shouldAcknowlegde) {
						tempMsg.acknowledge();
					}
					session.commit();
				}
				tempMsg = (Message) consumer.receive(parameter.getReceiveTimeout());
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
