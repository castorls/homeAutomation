package smadja.homeAutomation.model;

import java.util.Date;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.log4j.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;

public class JmsHelper {
	private static Logger logger = Logger.getLogger(JmsHelper.class);

	public static MessageProducer initQueueProducer(Session session, QueueParameter queueParam) {
		try {
			Destination dest = session.createQueue(queueParam.getDestinationQueue());
			MessageProducer producer = session.createProducer(dest);
			if (queueParam.isPersistent()) {
				producer.setDeliveryMode(DeliveryMode.PERSISTENT);
			} else {
				producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
			}
			return producer;
		} catch (JMSException e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}

	public static Connection initConnection(Parameter param) {
		Connection connection = null;
		try {
			ConnectionFactory factory = new ActiveMQConnectionFactory("tcp://" + param.getHost() + ":" + param.getPort());

			connection = factory.createConnection(param.getUser(), param.getPassword());
			return connection;
		} catch (JMSException e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}

	public static MessageConsumer initQueueReceiver(Session session, QueueReceiverParameter parameter) {
		try {
			Queue receiveDest = session.createQueue(parameter.getReceiveQueue());
			MessageConsumer consumer = session.createConsumer(receiveDest);
			return consumer;
		} catch (JMSException e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}
	
	public static String sendHomeMessage(Parameter parameter, String targetElement, String content, String correlationId, long expiration) {
		Message msg = new Message();
		msg.setContent(content);
		msg.setEventDate(new Date());
		msg.setEmitter(parameter.getEmitter());
		msg.setTargetElement(targetElement);
		return sendHomeMessage(parameter, targetElement, msg, correlationId, expiration);
	}
	
	public static String sendHomeMessage(Parameter parameter, String targetElement, Message msg, String correlationId, long expiration) {
		Connection connection = null;
		String msgId = null;
		try {
			connection = JmsHelper.initConnection(parameter);
			Session session = connection.createSession(true, Session.CLIENT_ACKNOWLEDGE);
			MessageProducer producer = JmsHelper.initQueueProducer(session, (QueueParameter) parameter);
			connection.start();			
			TextMessage jmsMsg = session.createTextMessage();
			jmsMsg.setJMSCorrelationID(correlationId);
			jmsMsg.setText(MessageHelper.serialize(msg));
			if(expiration > 0){
				jmsMsg.setJMSExpiration(System.currentTimeMillis() + expiration);
			}
			producer.send(jmsMsg);			
			session.commit();
			msgId = jmsMsg.getJMSMessageID();
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
		return msgId;
	}

}
