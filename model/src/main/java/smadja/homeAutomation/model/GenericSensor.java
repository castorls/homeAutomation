package smadja.homeAutomation.model;

public class GenericSensor extends HomeElement {
	private Double value;

	public GenericSensor(){
		super();
	}
	
	public GenericSensor(GenericSensor other){
		super(other);
		this.value = other.value == null ? null : new Double(other.value);
	}
	
	public Double getValue() {
		return value;
	}

	public void setValue(Double value) {
		this.value = value;
	}
}
