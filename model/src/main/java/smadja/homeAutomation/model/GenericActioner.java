package smadja.homeAutomation.model;

public class GenericActioner extends HomeElement {
	
	private String state = null;

	@Override
	public boolean shouldSendAction(String action) {
		if(state == null){
			return true;
		}
		if(state.equals(action)){
			return false;
		}
		return true;
	}

	@Override
	public void setLastedMessageId(String action, String correlationId) {
		if(action != null){
			state = action;
		}
	}
	
	
}
