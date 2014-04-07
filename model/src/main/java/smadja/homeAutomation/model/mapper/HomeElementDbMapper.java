package smadja.homeAutomation.model.mapper;

import java.sql.Connection;
import java.util.List;

import smadja.homeAutomation.model.HistoryData;
import smadja.homeAutomation.model.HomeAutomationException;
import smadja.homeAutomation.model.HomeElement;
import smadja.homeAutomation.model.Message;


public interface HomeElementDbMapper {

	void saveValue(HomeElement homeElt, Message msg, Connection connection)throws HomeAutomationException;

	List<HistoryData> getHistoryData(HomeElement homeElt, Connection connection) throws HomeAutomationException;
}
