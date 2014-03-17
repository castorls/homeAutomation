package smadja.homeAutomation.model;

public class QueueParameter extends Parameter {

	protected String destinationQueue;
	protected boolean isPersistent;

	public QueueParameter(String user, String emitter, String password, String host, int port, String destinationQueue, boolean isPersistent) {
		super(user, emitter, password, host, port);
		this.destinationQueue = destinationQueue;
		this.isPersistent = isPersistent;
	}
	
	public QueueParameter(Parameter param){
		super(param);
	}
	
	public QueueParameter(QueueParameter param){
		super(param);
		this.destinationQueue = param.getDestinationQueue();
		this.isPersistent = param.isPersistent();
	}

	public String getDestinationQueue() {
		return destinationQueue;
	}

	public void setDestinationQueue(String destinationQueue) {
		this.destinationQueue = destinationQueue;
	}

	public boolean isPersistent() {
		return isPersistent;
	}

	public void setPersistent(boolean isPersistent) {
		this.isPersistent = isPersistent;
	}

}
