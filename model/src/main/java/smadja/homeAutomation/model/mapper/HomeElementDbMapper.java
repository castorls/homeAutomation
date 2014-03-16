package smadja.homeAutomation.model.mapper;

import java.sql.Connection;

import smadja.homeAutomation.model.HomeAutomationException;
import smadja.homeAutomation.model.HomeElement;
import smadja.homeAutomation.model.Message;

public interface HomeElementDbMapper {

	void saveValue(HomeElement homeElt, Message msg, Connection connection)throws HomeAutomationException;

}
