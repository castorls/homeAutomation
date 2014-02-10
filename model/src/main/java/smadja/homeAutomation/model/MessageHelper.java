package smadja.homeAutomation.model;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MessageHelper {
	private static ObjectMapper mapper = new ObjectMapper(); 
	
	public static String serialize(Message msg) throws JsonProcessingException{
		return mapper.writeValueAsString(msg);
	}
	
	public static Message deserialize(String msg) throws JsonParseException, JsonMappingException, IOException{
		return mapper.readValue(msg,  Message.class);
	}
}
