package smadja.homeAutomation.model;

import java.util.Date;

public class HistoryData {

	private Date date;
	private Double value;
	private String valueAsString;

	public HistoryData() {
		super();
		this.date = null;
		this.value = null;
		this.valueAsString = null;
	}
	
	public HistoryData(Date date, Double value) {
		super();
		this.date = date;
		this.value = value;
		this.valueAsString = null;
	}

	public HistoryData(Date date, String value) {
		super();
		this.date = date;
		this.value = null;
		this.valueAsString = value;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Double getValue() {
		return value;
	}

	public void setValue(Double value) {
		this.value = value;
	}

	public String getValueAsString() {
		return valueAsString;
	}

	public void setValueAsString(String valueAsString) {
		this.valueAsString = valueAsString;
	}

}
