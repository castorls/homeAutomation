package smadja.homeAutomation.model;

public class QueueReceiverParameter extends Parameter {

	protected String receiveQueue;
	protected long receiveTimeout;

	public QueueReceiverParameter(String user, String emitter, String password, String host, int port, String receiveQueue, long receiveTimeout) {
		super(user, emitter, password, host, port);
		this.receiveQueue = receiveQueue;
		this.receiveTimeout = receiveTimeout;
	}

	public QueueReceiverParameter(Parameter param) {
		super(param);
	}
	
	public QueueReceiverParameter(QueueReceiverParameter param){
		super(param);
		this.receiveQueue = param.getReceiveQueue();
		this.receiveTimeout = param.getReceiveTimeout();
	}

	public String getReceiveQueue() {
		return receiveQueue;
	}

	public void setReceiveQueue(String receiveQueue) {
		this.receiveQueue = receiveQueue;
	}

	public long getReceiveTimeout() {
		return receiveTimeout;
	}

	public void setReceiveTimeout(long receiveTimeout) {
		this.receiveTimeout = receiveTimeout;
	}

}
