package smadja.homeAutomation.plugins.ics;

import java.net.URL;

public class ICSConfiguration {

	private URL url;
	private String user;
	private String password;

	public ICSConfiguration(URL url, String user, String password) {
		super();
		this.url = url;
		this.user = user;
		this.password = password;
	}

	public URL getUrl() {
		return url;
	}

	public void setUrl(URL url) {
		this.url = url;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
