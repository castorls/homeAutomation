package smadja.homeAutomation.simpleClient;

import java.security.Security;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import smadja.homeAutomation.model.HomeMessageListener;
import smadja.homeAutomation.model.JmsReceiver;
import smadja.homeAutomation.model.Parameter;
import smadja.homeAutomation.model.QueueReceiverParameter;

public class Receiver extends AbstractClient implements HomeMessageListener {

	private static Logger logger = Logger.getLogger(Receiver.class);

	public static void main(String[] args) {
		Security.addProvider(new BouncyCastleProvider());
		Receiver app = new Receiver();
		app.doJob(args);
	}

	@Override
	public Parameter init(Properties props) {
		Parameter parameter = super.init(props);
		if (parameter == null) {
			return null;
		}
		QueueReceiverParameter queueParam = new QueueReceiverParameter(parameter);
		queueParam.setReceiveQueue(props.getProperty("receiveQueue"));
		String receiveStr = props.getProperty("receiveTimeout");
		queueParam.setReceiveTimeout(receiveStr == null ? 1000L : Integer.parseInt(receiveStr));
		return queueParam;
	}

	@Override
	public void doInternalJob(Parameter parameter, String[] args) {
		HomeMessageListener[] listeners = { this };
		JmsReceiver receiver = new JmsReceiver((QueueReceiverParameter) parameter, listeners, (args.length > 0 && Boolean.parseBoolean(args[0]))?true:false);
		Thread t = new Thread(receiver);
		t.start();
	}

	@Override
	public void onMessage(smadja.homeAutomation.model.Message msg) {
		logger.info("Received : " + msg.toString());
	}

}
