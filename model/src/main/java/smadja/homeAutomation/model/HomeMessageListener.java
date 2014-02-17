package smadja.homeAutomation.model;

public interface HomeMessageListener {

	boolean onMessage(Message msg, boolean shouldAcknowledge);
	
}
