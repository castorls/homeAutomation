package smadja.homeAutomation.model;

import java.io.IOException;
import java.util.Date;

import junit.framework.TestCase;

import com.fasterxml.jackson.core.JsonProcessingException;

public class MessageHelperTest extends TestCase {

	public void testSerialization(){
		Message msg = new Message();
		String content = "The content to set";
		String emitter = "The emitter";
		Date eventDate = new Date(System.currentTimeMillis() - 1000L);
		
		msg.setContent(content);
		msg.setEmitter(emitter);
		msg.setEventDate(eventDate);
		msg.setTransientFlag(true);
		String value = null;
		try {
			value = JSONHelper.serialize(msg);
		} catch (JsonProcessingException e) {
			assertNotSame(null,e);
		}
		assertNotSame(null, value);
		try {
			Message msg2 = JSONHelper.deserialize(value);
			assertNotSame("null", msg2);
			assertEquals(content, msg2.getContent());
			assertEquals(emitter, msg2.getEmitter());
			assertEquals(eventDate, msg2.getEventDate());
			assertEquals(true, msg2.isTransientFlag());
		} catch (IOException e) {
			assertNotSame(null,e);
		}
	}
	
}
