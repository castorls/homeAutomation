package admin.console.listener;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import server.ServerClient;

public class SessionListener implements HttpSessionListener {

	@Override
	public void sessionCreated(HttpSessionEvent session) {
		ServerClient server = ServerClient.getInstance();
		int nbLevel = server.getLevelCount();
		List<Level> levels = new ArrayList<Level>();
		for (int i = 0; i < nbLevel; i++) {
			Level level = new Level(i);
			levels.add(level);
		}
		session.getSession().setAttribute("levels", levels);
	}

	public class Level {
		String label;
		int value;

		public Level(int index) {
			label = "Niveau " + index;
			value = index;
		}

		public String getLabel() {
			return label;
		}

		public void setLabel(String label) {
			this.label = label;
		}

		public int getValue() {
			return value;
		}

		public void setValue(int value) {
			this.value = value;
		}

	}

	@Override
	public void sessionDestroyed(HttpSessionEvent arg0) {
		// nothing to do
	}
}
