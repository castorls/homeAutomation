package smadja.homeAutomation.server.rest;

import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.server.ResourceConfig;

@ApplicationPath("rest")
public class MainResourceConfig extends ResourceConfig {

	public MainResourceConfig() {
        packages("smadja.homeAutomation.server.rest");
        register(new JacksonObjectMapperProvider());
    }
	


}
