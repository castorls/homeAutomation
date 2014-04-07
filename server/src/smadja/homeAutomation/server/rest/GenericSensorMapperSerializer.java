package smadja.homeAutomation.server.rest;

import java.io.IOException;

import smadja.homeAutomation.model.mapper.GenericSensorMapper;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class GenericSensorMapperSerializer extends JsonSerializer<GenericSensorMapper> {

		@Override
		public void serialize(GenericSensorMapper value, JsonGenerator jgen,
		        SerializerProvider provider) throws IOException,
		        JsonProcessingException {
		    jgen.writeStartObject();
		    jgen.writeStringField("type", value == null ? null : value.getClass().getName());
		    jgen.writeEndObject();
		}
}
