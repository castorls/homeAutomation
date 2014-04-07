package smadja.homeAutomation.model;

public class GenericActuator extends HomeElement implements Comparable<HomeElement> {
	
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + getClass().getName().hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		return true;
	}
}
