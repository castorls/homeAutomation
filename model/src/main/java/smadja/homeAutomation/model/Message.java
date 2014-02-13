package smadja.homeAutomation.model;

import java.util.Date;

public class Message {

	private String emitter;
	private String content;
	private Date eventDate;
	private javax.jms.Message jmsMessage;
	private boolean transientFlag = false;

	public boolean isTransientFlag() {
		return transientFlag;
	}

	public void setTransientFlag(boolean transientFlag) {
		this.transientFlag = transientFlag;
	}

	public String getEmitter() {
		return emitter;
	}

	public void setEmitter(String emitter) {
		this.emitter = emitter;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Date getEventDate() {
		return eventDate;
	}

	public void setEventDate(Date eventDate) {
		this.eventDate = eventDate;
	}

	@Override
	public String toString() {
		return "Message [emitter=" + emitter + ", content=" + content + ", eventDate=" + eventDate + ", jmsMessage=" + jmsMessage + ", transientFlag=" + transientFlag + "]";
	}

	public javax.jms.Message getJmsMessage() {
		return jmsMessage;
	}

	public void setJmsMessage(javax.jms.Message msg) {
		this.jmsMessage = msg;

	}

}
