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
