package smadja.homeAutomation.server.rest;

import java.io.IOException;

import org.apache.log4j.Logger;

import smadja.homeAutomation.model.mapper.GenericSensorMapper;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

public class GenericSensorMapperDeserializer extends JsonDeserializer<GenericSensorMapper> {

	private static Logger logger = Logger.getLogger(GenericSensorMapperDeserializer.class);
	
		@Override
		public GenericSensorMapper deserialize(JsonParser jsonParser, DeserializationContext ctx) throws IOException, JsonProcessingException {
			 ObjectCodec oc = jsonParser.getCodec();
		        JsonNode node = oc.readTree(jsonParser);
		        String classname = node.get("type").textValue();
		        if( classname.equals(null) || "null".equals(classname)){
		        	return null;
		        }
		        try {
					return (GenericSensorMapper) Class.forName(classname).newInstance();
				} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
					logger.warn(e.getMessage(), e);
					throw new IOException(e);
				}
		}
}
