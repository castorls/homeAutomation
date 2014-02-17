package smadja.homeAutomation.simulation;

import java.security.Security;
import java.text.DecimalFormat;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import smadja.homeAutomation.model.Message;
import smadja.homeAutomation.simpleClient.GenericSensorClient;

public class Thermometer extends GenericSensorClient {


	public static void main(String[] args) {
		Security.addProvider(new BouncyCastleProvider());
		Thermometer app = new Thermometer();
		app.doJob(args);
	}
	
	@Override
	public Message generateValueMessage(Message msg) {
		Message returnedMsg = getEmptyReturnMessage(msg);
		returnedMsg.setTransientFlag(false);
		double alea = Math.sin((msg.getEventDate().getTime() / (15 * 1000L)));
		DecimalFormat numberFormat = new DecimalFormat("0.##");
		returnedMsg.setContent(numberFormat.format(25 + 5 * alea));
		return returnedMsg;
	}
	

}
