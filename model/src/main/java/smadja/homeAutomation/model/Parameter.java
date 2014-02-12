package smadja.homeAutomation.model;

public class Parameter {
	protected String user;
	protected String emitter;
	protected String password;
	protected String host;
	protected int port;

	public Parameter(String user, String emitter, String password, String host, int port) {
		super();
		this.user = user;
		this.emitter = emitter;
		this.password = password;
		this.host = host;
		this.port = port;
	}

	public Parameter(Parameter param) {
		if (param == null) {
			throw new IllegalStateException("Param should not be null");
		}
		this.user = param.getUser();
		this.emitter = param.getEmitter();
		this.password = param.getPassword();
		this.host = param.getHost();
		this.port = param.getPort();
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getEmitter() {
		return emitter;
	}

	public void setEmitter(String emitter) {
		this.emitter = emitter;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

}
