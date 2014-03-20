package smadja.homeAutomation.model;

public class GenericActuator extends HomeElement {
	
	private String state = null;

	public GenericActuator(){
		super();
	}
	
	public GenericActuator(GenericActuator other){
		super(other);
		this.state = other.state == null ? null : new String(other.state);
	}
	
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
