package smadja.homeAutomation.model;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.log4j.Logger;

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


}
