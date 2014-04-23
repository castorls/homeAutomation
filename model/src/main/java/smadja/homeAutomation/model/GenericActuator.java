package smadja.homeAutomation.model;

public class GenericActuator extends HomeElement implements Comparable<HomeElement> {
	
	public GenericActuator(){
		super();
	}
	
	public GenericActuator(GenericActuator other){
		super(other);
	}
	
	@Override
	public boolean shouldSendAction(Double action) {
		Double val = getValue();
		if(val == null){
			return true;
		}
		if(action == null){
			action = Double.valueOf(0);
		}
	
		if(val.doubleValue() == action.doubleValue()){
			return false;
		}
		return true;
	}

	@Override
	public void setLastedMessageId(Double action, String correlationId) {
		if(action != null){
			setValue(action);
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
