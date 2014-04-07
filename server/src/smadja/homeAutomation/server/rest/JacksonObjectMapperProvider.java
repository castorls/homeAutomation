package smadja.homeAutomation.server.rest;

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.databind.ObjectMapper;

@Provider
public class JacksonObjectMapperProvider implements ContextResolver<ObjectMapper> {
	@Override
	public ObjectMapper getContext(Class<?> type) {
		final ObjectMapper result = new ObjectMapper();
		/*
		SimpleModule module = new SimpleModule(getClass().getName(), new Version(1, 0, 0, null, null, null))
			.addSerializer(GenericSensorMapper.class, new GenericSensorMapperSerializer())
			.addDeserializer(GenericSensorMapper.class, new GenericSensorMapperDeserializer());
		result.registerModule(module);*/
		return result;
	}
}
