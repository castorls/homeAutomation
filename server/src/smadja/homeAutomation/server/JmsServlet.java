package smadja.homeAutomation.server;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

public class JmsServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4686508337397891295L;

	@Override
	public void init(ServletConfig config) throws ServletException {
		Server server = Server.getInstance();
		server.startAndWait();
	}

}
