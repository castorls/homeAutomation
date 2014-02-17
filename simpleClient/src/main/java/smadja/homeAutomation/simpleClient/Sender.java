package smadja.homeAutomation.simpleClient;

import java.security.Security;
import java.util.Properties;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import smadja.homeAutomation.model.JmsHelper;
import smadja.homeAutomation.model.Parameter;
import smadja.homeAutomation.model.QueueParameter;

public class Sender extends AbstractClient {

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
		String persistentStr = props.getProperty("isPersistent");
		queueParam.setPersistent(persistentStr == null ? true : Boolean.parseBoolean(persistentStr));
		return queueParam;
	}

	public void doInternalJob(Parameter parameter, String[] args) {
		JmsHelper.sendHomeMessage(parameter, null, args[0], (args.length > 1 ? args[1] : null), 60*10*1000L);
	}

}