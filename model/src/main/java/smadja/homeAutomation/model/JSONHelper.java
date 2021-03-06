package smadja.homeAutomation.model;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JSONHelper {
	private static ObjectMapper mapper = new ObjectMapper();

	public static ObjectMapper getMapper() {
		return mapper;
	}

	public static void setMapper(ObjectMapper mapper) {
		JSONHelper.mapper = mapper;
	}

	public static String serialize(Message msg) throws JsonProcessingException {
		return mapper.writeValueAsString(msg);
	}
	
	public static String serialize(Object obj) throws JsonProcessingException {
		return mapper.writeValueAsString(obj);
	}

	public static Message deserialize(String msg) throws JsonParseException, JsonMappingException, IOException {
		return mapper.readValue(msg, Message.class);
	}

	public static double getDoubleValue(Message msg) throws ParseException {
		DecimalFormat numberFormat = new DecimalFormat("0.##");
		double doubleValue = numberFormat.parse(msg.getContent()).doubleValue();
		return doubleValue;
	}
	
	public static String getValue(Message msg){
		return msg.getContent();
	}
}
